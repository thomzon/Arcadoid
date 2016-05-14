package applications;

import java.io.IOException;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import utils.global.GlobalUtils;

/**
 * Handles the update of all applications version number.
 * @author Thomas Debouverie
 *
 */
public class ApplicationVersionService {

	/**
	 * Version number constants for all applications
	 */
	public static String EDITOR_VERSION_NUMBER = "1";
	public static String FRONTEND_VERSION_NUMBER = "7";
	public static String UPDATER_VERSION_NUMBER = "3";
	
	/**
	 * Tries to update in file the version number linked to given property. If it fails, it will display
	 * an appropriate dialog, then exit the application.
	 * @param versionNumber The version number to set
	 * @param property The property ID for the application's version number
	 */
	public static void updateVersionNumberForProperty(String versionNumber, PropertyId property) {
		Settings.setSetting(property, versionNumber);
		try {
			Settings.saveSettings();
		} catch (IOException e) {
			handleSettingsSaveFailure();
		}
	}
	
	private static void handleSettingsSaveFailure() {
		GlobalUtils.simpleErrorAlertForKeys("error.header.saveToFile", "error.body.versionNumberUpdate", true);
	}
	
}
