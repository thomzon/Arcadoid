package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsViewController implements Initializable {

	@FXML
	private TextField ftpAddressField, ftpPortNumberField, ftpDataPathField, ftpMameRomsPathField, ftpUserField;
	@FXML
	private PasswordField ftpPasswordField;
	@FXML
	private Button resetButton, saveButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.preparePortNumberField();
		this.resetAction();
	}
	
	private void preparePortNumberField() {
		this.ftpPortNumberField.textProperty().addListener(
			(observable, oldValue, newValue) -> {
				if (newValue.equals("")) return; 
				try {
					Integer i = Integer.valueOf(newValue);
					 ((StringProperty)observable).setValue("" + i);
				} catch (Exception e) {
				    ((StringProperty)observable).setValue(oldValue);
				}
			}
		);
	}
	
	@FXML
	private void resetAction() {
		this.ftpAddressField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS));
		this.ftpDataPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_DATA_PATH));
		this.ftpMameRomsPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH));
		this.ftpUserField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_USER));
		this.ftpPasswordField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD));
	}
	
	@FXML
	private void saveAction() {
		this.changeInterfaceState(false);
		DataTransfer dataTransfer = new DataTransfer();
		dataTransfer.setFtpAddress(this.ftpAddressField.getText());
		dataTransfer.setFtpPort(this.ftpPortNumberField.getText());
		dataTransfer.setFtpUser(this.ftpUserField.getText());
		dataTransfer.setFtpPassword(this.ftpPasswordField.getText());

		dataTransfer.connectWithCompletion(new CompletionCallable() {
			@Override
			public Void call() throws Exception {
				changeInterfaceState(true);
				if (this.result.success) {
					doSaveSettings();
					handleSaveSuccess();
				} else {
					handleErrorForResult(this.result);
				}
				return null;
			}
		});
	}
	
	private void changeInterfaceState(boolean enabled) {
		this.ftpAddressField.setDisable(!enabled);
		this.ftpPortNumberField.setDisable(!enabled);
		this.ftpUserField.setDisable(!enabled);
		this.ftpPasswordField.setDisable(!enabled);
		this.ftpDataPathField.setDisable(!enabled);
		this.ftpMameRomsPathField.setDisable(!enabled);
		this.resetButton.setDisable(!enabled);
		this.saveButton.setDisable(!enabled);
	}
	
	private void doSaveSettings() {
		Settings.setSetting(PropertyId.REPOSITORY_FTP_ADDRESS, this.ftpAddressField.getText());
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER, this.ftpPortNumberField.getText());
		Settings.setSetting(PropertyId.REPOSITORY_DATA_PATH, this.ftpDataPathField.getText());
		Settings.setSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH, this.ftpMameRomsPathField.getText());
		Settings.setSetting(PropertyId.REPOSITORY_FTP_USER, this.ftpUserField.getText());
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PASSWORD, this.ftpPasswordField.getText());
		try {
			Settings.saveSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleSaveSuccess() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Arcadoid");
		alert.setHeaderText("FTP Settings Saved");
		alert.setContentText("Connection to this FTP server was successful, settings have been saved.");
		alert.show();
	}
	
	private void handleErrorForResult(CompletionResult result) {
		String message = null;
		switch (result.errorType) {
		case OTHER_ERROR:
			message = "An unexpected error occured, please retry. You might want to verify the entered port number.";
			break;
		case UNKNOWN_HOST:
			message = "FTP host " + this.ftpAddressField.getText() + " cannot be reached.";
			break;
		case WRONG_LOGIN:
			message = "Credentials are invalid.";
			break;
		default:
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Arcadoid");
		alert.setHeaderText("FTP Connection Error");
		alert.setContentText(message);
		alert.show();
	}

}
