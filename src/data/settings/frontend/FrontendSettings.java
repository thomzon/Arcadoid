package data.settings.frontend;

import java.io.IOException;

import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Wraps Settings properties related to the front-end application.
 * @author Thomas Debouverie
 *
 */
public class FrontendSettings {

	public String artworksFolderPath, mameRomsFolderPath, snesRomsFolderPath, fusionRomsFolderPath, nesRomsFolderPath;
	public String mameExecutablePath, steamExecutablePath, snes9xExecutablePath, kegaExecutablePath, rocknesxExecutablePath;

	/**
	 * Creates an instance by getting property values from the Settings interface.
	 */
	public FrontendSettings() {
		this.artworksFolderPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		this.mameRomsFolderPath = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		this.snesRomsFolderPath = Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
		this.fusionRomsFolderPath = Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH);
		this.nesRomsFolderPath = Settings.getSetting(PropertyId.NES_ROMS_FOLDER_PATH);
		this.mameExecutablePath = Settings.getSetting(PropertyId.MAME_PATH);
		this.steamExecutablePath = Settings.getSetting(PropertyId.STEAM_PATH);
		this.snes9xExecutablePath = Settings.getSetting(PropertyId.SNES9X_PATH);
		this.kegaExecutablePath = Settings.getSetting(PropertyId.KEGA_PATH);
		this.rocknesxExecutablePath = Settings.getSetting(PropertyId.ROCKNESX_PATH);
	}
	
	/**
	 * Saves current instance values to the Settings interface.
	 * @throws IOException Throws by the Settings interface in case writing to the properties file fails.
	 */
	public void save() throws IOException {
		Settings.setSetting(PropertyId.ARTWORKS_FOLDER_PATH, this.artworksFolderPath);
		Settings.setSetting(PropertyId.MAME_ROMS_FOLDER_PATH, this.mameRomsFolderPath);
		Settings.setSetting(PropertyId.SNES_ROMS_FOLDER_PATH, this.snesRomsFolderPath);
		Settings.setSetting(PropertyId.FUSION_ROMS_FOLDER_PATH, this.fusionRomsFolderPath);
		Settings.setSetting(PropertyId.NES_ROMS_FOLDER_PATH, this.nesRomsFolderPath);
		Settings.setSetting(PropertyId.MAME_PATH, this.mameExecutablePath);
		Settings.setSetting(PropertyId.STEAM_PATH, this.steamExecutablePath);
		Settings.setSetting(PropertyId.SNES9X_PATH, this.snes9xExecutablePath);
		Settings.setSetting(PropertyId.KEGA_PATH, this.kegaExecutablePath);
		Settings.setSetting(PropertyId.ROCKNESX_PATH, this.rocknesxExecutablePath);
		Settings.saveSettings();
	}

}
