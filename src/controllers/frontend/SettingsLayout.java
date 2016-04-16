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
	private Label	 	artworksFolderPathLabel, mameRomsFolderPathLabel, snesRomsFolderPathLabel, genesisRomsFolderPathLabel;
	private Label		mameExecutablePathLabel, steamExecutablePathLabel, snes9xExecutablePathLabel, kegaExecutablePathLabel;
	private Button 		artworksFolderPathButton, mameRomsFolderPathButton, snesRomsFolderPathButton, genesisRomsFolderPathButton;
	private Button		mameExecutablePathButton, steamExecutablePathButton, snes9xExecutablePathButton, kegaExecutablePathButton;
	private Label		ftpAddressTitleLabel, portNumberTitleLabel, ftpUserTitleLabel, ftpPasswordTitleLabel, remoteCatalogDataPathTitleLabel;
	private Label		remoteArtworksDataPathTitleLabel, remoteMameDataPathTitleLabel, remoteSnesDataPathTitleLabel, remoteGenesisDataPathTitleLabel;
	private TextField	ftpAddressField, portNumberField, ftpUserField, ftpPasswordField, remoteCatalogDataPathField;
	private TextField	remoteArtworksDataPathField, remoteMameDataPathField, remoteSnesDataPathField, remoteGenesisDataPathField;
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
		settings.snesRomsFolderPath = this.snesRomsFolderPathLabel.getText();
		settings.genesisRomsFolderPath = this.genesisRomsFolderPathLabel.getText();
		settings.mameExecutablePath = this.mameExecutablePathLabel.getText();
		settings.steamExecutablePath = this.steamExecutablePathLabel.getText();
		settings.snes9xExecutablePath = this.snes9xExecutablePathLabel.getText();
		settings.kegaExecutablePath = this.kegaExecutablePathLabel.getText();
		ftpSettings.address = this.ftpAddressField.getText();
		ftpSettings.portNumber = this.portNumberField.getText();
		ftpSettings.user = this.ftpUserField.getText();
		ftpSettings.password = this.ftpPasswordField.getText();
		ftpSettings.catalogDataPath = this.remoteCatalogDataPathField.getText();
		ftpSettings.artworksDataPath = this.remoteArtworksDataPathField.getText();
		ftpSettings.mameDataPath = this.remoteMameDataPathField.getText();
		ftpSettings.snesDataPath = this.remoteSnesDataPathField.getText();
		ftpSettings.genesisDataPath = this.remoteGenesisDataPathField.getText();
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
		
		this.snesRomsFolderPathButton = UIUtils.createButton("field.snesRomsFolderPath", true);
		this.snesRomsFolderPathButton.setOnAction((event) -> {
			pane.chooseFolderForFieldWithTitleKey(this.snesRomsFolderPathLabel, "field.snesRomsFolderPath");
		});
		this.snesRomsFolderPathLabel = UIUtils.createLabel(settings.snesRomsFolderPath, false);
		
		this.genesisRomsFolderPathButton = UIUtils.createButton("field.genesisRomsFolderPath", true);
		this.genesisRomsFolderPathButton.setOnAction((event) -> {
			pane.chooseFolderForFieldWithTitleKey(this.genesisRomsFolderPathLabel, "field.genesisRomsFolderPath");
		});
		this.genesisRomsFolderPathLabel = UIUtils.createLabel(settings.genesisRomsFolderPath, false);
		
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
		
		this.snes9xExecutablePathButton = UIUtils.createButton("field.snes9xExecutablePath", true);
		this.snes9xExecutablePathButton.setOnAction((event) -> {
			pane.chooseFileForFieldWithExtensionAndTitleKey(this.snes9xExecutablePathLabel, "field.snes9xExecutablePath", "field.snes9xExecutableDescription", "snes9x*.exe");
		});
		this.snes9xExecutablePathLabel = UIUtils.createLabel(settings.snes9xExecutablePath, false);
		
		this.kegaExecutablePathButton = UIUtils.createButton("field.kegaExecutablePath", true);
		this.kegaExecutablePathButton.setOnAction((event) -> {
			pane.chooseFileForFieldWithExtensionAndTitleKey(this.kegaExecutablePathLabel, "field.kegaExecutablePath", "field.kegaExecutableDescription", "Fusion*.exe");
		});
		this.kegaExecutablePathLabel = UIUtils.createLabel(settings.kegaExecutablePath, false);
		
		UIUtils.assignStyleClassToNodes("settings-label", this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.snesRomsFolderPathLabel, this.genesisRomsFolderPathLabel,
														  this.mameExecutablePathLabel, this.steamExecutablePathLabel, this.snes9xExecutablePathLabel, this.kegaExecutablePathLabel);
		UIUtils.assignStyleClassToNodes("settings-group-title", this.localSettingsGroupTitle);
		pane.getChildren().addAll(this.localSettingsGroupTitle);
		pane.getChildren().addAll(this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.snesRomsFolderPathButton, this.genesisRomsFolderPathButton,
								  this.mameExecutablePathButton, this.steamExecutablePathButton, this.snes9xExecutablePathButton, this.kegaExecutablePathButton);
		pane.getChildren().addAll(this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.snesRomsFolderPathLabel, this.genesisRomsFolderPathLabel,
								  this.mameExecutablePathLabel, this.steamExecutablePathLabel, this.snes9xExecutablePathLabel, this.kegaExecutablePathLabel);
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
		this.remoteSnesDataPathTitleLabel = UIUtils.createLabel("field.snesDataPath", true);
		this.remoteGenesisDataPathTitleLabel = UIUtils.createLabel("field.genesisDataPath", true);
		
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
		this.remoteSnesDataPathField = new TextField();
		this.remoteSnesDataPathField.setText(settings.snesDataPath);
		this.remoteGenesisDataPathField = new TextField();
		this.remoteGenesisDataPathField.setText(settings.genesisDataPath);
		
		UIUtils.assignStyleClassToNodes("settings-title", this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel,
														  this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel, this.remoteSnesDataPathTitleLabel, this.remoteGenesisDataPathTitleLabel);
		UIUtils.assignStyleClassToNodes("settings-group-title", this.ftpSettingsGroupTitle);
		pane.getChildren().addAll(this.ftpSettingsGroupTitle);
		pane.getChildren().addAll(this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel,
								  this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel, this.remoteSnesDataPathTitleLabel, this.remoteGenesisDataPathTitleLabel);
		pane.getChildren().addAll(this.ftpAddressField, this.portNumberField, this.ftpUserField, this.ftpPasswordField, this.remoteCatalogDataPathField,
								  this.remoteArtworksDataPathField, this.remoteMameDataPathField, this.remoteSnesDataPathField, this.remoteGenesisDataPathField);
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
		Button[] allButtons = new Button[]{
				this.artworksFolderPathButton, this.mameRomsFolderPathButton, this.snesRomsFolderPathButton, this.genesisRomsFolderPathButton,
				this.mameExecutablePathButton, this.steamExecutablePathButton, this.snes9xExecutablePathButton, this.kegaExecutablePathButton};
		Label[] allLabels = new Label[]{
				this.artworksFolderPathLabel, this.mameRomsFolderPathLabel, this.snesRomsFolderPathLabel, this.genesisRomsFolderPathLabel,
				this.mameExecutablePathLabel, this.steamExecutablePathLabel, this.snes9xExecutablePathLabel, this.kegaExecutablePathLabel};
		return UIUtils.layoutControlPairsInGridWithTitleStartingAtPosition(this.localSettingsGroupTitle, allButtons, allLabels, new Point2D(0, 0));
	}
	
	private double layoutFTPFieldsAtStartingHeight(double startingHeight) {
		Label[] allTitles = new Label[]{
				this.ftpAddressTitleLabel, this.portNumberTitleLabel, this.ftpUserTitleLabel, this.ftpPasswordTitleLabel, this.remoteCatalogDataPathTitleLabel,
				this.remoteArtworksDataPathTitleLabel, this.remoteMameDataPathTitleLabel, this.remoteSnesDataPathTitleLabel, this.remoteGenesisDataPathTitleLabel};
		Control[] allFields = new Control[]{
				this.ftpAddressField, this.portNumberField, this.ftpUserField, this.ftpPasswordField, this.remoteCatalogDataPathField,
				this.remoteArtworksDataPathField, this.remoteMameDataPathField, this.remoteSnesDataPathField, this.remoteGenesisDataPathField};
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
