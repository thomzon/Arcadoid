package data.settings.editor;

import java.io.IOException;

import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Wraps Settings properties related to the catalog editor.
 * @author Thomas Debouverie
 *
 */
public class EditorSettings {

	public String artworksFolderPath, mameRomsFolderPath; 
	
	/**
	 * Creates an instance by getting property values from the Settings interface.
	 */
	public EditorSettings() {
		this.artworksFolderPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		this.mameRomsFolderPath = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
	}
	
	/**
	 * Saves current instance values to the Settings interface.
	 * @throws IOException Throws by the Settings interface in case writing to the properties file fails.
	 */
	public void save() throws IOException {
		Settings.setSetting(PropertyId.ARTWORKS_FOLDER_PATH, this.artworksFolderPath);
		Settings.setSetting(PropertyId.MAME_ROMS_FOLDER_PATH, this.mameRomsFolderPath);
		Settings.saveSettings();
	}

}
