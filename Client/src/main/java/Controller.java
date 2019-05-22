import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Controller implements Initializable {
    @FXML
    Button downloadBtn;

    @FXML
    Button uploadBtn;

    @FXML
    ListView<String> clientList;

    @FXML
    ListView<String> serverList;

    @FXML
    TextField textField;

    @FXML
    TextArea textArea;

    @FXML
    TextArea ClientFiles;

    @FXML
    TextArea ServerFiles;

    @FXML
    HBox upperPanel;

    @FXML
    VBox bottomPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    private ExecutorService uploadService = Executors.newSingleThreadExecutor(Thread::new);

    public void setAuthorized(boolean isAuthorized){
        if (!isAuthorized){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            ServerFiles.setText("Server Files");
            ClientFiles.setText("Client Files");

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        System.out.println("initialized");
        Thread t = new Thread(()->{
            try {
                while (true) {
                    AbstractBox box = Network.readObject();
                    System.out.println("Object is read");
                    if (box instanceof MessageBox) {
                        if (box instanceof AuthMessage) {
                            setAuthorized(true);
                        }
                        if (box instanceof ErrorMessage) {
                            ErrorMessage em = (ErrorMessage) box;
                            textArea.appendText(em.getError().toString() + "\n");
                        }
                        if (box instanceof FileMessage) {
                            FileMessage fm = (FileMessage) box;
                            if (fm.getType() == MessageType.RELOAD){
                                Network.sendMsg(fm);
                            }
                            else textArea.appendText(fm.getFilename() + " " + fm.getMsg() + "\n");
                        }
                    }
                    if (box instanceof FileBox) {
                        FileBox fb = (FileBox) box;
                        System.out.println(fb.getFileName());
                        if (fb.isFirst()) {
                            System.out.println("first");
                            Files.deleteIfExists(Paths.get("client_storage/" + fb.getFileName()));
                            Files.write(Paths.get("client_storage/" + fb.getFileName()),
                                    fb.getContent(),
                                    StandardOpenOption.CREATE);
                        } else {
                            Files.write(Paths.get("client_storage/" + fb.getFileName()),
                                    fb.getContent(),
                                    StandardOpenOption.APPEND);
                        }
                        if (fb.isLast()) {
                            textArea.appendText("Файл " + fb.getFileName() + " успешно загружен в хранилище" + "\n");
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
                uploadService.shutdownNow();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
    }

    public void refreshLocalFilesList() {

        //Взял решение через runLater, потому что иначе выдавало ошибку NotJavaFXApplicationThread

        if (Platform.isFxApplicationThread()) {
            try {
                clientList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientList.getItems().add(o));
                serverList.getItems().clear();
                Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    clientList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientList.getItems().add(o));
                    serverList.getItems().clear();
                    Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void downloadFile(){
        if (textField.getLength() > 0){
            if (Files.exists(Paths.get("client_storage/" + textField.getText()))){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Rewrite Warning");
                alert.setHeaderText("Look, this file already exists here");
                alert.setContentText("It will be deleted. Do you want to continue?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    Network.sendMsg(new FileMessage(textField.getText()));
                }
            } else {
                Network.sendMsg(new FileMessage(textField.getText()));
            }
            textField.clear();
        }
    }

    public void uploadFile() {
        uploadService.execute(() -> {
            if (textField.getLength() > 0) {
                try {
                    Path path = Paths.get("client_storage/" + textField.getText());
                    System.out.println(path.getFileName());
                    System.out.println(path.toFile().getCanonicalPath());
                    System.out.println(path.toFile().getAbsolutePath());
                    if (Files.size(path) <= FileBox.PACKAGE_VOLUME){
                        FileBox fb = new FileBox(path);
                        Network.sendMsg(fb);
                    } else{
                        FileBox fb = new FileBox(path.getFileName().toString());
                        System.out.println(fb.getFileName());
                        RandomAccessFile raf = new RandomAccessFile("client_storage/" + textField.getText(), "r");
                        textField.clear();
                        do {
                            FileBox.writeInBox(fb, raf, false);
                            Network.sendMsg(fb);
                        } while (raf.length() - raf.getFilePointer() > (long)FileBox.PACKAGE_VOLUME);
                        FileBox.writeInBox(fb, raf, true);
                        Network.sendMsg(fb);
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        refreshLocalFilesList();
    }

    public void tryToAuth() {
        Network.tryToAuth(loginField.getText(), passwordField.getText());
    }
}



