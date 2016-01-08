package data.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import data.access.NotificationCenter;

/**
 * Static methods for access to application settings
 * @author Thomas
 *
 */
public class Settings
{	
	
	private static final String CONFIG_FILE_PATH = "config.properties";
	private static final String SETTING_LIST_SEPARATOR = "";
	public static final String SETTINGS_VALIDITY_CHANGED_NOTIFICATION = "SETTINGS_VALIDITY_CHANGED_NOTIFICATION";
	
	private static Properties prop;
	private static boolean initial;
	
	/**
	 * Enum for list of supported settings
	 * @author Thomas
	 *
	 */
	public enum PropertyId {
		EDITOR_SETTINGS_VALID("editor_settings_valid"),
		REPOSITORY_FTP_ADDRESS("repo_ftp_address"),
		REPOSITORY_FTP_PORT_NUMBER("repo_ftp_port"),
		REPOSITORY_FTP_USER("repo_ftp_user"),
		REPOSITORY_FTP_PASSWORD("repo_ftp_password"),
		REPOSITORY_DATA_PATH("repo_base_path"),
		REPOSITORY_ARTWORKS_PATH("repo_artworks_path"),
		REPOSITORY_MAME_ROMS_PATH("repo_mame_roms_path"),
		ARTWORKS_FOLDER_PATH("artworks_folder_path"),
		MAME_ROMS_FOLDER_PATH("mame_roms_folder_path"),
		MAME_PATH("mame_path"),
		STEAM_PATH("steam_path");
		
		private final String stringValue;
		private PropertyId(final String s) {
			stringValue = s;
		}
	}
	
	/**
	 * Try to read properties file, and create it if it does not exist yet
	 * Also retrieve message bundle for user locale (for now always English)
	 */
	static {
		prop = new Properties();
		try	{
			prop.load(new FileInputStream(CONFIG_FILE_PATH));
			initial = false;
		} catch (IOException e)	{
			initial = true;
			for (PropertyId property : PropertyId.values())	{
				setSetting(property, "");
			}
			try	{
				saveSettings();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns value for given property as a list of Integer
	 * @param property Property for which list must be returned
	 * @return List of integer for given property
	 */
	public static List<Integer> getSettingAsIntegerList(PropertyId property) {
		String strValue = getSetting(property);
		List<Integer> list = new ArrayList<Integer>();
		if (strValue == null || strValue.isEmpty()) return list;
		String[] tokens = strValue.split(SETTING_LIST_SEPARATOR);
		for (String token : tokens) {
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
	public static void setSettingForList(PropertyId property, Object[] values) {
		String strValue = "";
		for (Object value : values)	{
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
	public static String getSetting(PropertyId property) {
		return prop.getProperty(property.stringValue);
	}
	
	/**
	 * Returns boolean value for given property
	 * @param property Property for which value must be returned
	 * @return Boolean value of given property
	 */
	public static boolean getSettingAsBoolean(PropertyId property) {
		return Boolean.parseBoolean(getSetting(property));
	}
	
	/**
	 * Sets value for given property
	 * @param property Property for which value must be set
	 * @param value Value to set
	 */
	public static void setSetting(PropertyId property, String value) {
		prop.setProperty(property.stringValue, value);
	}
	
	/**
	 * Sets boolean value for given property
	 * @param property Property for which value must be set
	 * @param value Boolean value to set
	 */
	public static void setSetting(PropertyId property, boolean value) {
		prop.setProperty(property.stringValue, new Boolean(value).toString());
	}
	
	/**
	 * Mark editor settings as being valid and trigger appropriate notification
	 */
	public static void validateEditorSettings() {
		setSetting(PropertyId.EDITOR_SETTINGS_VALID, true);
		NotificationCenter.sharedInstance().postNotification(SETTINGS_VALIDITY_CHANGED_NOTIFICATION, null);
	}
	
	/**
	 * Saves settings to configuration file
	 * @throws IOException If any error occurs while saving
	 */
	public static void saveSettings() throws IOException {
		prop.store(new FileOutputStream(CONFIG_FILE_PATH), null);
	}
	
	/**
	 * @return True if settings file is initial
	 */
	public static boolean initialSettings() {
		return initial;
	}
	
	/**
	 * Creates a full local path from two parts
	 * @param root First part of the local path
	 * @param leaf Second part of the local path
	 * @return The full path with appropriate separator
	 */
	public static String fullPathWithRootAndLeaf(String root, String leaf) {
		if (root.endsWith(FileSystems.getDefault().getSeparator())) {
			return root + leaf;
		} else {
			return root + FileSystems.getDefault().getSeparator() + leaf;
		}
	}

}
