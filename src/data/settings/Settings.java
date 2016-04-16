package data.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import data.access.NotificationCenter;

/**
 * Static methods for access to application settings.
 * @author Thomas Debouverie
 *
 */
public class Settings
{	
	
	private static final String CONFIG_FILE_PATH = "config.properties";
	private static final String SETTING_LIST_SEPARATOR = "\\|\\|";
	/**
	 * Notification triggered when the mandatory settings have been validated or invalidated.
	 */
	public static final String SETTINGS_VALIDITY_CHANGED_NOTIFICATION = "SETTINGS_VALIDITY_CHANGED_NOTIFICATION";
	
	private static Properties prop;
	
	/**
	 * List of supported settings.
	 * @author Thomas Debouverie
	 *
	 */
	public enum PropertyId {
		// Versioning settings
		EDITOR_VERSION_NUMBER("editor_version_number", ""),
		FRONTEND_VERSION_NUMBER("frontend_version_number", ""),
		UPDATER_VERSION_NUMBER("updater_version_number", ""),
		
		// Editor settings
		EDITOR_SETTINGS_VALID("editor_settings_valid", ""),
		ARTWORKS_FOLDER_PATH("artworks_folder_path", Messages.get("field.artworksFolderPath")),
		
		// FTP settings
		REPOSITORY_FTP_ADDRESS("repo_ftp_address", Messages.get("field.address")),
		REPOSITORY_FTP_PORT_NUMBER("repo_ftp_port", Messages.get("field.portNumber")),
		REPOSITORY_FTP_USER("repo_ftp_user", Messages.get("field.username")),
		REPOSITORY_FTP_PASSWORD("repo_ftp_password", Messages.get("field.password")),
		REPOSITORY_APPLICATION_PATH("repo_application_path", Messages.get("field.applicationPath")),
		REPOSITORY_DATA_PATH("repo_base_path", Messages.get("field.catalogDataPath")),
		REPOSITORY_ARTWORKS_PATH("repo_artworks_path", Messages.get("field.artworksDataPath")),
		REPOSITORY_MAME_ROMS_PATH("repo_mame_roms_path", Messages.get("field.mameDataPath")),
		REPOSITORY_SNES_ROMS_PATH("repo_snes_roms_path", Messages.get("field.snesDataPath")),
		REPOSITORY_GENESIS_ROMS_PATH("repo_genesis_roms_path", Messages.get("field.genesisDataPath")),
		
		// Common settings
		MAME_ROMS_FOLDER_PATH("mame_roms_folder_path", Messages.get("field.mameRomsFolderPath")),
		SNES_ROMS_FOLDER_PATH("snes_roms_folder_path", Messages.get("field.snesRomsFolderPath")),
		GENESIS_ROMS_FOLDER_PATH("genesis_roms_folder_path", Messages.get("field.genesisRomsFolderPath")),
		
		// Front-end specific settings
		MAME_PATH("mame_path", Messages.get("field.mameExecutablePath")),
		STEAM_PATH("steam_path", Messages.get("field.steamExecutablePath")),
		SNES9X_PATH("snes9x_path", Messages.get("field.snes9xExecutablePath")),
		KEGA_PATH("kega_path", Messages.get("field.kegaExecutablePath")),
		KEY_P1_UP("key_p1_up", Messages.get("inputkey.p1.up")),
		KEY_P1_LEFT("key_p1_left", Messages.get("inputkey.p1.left")),
		KEY_P1_DOWN("key_p1_down", Messages.get("inputkey.p1.down")),
		KEY_P1_RIGHT("key_p1_right", Messages.get("inputkey.p1.right")),
		KEY_P1_B1("key_p1_b1", Messages.get("inputkey.p1.b1")),
		KEY_P1_B2("key_p1_b2", Messages.get("inputkey.p1.b2")),
		KEY_P1_B3("key_p1_b3", Messages.get("inputkey.p1.b3")),
		KEY_P1_B4("key_p1_b4", Messages.get("inputkey.p1.b4")),
		KEY_P1_B5("key_p1_b5", Messages.get("inputkey.p1.b5")),
		KEY_P1_B6("key_p1_b6", Messages.get("inputkey.p1.b6")),
		KEY_P1_B7("key_p1_b7", Messages.get("inputkey.p1.b7")),
		KEY_P1_B8("key_p1_b8", Messages.get("inputkey.p1.b8")),
		KEY_P1_START("key_p1_start", Messages.get("inputkey.p1.start")),
		KEY_P1_SELECT("key_p1_select", Messages.get("inputkey.p1.select")),
		KEY_P2_UP("key_p2_up", Messages.get("inputkey.p2.up")),
		KEY_P2_LEFT("key_p2_left", Messages.get("inputkey.p2.left")),
		KEY_P2_DOWN("key_p2_down", Messages.get("inputkey.p2.down")),
		KEY_P2_RIGHT("key_p2_right", Messages.get("inputkey.p2.right")),
		KEY_P2_B1("key_p2_b1", Messages.get("inputkey.p2.b1")),
		KEY_P2_B2("key_p2_b2", Messages.get("inputkey.p2.b2")),
		KEY_P2_B3("key_p2_b3", Messages.get("inputkey.p2.b3")),
		KEY_P2_B4("key_p2_b4", Messages.get("inputkey.p2.b4")),
		KEY_P2_B5("key_p2_b5", Messages.get("inputkey.p2.b5")),
		KEY_P2_B6("key_p2_b6", Messages.get("inputkey.p2.b6")),
		KEY_P2_B7("key_p2_b7", Messages.get("inputkey.p2.b7")),
		KEY_P2_B8("key_p2_b8", Messages.get("inputkey.p2.b8")),
		KEY_P2_START("key_p2_start", Messages.get("inputkey.p2.start")),
		KEY_P2_SELECT("key_p2_select", Messages.get("inputkey.p2.select")),
		KEY_COMB_VOLUME_UP("keycomb_volume_up", Messages.get("inputkey.comb.volumeUp")),
		KEY_COMB_VOLUME_DOWN("keycomb_volume_down", Messages.get("inputkey.comb.volumeDown")),
		KEY_COMB_QUIT_GAME("keycomb_quit_game", Messages.get("inputkey.comb.quitGame")),
		KEY_COMB_FAVORITE("keycomb_favorite", Messages.get("inputkey.comb.favorite"));
		
