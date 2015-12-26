package data.access;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.jnativehook.keyboard.NativeKeyEvent;

/**
 * Static methods for access to application settings
 * @author Thomas
 *
 */
public class Settings
{

	/**
	 * Separator used for multiple-values settings
	 */
	public static final int 	SETTING_LIST_SEPARATOR_CODE = NativeKeyEvent.VK_SLASH;
	public static final String 	SETTING_LIST_SEPARATOR 		= "/";
	
	/**
	 * Access to properties file
	 */
	private static Properties prop;
	/**
	 * Access to message file
	 */
	private static ResourceBundle messages;
	/**
	 * Initial settings indicator
	 */
	private static boolean initial;
	/**
	 * Path to properties file
	 */
	private static final String CONFIG_FILE_PATH = "config.properties";
	/**
	 * Path to items file
	 */
	private static final String ITEMS_FILE_PATH = "items.json";
	/**
	 * Name of messages bundle
	 */
	private static final String MESSAGES_BUNDLE_NAME = "messages";
	
	/**
	 * Enum for list of supported settings
	 * @author Thomas
	 *
	 */
	public enum PropertyId
	{
		MAME_PATH("mame_path"),
		STEAM_PATH("steam_path"),
		SHUTDOWN_COMB("st_comb"),
		QUIT_GAME_COMB("qg_comb");
		
		private final String stringValue;
		private PropertyId(final String s)
		{
			stringValue = s;
		}
	}
	
	/**
	 * Try to read properties file, and create it if it does not exist yet
	 * Also retrieve message bundle for user locale (for now always English)
	 */
	static
	{
		prop = new Properties();
		try
		{
			prop.load(new FileInputStream(CONFIG_FILE_PATH));
			initial = false;
		} catch (IOException e)
		{
			initial = true;
			for (PropertyId property : PropertyId.values())
			{
				setSetting(property, "");
			}
			try
			{
				saveSettings();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		messages = ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, new Locale("en"));
	}
	
	/**
	 * Retrieve all UbercadeItems from JSON file items.json and return them
	 * @return All UbercadeItems from local items.json file
	 */
	/*public static List<UbercadeItem> getAllItems()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(UbercadeItem.class, new UbercadeItemDeserializer());
		Gson gson = builder.create();
		List<UbercadeItem> items = new ArrayList<UbercadeItem>();
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ITEMS_FILE_PATH), "UTF-8");
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.beginArray();
			while (jsonReader.hasNext())
			{
				UbercadeItem item = gson.fromJson(jsonReader, UbercadeItem.class);
				items.add(item);
			}
			jsonReader.endArray();
			jsonReader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(4);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(4);
		}
		return items;
	}*/
	
	/**
	 * Saves all given UbercadeItems to JSON file items.json
	 * @param items Items to save
	 */
	/*public static void saveAllItems(Iterable<UbercadeItem> items)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(ITEMS_FILE_PATH), "UTF-8");
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.beginArray();
			for (UbercadeItem ubercadeItem : items)
			{
				gson.toJson(ubercadeItem, ubercadeItem.getClass(), jsonWriter);
			}
			jsonWriter.endArray();
			jsonWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(4);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(4);
		}
	}*/
	
	/**
	 * Returns value for given property as a list of Integer
	 * @param property Property for which list must be returned
	 * @return List of integer for given property
	 */
	public static List<Integer> getSettingAsIntegerList(PropertyId property)
	{
		String strValue = getSetting(property);
		List<Integer> list = new ArrayList<Integer>();
		if (strValue == null || strValue.isEmpty()) return list;
		String[] tokens = strValue.split(SETTING_LIST_SEPARATOR);
		for (String token : tokens)
		{
			list.add(new Integer(token));
		}
		return list;
	}
	
	/**
	 * Sets value for given property from a list of objects - list will be concatenated
	 * in one string with SETTING_LIST_SEPARATOR as separator
	 * @param property Property for which value must be set
	 * @param values List of values to be saved as one string
	 */
	public static void setSettingForList(PropertyId property, Object[] values)
	{
		String strValue = "";
		for (Object value : values)
		{
			if (!strValue.isEmpty()) strValue += SETTING_LIST_SEPARATOR;
			strValue += value.toString();
		}
		setSetting(property, strValue);
	}
	
	/**
	 * Returns value for given property
	 * @param property Property for which value must be returned
	 * @return Value of given property
	 */
	public static String getSetting(PropertyId property)
	{
		return prop.getProperty(property.stringValue);
	}
	
	/**
	 * Sets value for given property
	 * @param property Property for which value must be set
	 * @param value Value to set
	 */
	public static void setSetting(PropertyId property, String value)
	{
		prop.setProperty(property.stringValue, value);
	}
	
	/**
	 * Saves settings to configuration file
	 * @throws IOException If any error occurs while saving
	 */
	public static void saveSettings() throws IOException
	{
		prop.store(new FileOutputStream(CONFIG_FILE_PATH), null);
	}
	
	/**
	 * @return True if settings file is initial
	 */
	public static boolean initialSettings()
	{
		return initial;
	}
	
	/**
	 * Returns text message for given key
	 * @param key Key of text message to return
	 * @return Text message for given key
	 */
	public static String getMessage(String key, String... parameters)
	{
		String message = messages.getString(key);
		if (parameters != null)
		{
			for (int i=0; i<parameters.length; i++)
			{
				message = message.replace("&"+(i+1), parameters[i]);
			}
		}
		return message;
	}

}
