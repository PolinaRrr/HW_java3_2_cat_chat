package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public Button btnEnter;
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox msgPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField password;
    @FXML
    public Button enter;
    @FXML
    public ListView<String> listClients;


    private Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8290;

    private boolean authendicate;
    private String nick;
    private Stage stage;
    private Stage registrate;
    private Registration registration;

    public void setAuthendicate(boolean authendicate) {
        this.authendicate = authendicate;
        authPanel.setVisible(!authendicate);
        authPanel.setManaged(!authendicate);
        msgPanel.setManaged(authendicate);
        msgPanel.setVisible(authendicate);
        listClients.setVisible(authendicate);
        listClients.setManaged(authendicate);

        if (!authendicate) {
            nick = "";
        }
        textArea.clear();
        setTitle(nick);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthendicate(false);
        createRegWin();
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("goodluck");
                    if (socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {
                    //цикл авторизации
                    while (true) {
                        String str = in.readUTF();
                        System.out.println(str);
                        if (str.startsWith("/")) {
                            if (str.startsWith("/authok ")) {

                                nick = str.split(" ")[1];
                                setAuthendicate(true);
                                break;
                            }
                            if (str.startsWith("/regok")) {
                                registration.addMsgToTextArea("Поздравляем с регистрацией в CatChat!");
                            }
                            if (str.startsWith("/regno")) {
                                registration.addMsgToTextArea("Не удается зарегистроваться \n Вероятно, логин или ник уже заняты");
                            }
                            if (str.equals("/end")) {
                                throw new RuntimeException("Соединение закрыто по таймауту");
                            }

                            textArea.appendText(str + "\n");
                        }

                    }


                    //цикл работы
                    while (true) {

                        String str = in.readUTF();

                        if (str.startsWith("/")) {

                            if (str.startsWith("/client")) {
                                String[] token = str.split(" ");

                                Platform.runLater(() -> {
                                    listClients.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        listClients.getItems().add(token[i]);
                                    }
                                });
                            }
                            if(str.equals("/end")){
                                break;
                            }
                           //отображение ника
                            if(str.startsWith("/unick ")){
                                nick=str.split(" ")[1];
                                setTitle(nick);
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Мы отключились");
                    setAuthendicate(false);
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText().trim() + " " + password.getText());
            password.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nick) {

        Platform.runLater(() -> {
            ((Stage) textField.getScene().getWindow()).setTitle("CatChat"+ " " + nick);
        });
    }

    public void clickListView(MouseEvent mouseEvent) {
        String receiver = listClients.getSelectionModel().getSelectedItem();
        textField.setText("/w " + receiver + " ");
    }

    private Stage createRegWin() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/registration.fxml"));
            Parent root = fxmlLoader.load();

            registrate = new Stage();
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, 700, 400));

            Registration registration = fxmlLoader.getController();
            registration.setController(this);

            stage.initModality(Modality.APPLICATION_MODAL);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public void showRegWin(ActionEvent actionEvent) {
        registrate.show();
    }

    public void tryToRegistrate(String nick, String login, String password) {
        String msg = String.format("/reg %s %s %s", nick, login, password);

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
