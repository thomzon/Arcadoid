package applications;

import java.io.IOException;

import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
	public static String FRONTEND_VERSION_NUMBER = "1";
	public static String UPDATER_VERSION_NUMBER = "1";
	
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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.saveToFile"));
		alert.setContentText(Messages.get("error.body.versionNumberUpdate"));
		alert.setOnCloseRequest((event) -> {
			System.exit(0);
		});
		alert.show();
	}
	
}
