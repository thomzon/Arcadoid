package controllers.updater;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * View controller in charge of offering simple controls to start an application update.
 * @author Thomas Debouverie
 *
 */
public class ArcadoidUpdaterController implements Initializable {

	@FXML
	private Button updateEditorButton, updateFrontendButton, updateUpdaterButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML private void updateEditorAction() {
		System.out.println("Update editor !");
	}
	
	@FXML private void updateFrontendAction() {
		System.out.println("Update frontend !");
	}

	@FXML private void updateUpdaterAction() {
		System.out.println("Update updater !");
	}

}
