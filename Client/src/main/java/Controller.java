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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    Button downloadBtn;

    @FXML
    Button uploadBtn;

    @FXML
    ListView<String> filesList;

    @FXML
    TextField textField;

    @FXML
    TextArea textArea;

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
                    if (box instanceof Command){
                        Command command = (Command) box;
                        if (command.getId() == Commands.AUTH) setAuthorized(true);
                        if (command.getId() == Commands.ERROR) textArea.appendText(command.getError().toString());
                        if (command.getId() == Commands.SERVER_RESPONSE){
                            textArea.appendText(command.getFileName() + " " + command.getMsg());
                        }
                    }
                    if (box instanceof FileBox){
                        FileBox fileBox = (FileBox) box;
                        System.out.println(fileBox.getFileName());
                        Files.write(Paths.get("client_storage/" + fileBox.getFileName()),
                                fileBox.getContent(),
                                StandardOpenOption.CREATE);
                        textArea.appendText("Файл " + fileBox.getFileName() + " успешно загружен в хранилище");
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
        if (Platform.isFxApplicationThread()) {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    filesList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void downloadFile(){
        if (textField.getLength() > 0){
            Network.sendMsg(new Command(Commands.RECEIVE_FILE, textField.getText()));
            textField.clear();
        }
    }

    public void uploadFile() {
        if (textField.getLength() > 0) {
            try {
                Network.sendMsg(new FileBox(Paths.get("client_storage/" + textField.getText())));
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



