package controllers.editor;

import java.io.File;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class SettingsViewController implements Initializable {

	@FXML
	private TextField ftpAddressField, ftpPortNumberField, ftpDataPathField, ftpArtworksPathField, ftpMameRomsPathField, ftpUserField;
	@FXML
	private PasswordField ftpPasswordField;
	@FXML
	private Button resetButton, saveButton, pickArtworksFolderButton, clearArtworksFolderButton, pickMameRomsFolderButton, clearMameRomsFolderButton;
	@FXML
	private Label artworksFolderLabel, mameRomsFolderLabel;
	
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
	
	@FXML private void pickArtworksFolderAction() {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(Messages.get("title.selectFolder"));
		File folder = folderChooser.showDialog(this.artworksFolderLabel.getScene().getWindow());
		if (folder != null) {
			this.artworksFolderLabel.setText(folder.getAbsolutePath());
		}
	}
	
	@FXML private void clearArtworksFolderAction() {
		this.artworksFolderLabel.setText("");
	}
	
	@FXML private void pickMameRomsFolderAction() {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(Messages.get("title.selectFolder"));
		File folder = folderChooser.showDialog(this.mameRomsFolderLabel.getScene().getWindow());
		if (folder != null) {
			this.mameRomsFolderLabel.setText(folder.getAbsolutePath());
		}
	}
	
	@FXML private void clearMameRomsFolderAction() {
		this.mameRomsFolderLabel.setText("");
	}
	
	@FXML private void resetAction() {
		this.ftpAddressField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS));
		this.ftpDataPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_DATA_PATH));
		this.ftpArtworksPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_ARTWORKS_PATH));
		this.ftpMameRomsPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH));
		this.ftpUserField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_USER));
		this.ftpPasswordField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD));
		this.artworksFolderLabel.setText(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH));
		this.mameRomsFolderLabel.setText(Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH));
	}
	
	@FXML private void saveAction() {
		if (!this.checkAndSaveMandatorySettings()) return;
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
	
	private boolean checkAndSaveMandatorySettings() {
		if (this.artworksFolderLabel.getText() == null || this.artworksFolderLabel.getText().isEmpty() || !new File(this.artworksFolderLabel.getText()).exists()) {
			this.handleErrorForLocalPathsCheckWithMessage(Messages.get("error.body.artworksPathNotFound"));
			return false;
		}
		if (this.mameRomsFolderLabel.getText() == null || this.mameRomsFolderLabel.getText().isEmpty() || !new File(this.mameRomsFolderLabel.getText()).exists()) {
			this.handleErrorForLocalPathsCheckWithMessage(Messages.get("error.body.mameRomsPathNotFound"));
			return false;
		}
		Settings.setSetting(PropertyId.ARTWORKS_FOLDER_PATH, this.artworksFolderLabel.getText());
		Settings.setSetting(PropertyId.MAME_ROMS_FOLDER_PATH, this.mameRomsFolderLabel.getText());
		Settings.validateEditorSettings();
		return true;
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
		settings.artworksDataPath = this.ftpArtworksPathField.getText();
		settings.mameDataPath = this.ftpMameRomsPathField.getText();
		return settings;
	}
	
	private void changeInterfaceState(boolean enabled) {
		this.pickArtworksFolderButton.setDisable(!enabled);
		this.clearArtworksFolderButton.setDisable(!enabled);
		this.pickMameRomsFolderButton.setDisable(!enabled);
		this.clearMameRomsFolderButton.setDisable(!enabled);
		this.ftpAddressField.setDisable(!enabled);
		this.ftpPortNumberField.setDisable(!enabled);
		this.ftpUserField.setDisable(!enabled);
		this.ftpPasswordField.setDisable(!enabled);
		this.ftpDataPathField.setDisable(!enabled);
		this.ftpArtworksPathField.setDisable(!enabled);
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
			this.handleErrorForFtpResult(result);
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
	
	private void handleErrorForLocalPathsCheckWithMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.localPathCheckError"));
		alert.setContentText(message);
		alert.show();
	}
	
	private void handleErrorForFtpResult(CompletionResult result) {
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