		private final String stringValue;
		private final String description;
		private PropertyId(final String s, final String d) {
			this.stringValue = s;
			this.description = d;
		}
		public String getKey() {
			return this.stringValue;
		}
		public String getDescription() {
			return this.description;
		}
		public String toString() {
			return this.description;
		}
	}
	
	/**
	 * Try to read properties file, and create it if it does not exist yet.
	 */
	static {
		prop = new Properties();
		try	{
			prop.load(new FileInputStream(CONFIG_FILE_PATH));
		} catch (IOException e)	{
			setDefaultValues();
			try	{
				saveSettings();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private static void setDefaultValues() {
		for (PropertyId property : PropertyId.values())	{
			setSetting(property, "");
		}
		File artworksFolder = new File("Artworks");
		artworksFolder.mkdir();
		setSetting(PropertyId.ARTWORKS_FOLDER_PATH, artworksFolder.getAbsolutePath());
	}
	
	public static String getSettingsValueForPropertyFromFile(PropertyId property, String file) {
		String value = null;
		Properties properties = new Properties();
		try	{
			properties.load(new FileInputStream(file));
			value = properties.getProperty(property.stringValue);
		} catch (Exception e) {
		}
		return value;
	}
	
	/**
	 * Returns value for given property as a list of Integer.
	 * The property must be a series of Integer separated by the SETTING_LIST_SEPARATOR constant value.
	 * @param property Property for which list must be returned.
	 * @return List of integer for given property.
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
	 * in one string with SETTING_LIST_SEPARATOR as separator.
	 * @param property Property for which value must be set.
	 * @param values List of values to be saved as one string.
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
	 * Returns string value for given property.
	 * @param property Property for which value must be returned.
	 * @return Value of given property.
	 */
	public static String getSetting(PropertyId property) {
		return prop.getProperty(property.stringValue);
	}
	
	/**
	 * Returns boolean value for given property.
	 * @param property Property for which value must be returned.
	 * @return Boolean value of given property.
	 */
	public static boolean getSettingAsBoolean(PropertyId property) {
		return Boolean.parseBoolean(getSetting(property));
	}
	
	/**
	 * Sets value for given property.
	 * @param property Property for which value must be set.
	 * @param value Value to set.
	 */
	public static void setSetting(PropertyId property, String value) {
		if (value != null) {
			prop.setProperty(property.stringValue, value);
		}
	}
	
	/**
	 * Sets boolean value for given property.
	 * @param property Property for which value must be set.
	 * @param value Boolean value to set.
	 */
	public static void setSetting(PropertyId property, boolean value) {
		prop.setProperty(property.stringValue, new Boolean(value).toString());
	}
	
	/**
	 * Mark editor settings as being valid and trigger appropriate notification.
	 */
	public static void validateEditorSettings() {
		setSetting(PropertyId.EDITOR_SETTINGS_VALID, true);
		NotificationCenter.sharedInstance().postNotification(SETTINGS_VALIDITY_CHANGED_NOTIFICATION, null);
	}
	
	/**
	 * Mark editor settings as being invalid and trigger appropriate notification.
	 */
	public static void invalidateEditorSettings() {
		setSetting(PropertyId.EDITOR_SETTINGS_VALID, false);
		NotificationCenter.sharedInstance().postNotification(SETTINGS_VALIDITY_CHANGED_NOTIFICATION, null);
	}
	
	/**
	 * Saves settings to configuration file.
	 * @throws IOException If any error occurs while saving.
	 */
	public static void saveSettings() throws IOException {
		prop.store(new FileOutputStream(CONFIG_FILE_PATH), null);
	}
	
	/**
	 * Creates a full local path from two parts.
	 * @param root First part of the local path.
	 * @param leaf Second part of the local path.
	 * @return The full path with appropriate separator.
	 */
	public static String fullPathWithRootAndLeaf(String root, String leaf) {
		if (root.endsWith(FileSystems.getDefault().getSeparator())) {
			return root + leaf;
		} else {
			return root + FileSystems.getDefault().getSeparator() + leaf;
		}
	}

}
