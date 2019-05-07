import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

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
                            switchToMain(resources);
                        }
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
    }


    public void tryToAuth() {
        Network.tryToAuth(loginField.getText(), passwordField.getText());
    }

    private void switchToMain(ResourceBundle resources) throws IOException {
//        Parent root;
//        try {
//            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")), resources);
//            Stage stage = new Stage();
//            stage.setTitle("MyCloud");
//            stage.setScene(new Scene(root, 450, 450));
//            stage.show();
//            //((Node)(event.getSource())).getScene().getWindow().hide();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }


//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("main.fxml"));
//        /*
//         * if "fx:controller" is not set in fxml
//         * fxmlLoader.setController(NewWindowController);
//         */
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//        Stage stage = new Stage();
//        stage.setTitle("New Window");
//        stage.setScene(scene);
//        stage.show();
    }

}
