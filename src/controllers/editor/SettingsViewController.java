package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.DataTransfer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsViewController implements Initializable {

	@FXML
	private TextField ftpAddressField, ftpPortNumberField, ftpDataPathField, ftpMameRomsPathField, ftpUserField;
	@FXML
	private PasswordField ftpPasswordField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resetAction();
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
		DataTransfer dataTransfer = new DataTransfer();
		dataTransfer.setFtpAddress(this.ftpAddressField.getText());
		dataTransfer.setFtpPort(this.ftpPortNumberField.getText());
		dataTransfer.setFtpUser(this.ftpUserField.getText());
		dataTransfer.setFtpPassword(this.ftpPasswordField.getText());

		dataTransfer.connectWithCompletion(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				doSaveSettings();
				return null;
			}
		});
		
	}
	
	private void doSaveSettings() {
		System.out.println("Hello there!");
		Settings.setSetting(PropertyId.REPOSITORY_FTP_ADDRESS, this.ftpAddressField.getText());
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

}
