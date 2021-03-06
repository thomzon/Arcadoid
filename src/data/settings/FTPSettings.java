package data.settings;

import java.io.IOException;

import data.settings.Settings.PropertyId;

/**
 * Wraps Settings properties related to FTP communication.
 * @author Thomas Debouverie
 *
 */
public class FTPSettings {

	public String address, portNumber, user, password, applicationPath, catalogDataPath, artworksDataPath;
	public String mameDataPath, snesDataPath, fusionDataPath, nesDataPath;
	
	/**
	 * Creates an instance by getting current settings via the Settings interface.
	 */
	public FTPSettings() {
		this.address = Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS);
		this.portNumber = Settings.getSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER);
		this.user = Settings.getSetting(PropertyId.REPOSITORY_FTP_USER);
		this.password = Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD);
		this.applicationPath = Settings.getSetting(PropertyId.REPOSITORY_APPLICATION_PATH);
		this.catalogDataPath = Settings.getSetting(PropertyId.REPOSITORY_DATA_PATH);
		this.artworksDataPath = Settings.getSetting(PropertyId.REPOSITORY_ARTWORKS_PATH);
		this.mameDataPath = Settings.getSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH);
		this.snesDataPath = Settings.getSetting(PropertyId.REPOSITORY_SNES_ROMS_PATH);
		this.fusionDataPath = Settings.getSetting(PropertyId.REPOSITORY_FUSION_ROMS_PATH);
		this.nesDataPath = Settings.getSetting(PropertyId.REPOSITORY_NES_ROMS_PATH);
	}
	
	/**
	 * Saves current instance values to the Settings interface.
	 * @throws IOException Throws by the Settings interface in case writing to the properties file fails.
	 */
	public void save() throws IOException {
		Settings.setSetting(PropertyId.REPOSITORY_FTP_ADDRESS, this.address);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER, this.portNumber);
		Settings.setSetting(PropertyId.REPOSITORY_APPLICATION_PATH, this.applicationPath);
		Settings.setSetting(PropertyId.REPOSITORY_DATA_PATH, this.catalogDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_ARTWORKS_PATH, this.artworksDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_MAME_ROMS_PATH, this.mameDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_SNES_ROMS_PATH, this.snesDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_FUSION_ROMS_PATH, this.fusionDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_NES_ROMS_PATH, this.nesDataPath);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_USER, this.user);
		Settings.setSetting(PropertyId.REPOSITORY_FTP_PASSWORD, this.password);
		Settings.saveSettings();
	}

}
