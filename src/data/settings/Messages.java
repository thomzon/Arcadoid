package data.settings;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;

/**
 * Simple static wrapper around localized application texts.
 * @author Thomas Debouverie
 *
 */
public class Messages {

	private static final String MESSAGES_BUNDLE_NAME = "messages";
	
	private static ResourceBundle messages;

	static {
		messages = ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, new Locale("en"));
	}

	public static void setupLoader(FXMLLoader loader) {
		loader.setResources(ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, new Locale("en")));
	}
	
	/**
	 * Returns text message for given key
	 * @param key Key of text message to return
	 * @return Text message for given key
	 */
	public static String get(String key, String... parameters) {
		String message = messages.getString(key);
		if (message == null) {
			message = key;
		}
		if (parameters != null)	{
			for (int i=0; i<parameters.length; i++)	{
				message = message.replace("&"+(i+1), parameters[i]);
			}
		}
		return message;
	}
	
}
