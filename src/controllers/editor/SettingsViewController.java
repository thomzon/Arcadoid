package controllers.editor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import data.access.NotificationCenter;
import data.settings.ConfirmationDialogCallable;
import data.settings.FTPSettings;
import data.settings.FTPSettingsValidator;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.settings.editor.EditorSettings;
import data.settings.editor.EditorSettingsValidator;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataUpdateChecker;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import utils.global.GlobalUtils;
import utils.transfer.TransferUtils;

/**
 * View controller in charge of the Arcadoid Editor settings pane.
 * @author Thomas Debouverie
 *
 */
public class SettingsViewController implements Initializable {

	@FXML
	private TextField 	ftpAddressField, ftpPortNumberField, ftpApplicationPathField, ftpDataPathField,
						ftpArtworksPathField, ftpMameRomsPathField, ftpUserField, ftpSnesRomsPathField, ftpGenesisRomsPathField;
	@FXML
	private PasswordField ftpPasswordField;
	@FXML
	private Button 	resetButton, saveButton, pickArtworksFolderButton, clearArtworksFolderButton,
					pickMameRomsFolderButton, clearMameRomsFolderButton,
					pickSnesRomsFolderButton, clearSnesRomsFolderButton,
					pickGenesisRomsFolderButton, clearGenesisRomsFolderButton;
	@FXML
	private Label artworksFolderLabel, mameRomsFolderLabel, snesRomsFolderLabel, genesisRomsFolderLabel;

	private DataUpdateChecker updateChecker = new DataUpdateChecker();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.preparePortNumberField();
		this.resetAction();
		this.checkMandatorySettingsAtStartup();
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
	
	private void checkMandatorySettingsAtStartup() {
		EditorSettings settings = this.setupEditorSettings();
		EditorSettingsValidator validator = new EditorSettingsValidator(settings);
		CompletionResult result = validator.validate();
		if (result != null && !result.success) {
			this.handleErrorForStartupSettingsCheck(result);
		}
	}
	
	@FXML private void pickArtworksFolderAction() {
		this.pickRomsFolderForField(this.artworksFolderLabel);

	}
	
	@FXML private void clearArtworksFolderAction() {
		this.artworksFolderLabel.setText("");
	}
	
	@FXML private void pickMameRomsFolderAction() {
		this.pickRomsFolderForField(this.mameRomsFolderLabel);
	}
	
	@FXML private void clearMameRomsFolderAction() {
		this.mameRomsFolderLabel.setText("");
	}
	
	@FXML private void pickSnesRomsFolderAction() {
		this.pickRomsFolderForField(this.snesRomsFolderLabel);
	}
	
	@FXML private void clearSnesRomsFolderAction() {
		this.snesRomsFolderLabel.setText("");
	}
	
	@FXML private void pickGenesisRomsFolderAction() {
		this.pickRomsFolderForField(this.genesisRomsFolderLabel);
	}
	
	@FXML private void clearGenesisRomsFolderAction() {
		this.genesisRomsFolderLabel.setText("");
	}
	
	private void pickRomsFolderForField(Label field) {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(Messages.get("title.selectFolder"));
		File folder = folderChooser.showDialog(field.getScene().getWindow());
		if (folder != null) {
			field.setText(folder.getAbsolutePath());
		}
	}
	
