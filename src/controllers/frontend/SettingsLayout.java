package controllers.frontend;

import data.settings.frontend.FrontendSettings;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import utils.frontend.UIUtils;

public class SettingsLayout {

	private Label	 	artworksFolderPathLabel, mameRomsFolderPathLabel, mameExecutablePathLabel, steamExecutablePathLabel;
	private Button 		artworksFolderPathButton, mameRomsFolderPathButton, mameExecutablePathButton, steamExecutablePathButton;
	private Button		recordMissingInputsButton, recordAllInputsButton;
	private Button		cancelButton, confirmButton;
	
	public void createAllNodesInSettingsPaneForSettings(SettingsPane pane, FrontendSettings settings) {
		this.artworksFolderPathButton = UIUtils.createButton("field.artworksFolderPath", true);
		this.artworksFolderPathButton.setOnAction((event) -> {
			pane.chooseFolderForFieldWithTitleKey(this.artworksFolderPathLabel, "field.artworksFolderPath");
		});
		this.artworksFolderPathLabel = UIUtils.createLabel(settings.artworksFolderPath, false);
		
		this.mameRomsFolderPathButton = UIUtils.createButton("field.mameRomsFolderPath", true);
		this.mameRomsFolderPathButton.setOnAction((event) -> {
			pane.chooseFolderForFieldWithTitleKey(this.mameRomsFolderPathLabel, "field.mameRomsFolderPath");
		});
		this.mameRomsFolderPathLabel = UIUtils.createLabel(settings.mameRomsFolderPath, false);
		
		this.mameExecutablePathButton = UIUtils.createButton("field.mameExecutablePath", true);
		this.mameExecutablePathButton.setOnAction((event) -> {
			pane.chooseFileForFieldWithExtensionAndTitleKey(this.mameExecutablePathLabel, "field.mameExecutablePath", "field.mameExecutableDescription", "mame*.exe");
		});
		this.mameExecutablePathLabel = UIUtils.createLabel(settings.mameExecutablePath, false);
		
		this.steamExecutablePathButton = UIUtils.createButton("field.steamExecutablePath", true);
		this.steamExecutablePathButton.setOnAction((event) -> {
			pane.chooseFileForFieldWithExtensionAndTitleKey(this.steamExecutablePathLabel, "field.steamExecutablePath", "field.steamExecutableDescription", "steam*.exe");
		});
		this.steamExecutablePathLabel = UIUtils.createLabel(settings.steamExecutablePath, false);
		
		this.recordMissingInputsButton = UIUtils.createButton("frontend.inputValidation.recordMissing", true);
		this.recordMissingInputsButton.setOnAction((event) -> {
			pane.startRecordingMissingInputs();
		});
		
		this.recordAllInputsButton = UIUtils.createButton("frontend.inputValidation.recordAll", true);
		this.recordAllInputsButton.setOnAction((event) -> {
			pane.startRecordingAllInputs();
		});
		
		this.cancelButton = UIUtils.createButton("common.cancel", true);
		this.cancelButton.setOnAction((event) -> {
			pane.cancel();
		});
		
		this.confirmButton = UIUtils.createButton("common.confirm", true);
		this.confirmButton.setOnAction((event) -> {
			pane.confirm();
		});
		
		pane.getChildren().addAll(this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.mameExecutablePathButton, this.steamExecutablePathButton);
		pane.getChildren().addAll(this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel);
		pane.getChildren().addAll(this.recordMissingInputsButton, this.recordAllInputsButton);
		pane.getChildren().addAll(this.cancelButton, this.confirmButton);
	}
	
	public void layoutAllNodes() {
		this.layoutSettingsFields();
		this.layoutInputValidationButtons();
		this.layoutConfirmAndCancelButton();
	}
	
	public void transferFieldValuesToSettings(FrontendSettings settings) {
		settings.artworksFolderPath = this.artworksFolderPathLabel.getText();
		settings.mameRomsFolderPath = this.mameRomsFolderPathLabel.getText();
		settings.mameExecutablePath = this.mameExecutablePathLabel.getText();
		settings.steamExecutablePath = this.steamExecutablePathLabel.getText();
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
	
	private void layoutInputValidationButtons() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.recordMissingInputsButton.setLayoutX(UIUtils.BORDER_NODE_MARGIN);
		this.recordMissingInputsButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
		this.recordAllInputsButton.setLayoutX(this.recordMissingInputsButton.getLayoutX() + UIUtils.BORDER_NODE_MARGIN + this.recordMissingInputsButton.getWidth());
		this.recordAllInputsButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
	}
	
	private void layoutConfirmAndCancelButton() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.cancelButton.setLayoutX(screenBounds.getWidth() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getWidth());
		this.cancelButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
		this.confirmButton.setLayoutX(this.cancelButton.getLayoutX() - UIUtils.BORDER_NODE_MARGIN - this.confirmButton.getWidth());
		this.confirmButton.setLayoutY(screenBounds.getHeight() - UIUtils.BORDER_NODE_MARGIN - this.cancelButton.getHeight());
	}
	
}
