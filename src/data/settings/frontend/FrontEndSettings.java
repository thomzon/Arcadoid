package data.settings.frontend;

import java.io.IOException;

import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Wraps Settings properties related to the front-end application.
 * @author Thomas Debouverie
 *
 */
public class FrontEndSettings {

	public String artworksFolderPath, mameExecutablePath, steamExecutablePath; 

	/**
	 * Creates an instance by getting property values from the Settings interface.
	 */
	public FrontEndSettings() {
		this.artworksFolderPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		this.mameExecutablePath = Settings.getSetting(PropertyId.MAME_PATH);
		this.steamExecutablePath = Settings.getSetting(PropertyId.STEAM_PATH);
	}
	
	/**
	 * Saves current instance values to the Settings interface.
	 * @throws IOException Throws by the Settings interface in case writing to the properties file fails.
	 */
	public void save() throws IOException {
		Settings.setSetting(PropertyId.ARTWORKS_FOLDER_PATH, this.artworksFolderPath);
		Settings.setSetting(PropertyId.MAME_PATH, this.mameExecutablePath);
		Settings.setSetting(PropertyId.STEAM_PATH, this.steamExecutablePath);
		Settings.saveSettings();
	}

}