	@FXML private void resetAction() {
		this.ftpAddressField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS));
		this.ftpApplicationPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_APPLICATION_PATH));
		this.ftpDataPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_DATA_PATH));
		this.ftpArtworksPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_ARTWORKS_PATH));
		this.ftpMameRomsPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH));
		this.ftpSnesRomsPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_SNES_ROMS_PATH));
		this.ftpGenesisRomsPathField.setText(Settings.getSetting(PropertyId.REPOSITORY_GENESIS_ROMS_PATH));
		this.ftpUserField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_USER));
		this.ftpPasswordField.setText(Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD));
		this.artworksFolderLabel.setText(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH));
		this.mameRomsFolderLabel.setText(Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH));
		this.snesRomsFolderLabel.setText(Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH));
		this.genesisRomsFolderLabel.setText(Settings.getSetting(PropertyId.GENESIS_ROMS_FOLDER_PATH));
	}
	
	@FXML private void saveAction() {
		Settings.invalidateEditorSettings();
		if (!this.checkMandatorySettings()) return;
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
	
	private boolean checkMandatorySettings() {
		EditorSettings settings = this.setupEditorSettings();
		EditorSettingsValidator validator = new EditorSettingsValidator(settings);
		CompletionResult result = validator.validate();
		if (result != null && !result.success) {
			this.handleErrorForEditorSettingsCheckResult(result);
			return false;
		} else {
			return true;
		}
	}
	
	private boolean askToCreatePath(String path) {
		Optional<ButtonType> result = GlobalUtils.simpleConfirmationAlertForKeys("confirmation.header.ftpPathUnknown", "confirmation.body.ftpPathUnknown", path);
		return result.isPresent() && result.get() == ButtonType.OK;		
	}
	
	private FTPSettings setupFTPSettings() {
		FTPSettings settings = new FTPSettings();
		settings.address = this.ftpAddressField.getText();
		settings.portNumber = this.ftpPortNumberField.getText();
		settings.user = this.ftpUserField.getText();
		settings.password = this.ftpPasswordField.getText();
		settings.applicationPath = this.ftpApplicationPathField.getText();
		settings.catalogDataPath = this.ftpDataPathField.getText();
		settings.artworksDataPath = this.ftpArtworksPathField.getText();
		settings.mameDataPath = this.ftpMameRomsPathField.getText();
		settings.snesDataPath = this.ftpSnesRomsPathField.getText();
		settings.genesisDataPath = this.ftpGenesisRomsPathField.getText();
		return settings;
	}
	
	private EditorSettings setupEditorSettings() {
		EditorSettings editorSettings = new EditorSettings();
		editorSettings.artworksFolderPath = this.artworksFolderLabel.getText();
		editorSettings.mameRomsFolderPath = this.mameRomsFolderLabel.getText();
		editorSettings.snesRomsFolderPath = this.snesRomsFolderLabel.getText();
		editorSettings.genesisRomsFolderPath = this.genesisRomsFolderLabel.getText();
		return editorSettings;
	}
	
	private void changeInterfaceState(boolean enabled) {
		this.pickArtworksFolderButton.setDisable(!enabled);
		this.clearArtworksFolderButton.setDisable(!enabled);
		this.pickMameRomsFolderButton.setDisable(!enabled);
		this.clearMameRomsFolderButton.setDisable(!enabled);
		this.pickSnesRomsFolderButton.setDisable(!enabled);
		this.clearSnesRomsFolderButton.setDisable(!enabled);
		this.pickGenesisRomsFolderButton.setDisable(!enabled);
		this.clearGenesisRomsFolderButton.setDisable(!enabled);
		this.ftpAddressField.setDisable(!enabled);
		this.ftpPortNumberField.setDisable(!enabled);
		this.ftpUserField.setDisable(!enabled);
		this.ftpPasswordField.setDisable(!enabled);
		this.ftpApplicationPathField.setDisable(!enabled);
		this.ftpDataPathField.setDisable(!enabled);
		this.ftpArtworksPathField.setDisable(!enabled);
		this.ftpMameRomsPathField.setDisable(!enabled);
		this.ftpSnesRomsPathField.setDisable(!enabled);
		this.ftpGenesisRomsPathField.setDisable(!enabled);
		this.resetButton.setDisable(!enabled);
		this.saveButton.setDisable(!enabled);
	}
	
	private void finalizeVerificationProcedureWithResult(CompletionResult result) {
		if (result == null || result.success) {
			this.changeInterfaceState(true);
			this.doSaveSettings();
			this.handleSaveSuccess();
			this.checkForUpdate();
		} else {
			this.changeInterfaceState(true);
			TransferUtils.handleErrorForFtpResult(result, this.ftpAddressField.getText());
		}
		Settings.validateEditorSettings();
		try {
			Settings.saveSettings();
		} catch (IOException e) {
			Platform.runLater(() -> this.handleErrorSavingSettings());
		}
	}
	
	private void checkForUpdate() {
		this.updateChecker.checkForUpdate(new CompletionCallable() {
			@Override public Void call() throws Exception {
				NotificationCenter.sharedInstance().postNotification(RootController.ASK_ABOUT_DATA_UPDATE_NOTIFICATION, null);
				return null;
			}
		});
	}
	
	private void doSaveSettings() {
		try {
			this.setupFTPSettings().save();
			this.setupEditorSettings().save();
		} catch (IOException e) {
			Platform.runLater(() -> this.handleErrorSavingSettings());
		}
	}
	
	private void handleSaveSuccess() {
		GlobalUtils.simpleInfoAlertForKeys("info.header.ftpCheckSuccess", "info.body.ftpCheckSuccess");
	}
	
	private void handleErrorForStartupSettingsCheck(CompletionResult result) {
		Settings.invalidateEditorSettings();
		try {
			Settings.saveSettings();
		} catch (IOException e) {
			this.handleErrorSavingSettings();
			return;
		}
		GlobalUtils.simpleInfoAlertForKeys("info.header.incompleteSettings", "info.body.incompleteSettings");
	}
	
	private void handleErrorForEditorSettingsCheckResult(CompletionResult result) {
		String messageKey = null;
		switch (result.errorType) {
		case ARTWORKS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.artworksPathNotFound";
			break;
		case MAME_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.mameRomsPathNotFound";
			break;
		case SNES_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.snesRomsPathNotFound";
			break;
		case GENESIS_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.genesisRomsPathNotFound";
			break;
		default:
			messageKey = "error.body.unexpectedSettingsCheckError";
			break;
		}
		GlobalUtils.simpleErrorAlertForKeys("error.header.localPathCheckError", messageKey);
	}
	
	private void handleErrorSavingSettings() {
		GlobalUtils.simpleErrorAlertForKeys("error.header.settingsSave", "error.body.settingsSave");
	}

}
