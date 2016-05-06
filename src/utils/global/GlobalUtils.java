package utils.global;

import java.util.Optional;

import data.settings.Messages;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class GlobalUtils {

	public static void simpleInfoAlertForKeys(String headerKey, String messageKey) {
		alert(AlertType.INFORMATION, "alert.title", headerKey, messageKey, false);
	}
	
	public static void simpleErrorAlertForKeys(String headerKey, String messageKey, String... variables) {
		alert(AlertType.ERROR, "error.title", headerKey, messageKey, false, variables);
	}
	
	public static void simpleErrorAlertForKeys(String headerKey, String messageKey, boolean exitOnClose, String... variables) {
		alert(AlertType.ERROR, "error.title", headerKey, messageKey, true, variables);
	}
	
	public static Optional<ButtonType> simpleConfirmationAlertForKeys(String headerKey, String messageKey, String... variables) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get(headerKey));
		alert.setContentText(Messages.get(messageKey, variables));
		return alert.showAndWait();
	}
	
	private static void alert(AlertType type, String titleKey, String headerKey, String messageKey, boolean exitOnClose, String... variables) {
		Alert alert = new Alert(type);
		alert.setTitle(Messages.get(titleKey));
		alert.setHeaderText(Messages.get(headerKey));
		alert.setContentText(Messages.get(messageKey, variables));
		if (exitOnClose) {
			alert.setOnCloseRequest((event) -> {
				System.exit(0);
			});
		}
		alert.show();
	}
	
}
