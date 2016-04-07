package controllers.frontend;

import java.io.File;
import java.io.IOException;

import data.settings.Messages;
import data.settings.frontend.FrontendSettings;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import utils.frontend.UIUtils;
import views.frontend.FrontendPane;
import views.frontend.InfoPopup;

public class SettingsPane extends FrontendPane {

	private Label	 	artworksFolderPathLabel, mameRomsFolderPathLabel, mameExecutablePathLabel, steamExecutablePathLabel;
	private Button 		artworksFolderPathButton, mameRomsFolderPathButton, mameExecutablePathButton, steamExecutablePathButton;
	private Button		cancelButton, confirmButton;
	private FrontendSettings settings = new FrontendSettings();
	
	@Override
	public void setupPane() {
		super.setupPane();
		this.createAllNodes();
		this.makeChildrenVisible(false);
	}
	
	@Override
	public void doLayout() {
		this.layoutAllNodes();
		this.makeChildrenVisible(true);
	}
	
	private void createAllNodes() {
		this.artworksFolderPathButton = UIUtils.createButton("field.artworksFolderPath", true);
		this.artworksFolderPathButton.setOnAction((event) -> {
	    	this.chooseFolderForFieldWithTitleKey(this.artworksFolderPathLabel, "field.artworksFolderPath");
		});
		this.artworksFolderPathLabel = UIUtils.createLabel(this.settings.artworksFolderPath, false);
		
		this.mameRomsFolderPathButton = UIUtils.createButton("field.mameRomsFolderPath", true);
		this.mameRomsFolderPathButton.setOnAction((event) -> {
	    	this.chooseFolderForFieldWithTitleKey(this.mameRomsFolderPathLabel, "field.mameRomsFolderPath");
		});
		this.mameRomsFolderPathLabel = UIUtils.createLabel(this.settings.mameRomsFolderPath, false);
		
		this.mameExecutablePathButton = UIUtils.createButton("field.mameExecutablePath", true);
		this.mameExecutablePathButton.setOnAction((event) -> {
	    	this.chooseFileForFieldWithExtensionAndTitleKey(this.mameExecutablePathLabel, "field.mameExecutablePath", "field.mameExecutableDescription", "mame*.exe");
		});
		this.mameExecutablePathLabel = UIUtils.createLabel(this.settings.mameExecutablePath, false);
		
		this.steamExecutablePathButton = UIUtils.createButton("field.steamExecutablePath", true);
		this.steamExecutablePathButton.setOnAction((event) -> {
	    	this.chooseFileForFieldWithExtensionAndTitleKey(this.steamExecutablePathLabel, "field.steamExecutablePath", "field.steamExecutableDescription", "steam*.exe");
		});
		this.steamExecutablePathLabel = UIUtils.createLabel(this.settings.steamExecutablePath, false);
		
		this.cancelButton = UIUtils.createButton("common.cancel", true);
		this.cancelButton.setOnAction((event) -> {
			this.cancel();
		});
		
		this.confirmButton = UIUtils.createButton("common.confirm", true);
		this.confirmButton.setOnAction((event) -> {
			this.confirm();
		});
		
		this.getChildren().addAll(this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.mameExecutablePathButton, this.steamExecutablePathButton);
		this.getChildren().addAll(this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel);
		this.getChildren().addAll(this.cancelButton, this.confirmButton);
	}
	
	private void layoutAllNodes() {
		this.layoutSettingsFields();
		this.layoutConfirmAndCancelButton();
	}
	
	private void layoutSettingsFields() {
		Button[] allButtons = new Button[]{this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.mameExecutablePathButton, this.steamExecutablePathButton};
		Label[] allLabels = new Label[]{this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel};
		double usedHeight = 0;
		double highestWidth = 0;
		for (Button button : allButtons) {
			if (button.getWidth() > highestWidth) {
				highestWidth = button.getWidth();
			}
		}
		for (int index = 0; index < allButtons.length; ++index) {
			Button button = allButtons[index];
			Label label = allLabels[index];
			button.setLayoutX(UIUtils.BORDER_NODE_MARGIN);
			button.setLayoutY(UIUtils.BORDER_NODE_MARGIN * (index + 1) + usedHeight);
			label.setLayoutX(UIUtils.BUTTON_LABEL_MARGIN + highestWidth);
			label.setLayoutY(button.getLayoutY() + button.getHeight()/2 - label.getHeight()/2);
			usedHeight += button.getHeight();
		}
	}
	
	private void layoutConfirmAndCancelButton() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.cancelButton.setLayoutX(screenBounds.getWidth() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getWidth());
		this.cancelButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
		this.confirmButton.setLayoutX(UIUtils.BORDER_NODE_MARGIN);
		this.confirmButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
	}
	
	private void chooseFolderForFieldWithTitleKey(Label field, String titleKey) {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(Messages.get("title.selectFolder"));
		File folder = folderChooser.showDialog(this.getScene().getWindow());
		if (folder != null) field.setText(folder.getAbsolutePath());
	}
	
	private void chooseFileForFieldWithExtensionAndTitleKey(Label field, String titleKey, String extensionKey, String extension) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(Messages.get(titleKey));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Messages.get(extensionKey), extension);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (file != null) field.setText(file.getPath());
	}
	
	private void cancel() {
		UIService.getInstance().displayGameNavigation(true);
	}

	private void confirm() {
		this.settings.artworksFolderPath = this.artworksFolderPathLabel.getText();
		this.settings.mameRomsFolderPath = this.mameRomsFolderPathLabel.getText();
		this.settings.mameExecutablePath = this.mameExecutablePathLabel.getText();
		this.settings.steamExecutablePath = this.steamExecutablePathLabel.getText();
		try {
			this.settings.save();
			UIService.getInstance().displayGameNavigation(true);
		} catch (IOException e) {
			this.reportSaveError();
		}
	}
	
	private void reportSaveError() {
		InfoPopup infoPopup = new InfoPopup(400, 200, Messages.get("frontend.settings.saveError"), true);
		UIService.getInstance().displayPopup(infoPopup);
	}
	
}
