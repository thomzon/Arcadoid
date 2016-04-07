package controllers.frontend;

import java.io.File;
import java.io.IOException;

import data.settings.Messages;
import data.settings.frontend.FrontendSettings;
import data.settings.frontend.InputSettingsValidator;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import views.frontend.FrontendPane;
import views.frontend.InfoPopup;
import views.frontend.InputValidationPopup;

public class SettingsPane extends FrontendPane {
	
	private SettingsLayout layout = new SettingsLayout();
	private FrontendSettings settings = new FrontendSettings();

	@Override
	public void setupPane() {
		super.setupPane();
		this.layout.createAllNodesInSettingsPaneForSettings(this, settings);
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
		UIService.getInstance().displayPopup(popup);
	}
	
	public void startRecordingAllInputs() {
		InputSettingsValidator validator = new InputSettingsValidator(true);
		InputValidationPopup popup = new InputValidationPopup(validator, () -> {
			
		});
		UIService.getInstance().displayPopup(popup);
	}
	
	public void cancel() {
		UIService.getInstance().displayGameNavigation(true);
	}

	public void confirm() {
		this.layout.transferFieldValuesToSettings(this.settings);
		try {
			this.settings.save();
			UIService.getInstance().displayGameNavigation(true);
		} catch (IOException e) {
			this.reportSaveError();
		}
	}
	
	public void reportSaveError() {
		InfoPopup infoPopup = new InfoPopup(400, 200, Messages.get("frontend.settings.saveError"), true);
		UIService.getInstance().displayPopup(infoPopup);
	}
	
}
