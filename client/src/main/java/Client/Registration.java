package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Registration {
    private Controller controller;
    @FXML
    public TextField nickField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passField;
    @FXML
    public Button regBtn;
    @FXML
    private TextArea textArea;


    public void clickOK(){
        controller.tryToRegistrate(nickField.getText(),loginField.getText(),passField.getText());
    }

    public void addMsgToTextArea(String msg) {
        textArea.appendText(msg + "\n");
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
