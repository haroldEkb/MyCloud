import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;


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

    private boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
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
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractBox box = Network.readObject();
                    System.out.println("Object is read");
                    if (box instanceof MessageBox){
                        if (box instanceof AuthMessage) {
                            setAuthorized(true);
                        }
                        if (box instanceof ErrorMessage) {
                            ErrorMessage em = (ErrorMessage) box;
                            textArea.appendText(em.getError().toString() + "\n");
                        }
                        if (box instanceof FileMessage){
                            FileMessage fm = (FileMessage) box;
                            textArea.appendText(fm.getFilename() + " " + fm.getMsg() + "\n");
                        }
                    }
                    if (box instanceof FileBox){
                        FileBox fileBox = (FileBox) box;
                        System.out.println(fileBox.getFileName());
                        Files.write(Paths.get("client_storage/" + fileBox.getFileName()),
                                fileBox.getContent(),
                                StandardOpenOption.CREATE);
                        textArea.appendText("Файл " + fileBox.getFileName() + " успешно загружен в хранилище" + "\n");
                        refreshLocalFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
    }

    public void refreshLocalFilesList() {
        try {
            clientList.getItems().clear();
            Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientList.getItems().add(o));
            serverList.getItems().clear();
            Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverList.getItems().add(o));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(){
        if (textField.getLength() > 0){
            Network.sendMsg(new FileMessage(textField.getText()));
            textField.clear();
        }
    }

    public void uploadFile() {
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
            textField.clear();
        }
    }

    public void tryToAuth() {
        Network.tryToAuth(loginField.getText(), passwordField.getText());
    }
}



