package controllers.frontend;

import com.sun.javafx.geom.Point2D;

import data.settings.FTPSettings;
import data.settings.frontend.FrontendSettings;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import utils.frontend.UIUtils;

public class SettingsLayout {

	private Label		localSettingsGroupTitle, ftpSettingsGroupTitle;
	private Label	 	artworksFolderPathLabel, mameRomsFolderPathLabel, mameExecutablePathLabel, steamExecutablePathLabel;
	private Button 		artworksFolderPathButton, mameRomsFolderPathButton, mameExecutablePathButton, steamExecutablePathButton;
	private Label		ftpAddressTitleLabel, portNumberTitleLabel, ftpUserTitleLabel, ftpPasswordTitleLabel, remoteCatalogDataPathTitleLabel, remoteArtworksDataPathTitleLabel, remoteMameDataPathTitleLabel;
	private TextField	ftpAddressField, portNumberField, ftpUserField, ftpPasswordField, remoteCatalogDataPathField, remoteArtworksDataPathField, remoteMameDataPathField;
	private Button		recordMissingInputsButton, recordAllInputsButton;
	private Button		cancelButton, confirmButton;
	
	public void createAllNodesInSettingsPaneForSettings(SettingsPane pane, FrontendSettings settings, FTPSettings ftpSettings) {
		this.createLocalSettingsFields(pane, settings);
		this.createFTPSettingsFields(pane, ftpSettings);
		this.createInputAndConfirmButtons(pane);
	}
	
	public void layoutAllNodes() {
		double usedHeight = this.layoutSettingsFields();
		this.layoutFTPFieldsAtStartingHeight(usedHeight);
		this.layoutInputValidationButtons();
		this.layoutConfirmAndCancelButton();
	}
	
	public void transferFieldValuesToSettings(FrontendSettings settings, FTPSettings ftpSettings) {
		settings.artworksFolderPath = this.artworksFolderPathLabel.getText();
		settings.mameRomsFolderPath = this.mameRomsFolderPathLabel.getText();
		settings.mameExecutablePath = this.mameExecutablePathLabel.getText();
		settings.steamExecutablePath = this.steamExecutablePathLabel.getText();
		ftpSettings.address = this.ftpAddressField.getText();
		ftpSettings.portNumber = this.portNumberField.getText();
		ftpSettings.user = this.ftpUserField.getText();
		ftpSettings.password = this.ftpPasswordField.getText();
		ftpSettings.catalogDataPath = this.remoteCatalogDataPathField.getText();
		ftpSettings.artworksDataPath = this.remoteArtworksDataPathField.getText();
		ftpSettings.mameDataPath = this.remoteMameDataPathField.getText();
	}
	
	private void createLocalSettingsFields(SettingsPane pane, FrontendSettings settings) {
		this.localSettingsGroupTitle = UIUtils.createLabel("frontend.settings.localSettingsTitle", true);
		
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
		
		UIUtils.assignStyleClassToNodes("settings-label", this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel);
		UIUtils.assignStyleClassToNodes("settings-group-title", this.localSettingsGroupTitle);
		pane.getChildren().addAll(this.localSettingsGroupTitle);
		pane.getChildren().addAll(this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.mameExecutablePathButton, this.steamExecutablePathButton);
		pane.getChildren().addAll(this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel);
	}
	
	private void createFTPSettingsFields(SettingsPane pane, FTPSettings settings) {
		this.ftpSettingsGroupTitle = UIUtils.createLabel("frontend.settings.ftpSettingsTitle", true);
		
		this.ftpAddressTitleLabel = UIUtils.createLabel("field.address", true);
		this.portNumberTitleLabel = UIUtils.createLabel("field.portNumber", true);
		this.ftpUserTitleLabel = UIUtils.createLabel("field.username", true);
		this.ftpPasswordTitleLabel = UIUtils.createLabel("field.password", true);
		this.remoteCatalogDataPathTitleLabel = UIUtils.createLabel("field.catalogDataPath", true);
		this.remoteArtworksDataPathTitleLabel = UIUtils.createLabel("field.artworksDataPath", true);
		this.remoteMameDataPathTitleLabel = UIUtils.createLabel("field.mameDataPath", true);
		
		this.ftpAddressField = new TextField();
		this.ftpAddressField.setText(settings.address);
		this.portNumberField = new TextField();
		this.portNumberField.setText(settings.portNumber);
		this.ftpUserField = new TextField();
		this.ftpUserField.setText(settings.user);
		this.ftpPasswordField = new PasswordField();
		this.ftpPasswordField.setText(settings.password);
		this.remoteCatalogDataPathField = new TextField();
		this.remoteCatalogDataPathField.setText(settings.catalogDataPath);
		this.remoteArtworksDataPathField = new TextField();
		this.remoteArtworksDataPathField.setText(settings.artworksDataPath);
		this.remoteMameDataPathField = new TextField();
		this.remoteMameDataPathField.setText(settings.mameDataPath);
		
		UIUtils.assignStyleClassToNodes("settings-title", this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel, this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel);
		UIUtils.assignStyleClassToNodes("settings-group-title", this.ftpSettingsGroupTitle);
		pane.getChildren().addAll(this.ftpSettingsGroupTitle);
		pane.getChildren().addAll(this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel, this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel);
		pane.getChildren().addAll(this.ftpAddressField, this.portNumberField, this.ftpUserField, this.ftpPasswordField, this.remoteCatalogDataPathField, this.remoteArtworksDataPathField, this.remoteMameDataPathField);
	}
	
	private void createInputAndConfirmButtons(SettingsPane pane) {
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
		
		pane.getChildren().addAll(this.recordMissingInputsButton, this.recordAllInputsButton);
		pane.getChildren().addAll(this.cancelButton, this.confirmButton);
	}
	
	private double layoutSettingsFields() {
		Button[] allButtons = new Button[]{this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.mameExecutablePathButton, this.steamExecutablePathButton};
		Label[] allLabels = new Label[]{this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.mameExecutablePathLabel, this.steamExecutablePathLabel};
		return UIUtils.layoutControlPairsInGridWithTitleStartingAtPosition(this.localSettingsGroupTitle, allButtons, allLabels, new Point2D(0, 0));
	}
	
	private double layoutFTPFieldsAtStartingHeight(double startingHeight) {
		Label[] allTitles = new Label[]{this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel, this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel};
		Control[] allFields = new Control[]{this.ftpAddressField, this.portNumberField, this.ftpUserField, this.ftpPasswordField, this.remoteCatalogDataPathField, this.remoteArtworksDataPathField, this.remoteMameDataPathField};
		return UIUtils.layoutControlPairsInGridWithTitleStartingAtPosition(this.ftpSettingsGroupTitle, allTitles, allFields, new Point2D(0, (float)startingHeight));
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
