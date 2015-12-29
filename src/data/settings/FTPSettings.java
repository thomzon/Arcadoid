package data.settings;

import java.io.IOException;

import data.settings.Settings.PropertyId;

public class FTPSettings {

	public String address, portNumber, user, password, catalogDataPath, mameDataPath;
	
	public FTPSettings() {
		this.address = Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS);
		this.portNumber = Settings.getSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER);
		this.user = Settings.getSetting(PropertyId.REPOSITORY_FTP_USER);
		this.password = Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD);
		this.catalogDataPath = Settings.getSetting(PropertyId.REPOSITORY_DATA_PATH);
		this.mameDataPath = Settings.getSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH);
	}
	
	public void save() throws IOException {
		Settings.setSetting(PropertyId.REPOSITORY_FTP_ADDRESS, this.address);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER, this.portNumber);
		Settings.setSetting(PropertyId.REPOSITORY_DATA_PATH, this.catalogDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH, this.mameDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_USER, this.user);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PASSWORD, this.password);
		Settings.saveSettings();
	}

}
