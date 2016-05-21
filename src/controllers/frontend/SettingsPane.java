package controllers.frontend;

import java.io.File;
import java.io.IOException;

import data.settings.ConfirmationDialogCallable;
import data.settings.FTPSettings;
import data.settings.FTPSettingsValidator;
import data.settings.Messages;
import data.settings.frontend.FrontendSettings;
import data.settings.frontend.FrontendSettingsValidator;
import data.settings.frontend.InputSettingsValidator;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import utils.global.GlobalUtils;
import utils.transfer.TransferUtils;
import views.frontend.FrontendPane;
import views.frontend.InfoPopup;
import views.frontend.InputValidationPopup;

/**
 * Settings screen for the front-end UI. Handles settings confirmation and saving.
 * @author Thomas Debouverie
 *
 */
public class SettingsPane extends FrontendPane {
	
	private SettingsLayout layout = new SettingsLayout();
	private FrontendSettings settings = new FrontendSettings();
	private FTPSettings ftpSettings = new FTPSettings();

	@Override
	public void setupPane() {
		super.setupPane();
		this.layout.createAllNodesInSettingsPaneForSettings(this, this.settings, this.ftpSettings);
		this.makeChildrenVisible(false);
	}
	
	@Override
	public void doLayout() {
		this.layout.layoutAllNodes();
		this.makeChildrenVisible(true);
	}
	
	public void chooseFolderForFieldWithTitleKey(Label field, String titleKey) {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(Messages.get("title.selectFolder"));
		File folder = folderChooser.showDialog(this.getScene().getWindow());
		if (folder != null) field.setText(folder.getAbsolutePath());
	}
	
	public void chooseFileForFieldWithExtensionAndTitleKey(Label field, String titleKey, String extensionKey, String extension) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(Messages.get(titleKey));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Messages.get(extensionKey), extension);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (file != null) field.setText(file.getPath());
	}
	
	public void startRecordingMissingInputs() {
		InputSettingsValidator validator = new InputSettingsValidator(false);
		InputValidationPopup popup = new InputValidationPopup(validator, () -> {
			
		});
		UIService.sharedInstance().displayPopup(popup);
	}
	
	public void startRecordingAllInputs() {
		InputSettingsValidator validator = new InputSettingsValidator(true);
		InputValidationPopup popup = new InputValidationPopup(validator, () -> {
			
		});
		UIService.sharedInstance().displayPopup(popup);
	}
	
	public void cancel() {
		UIService.sharedInstance().displayGameNavigation(true);
	}

	public void confirm() {
		this.layout.transferFieldValuesToSettings(this.settings, this.ftpSettings);
		if (!this.checkMandatorySettings()) return;
		this.setDisable(true);
		ConfirmationDialogCallable folderCreationCallback = new ConfirmationDialogCallable() {
			@Override public Boolean call() throws Exception {
				return false;
			}
		};
		CompletionCallable completion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				finalizeVerificationProcedureWithResult(this.result);
				return null;
			}
		};
		FTPSettingsValidator validator = new FTPSettingsValidator(folderCreationCallback, completion, this.ftpSettings);
		validator.validate();
	}
	
	public void reportSaveError() {
		InfoPopup infoPopup = new InfoPopup(400, 200, Messages.get("frontend.settings.saveError"), true);
		UIService.sharedInstance().displayPopup(infoPopup);
	}
	
	private boolean checkMandatorySettings() {
		FrontendSettingsValidator validator = new FrontendSettingsValidator(this.settings);
		CompletionResult result = validator.validate();
		if (result != null && !result.success) {
			this.handleErrorForFrontendSettingsCheckResult(result);
			return false;
		} else {
			return true;
		}
	}
	
	private void finalizeVerificationProcedureWithResult(CompletionResult result) {
		this.setDisable(false);
		if (result == null || result.success) {
			this.doSaveSettings();
			this.handleSaveSuccess();
		} else {
			TransferUtils.handleErrorForFtpResult(result, this.ftpSettings.address);
		}
	}
	
	private void doSaveSettings() {
		try {
			this.settings.save();
			this.ftpSettings.save();
		} catch (IOException e) {
			Platform.runLater(() -> this.reportSaveError());
		}
	}
	
	private void handleSaveSuccess() {
		UIService.sharedInstance().displayGameNavigation(true);
	}
	
	private void handleErrorForFrontendSettingsCheckResult(CompletionResult result) {
		String messageKey = null;
		switch (result.errorType) {
		case ARTWORKS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.artworksPathNotFound";
			break;
		case MAME_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.mameRomsPathNotFound";
			break;
		case NES_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.nesRomsPathNotFound";
			break;
		case SNES_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.snesRomsPathNotFound";
			break;
		case FUSION_ROMS_FOLDER_PATH_NOT_FOUND:
			messageKey = "error.body.fusionRomsPathNotFound";
			break;
		default:
			messageKey = "error.body.unexpectedSettingsCheckError";
			break;
		}
		GlobalUtils.simpleErrorAlertForKeys("error.header.localPathCheckError", messageKey);
	}
	
}
