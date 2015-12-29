package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import data.settings.ConfirmationDialogCallable;
import data.settings.FTPSettings;
import data.settings.FTPSettingsValidator;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		ConfirmationDialogCallable folderCreationCallback = new ConfirmationDialogCallable() {
			@Override public Boolean call() throws Exception {
				return askToCreatePath(this.dialogParameter);
			}
		};
		CompletionCallable completion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				finalizeVerificationProcedureWithResult(this.result);
				return null;
			}
		};
		FTPSettingsValidator validator = new FTPSettingsValidator(folderCreationCallback, completion, this.setupFTPSettings());
		validator.validate();
	}
	
	private boolean askToCreatePath(String path) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("confirmation.header.ftpPathUnknown"));
		alert.setContentText(Messages.get("confirmation.body.ftpPathUnknown", path));
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == ButtonType.OK;		
	}
	
	private FTPSettings setupFTPSettings() {
		FTPSettings settings = new FTPSettings();
		settings.address = this.ftpAddressField.getText();
		settings.portNumber = this.ftpPortNumberField.getText();
		settings.user = this.ftpUserField.getText();
		settings.password = this.ftpPasswordField.getText();
		settings.catalogDataPath = this.ftpDataPathField.getText();
		settings.mameDataPath = this.ftpMameRomsPathField.getText();
		return settings;
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
	
	private void finalizeVerificationProcedureWithResult(CompletionResult result) {
		if (result == null || result.success) {
			this.changeInterfaceState(true);
			this.doSaveSettings();
			this.handleSaveSuccess();
		} else {
			this.changeInterfaceState(true);
			this.handleErrorForResult(result);
		}
	}
	
	private void doSaveSettings() {
		try {
			this.setupFTPSettings().save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleSaveSuccess() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("info.header.ftpCheckSuccess"));
		alert.setContentText(Messages.get("info.body.ftpCheckSuccess"));
		alert.show();
	}
	
	private void handleErrorForResult(CompletionResult result) {
		String message = null;
		String header = Messages.get("error.header.ftpCheckError");
		switch (result.errorType) {
		case OTHER_ERROR:
			message = Messages.get("error.body.unexpectedFtpError");
			break;
		case UNKNOWN_HOST:
			message = Messages.get("error.body.unknownFtpHost" ,this.ftpAddressField.getText());
			break;
		case WRONG_LOGIN:
			message = Messages.get("error.body.invalidFtpLogin");
			break;
		case INCOMPLETE_PATHS_CHECK:
			header = Messages.get("error.header.ftpCheckIncomplete");
			message = Messages.get("error.body.dataPathsNotAllValidated");
			break;
		default:
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.show();
	}

}
