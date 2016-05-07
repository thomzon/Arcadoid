package data.settings;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import data.settings.Settings.PropertyId;

/**
 * Properties standard class extensions that will write properties in a specific order,
 * to improve readability.
 * @author Thomas Debouverie
 *
 */
public class SortedProperties extends Properties {

	private static final long serialVersionUID = -4965482554409716528L;

	public Enumeration<Object> keys() {
//	     Enumeration<Object> keysEnum = super.keys();
	     Vector<Object> keyList = new Vector<Object>();
	     
	     keyList.add(PropertyId.EDITOR_VERSION_NUMBER.getKey());
	     keyList.add(PropertyId.FRONTEND_VERSION_NUMBER.getKey());
	     keyList.add(PropertyId.UPDATER_VERSION_NUMBER.getKey());
	     
	     keyList.add(PropertyId.EDITOR_SETTINGS_VALID.getKey());
	     
	     keyList.add(PropertyId.REPOSITORY_FTP_ADDRESS.getKey());
	     keyList.add(PropertyId.REPOSITORY_FTP_PORT_NUMBER.getKey());
	     keyList.add(PropertyId.REPOSITORY_FTP_USER.getKey());
	     keyList.add(PropertyId.REPOSITORY_FTP_PASSWORD.getKey());
	     keyList.add(PropertyId.REPOSITORY_APPLICATION_PATH.getKey());
	     keyList.add(PropertyId.REPOSITORY_DATA_PATH.getKey());
	     keyList.add(PropertyId.REPOSITORY_ARTWORKS_PATH.getKey());
	     keyList.add(PropertyId.REPOSITORY_MAME_ROMS_PATH.getKey());
	     keyList.add(PropertyId.REPOSITORY_SNES_ROMS_PATH.getKey());
	     keyList.add(PropertyId.REPOSITORY_FUSION_ROMS_PATH.getKey());

	     keyList.add(PropertyId.ARTWORKS_FOLDER_PATH.getKey());
	     keyList.add(PropertyId.MAME_ROMS_FOLDER_PATH.getKey());
	     keyList.add(PropertyId.SNES_ROMS_FOLDER_PATH.getKey());
	     keyList.add(PropertyId.FUSION_ROMS_FOLDER_PATH.getKey());
	     
	     keyList.add(PropertyId.MAME_PATH.getKey());
	     keyList.add(PropertyId.STEAM_PATH.getKey());
	     keyList.add(PropertyId.SNES9X_PATH.getKey());
	     keyList.add(PropertyId.KEGA_PATH.getKey());
	     
	     keyList.add(PropertyId.KEY_P1_UP.getKey());
	     keyList.add(PropertyId.KEY_P1_LEFT.getKey());
	     keyList.add(PropertyId.KEY_P1_DOWN.getKey());
	     keyList.add(PropertyId.KEY_P1_RIGHT.getKey());
	     keyList.add(PropertyId.KEY_P1_B1.getKey());
	     keyList.add(PropertyId.KEY_P1_B2.getKey());
	     keyList.add(PropertyId.KEY_P1_B3.getKey());
	     keyList.add(PropertyId.KEY_P1_B4.getKey());
	     keyList.add(PropertyId.KEY_P1_B5.getKey());
	     keyList.add(PropertyId.KEY_P1_B6.getKey());
	     keyList.add(PropertyId.KEY_P1_B7.getKey());
	     keyList.add(PropertyId.KEY_P1_B8.getKey());
	     keyList.add(PropertyId.KEY_P1_START.getKey());
	     keyList.add(PropertyId.KEY_P1_SELECT.getKey());
	     
	     keyList.add(PropertyId.KEY_P2_UP.getKey());
	     keyList.add(PropertyId.KEY_P2_LEFT.getKey());
	     keyList.add(PropertyId.KEY_P2_DOWN.getKey());
	     keyList.add(PropertyId.KEY_P2_RIGHT.getKey());
	     keyList.add(PropertyId.KEY_P2_B1.getKey());
	     keyList.add(PropertyId.KEY_P2_B2.getKey());
	     keyList.add(PropertyId.KEY_P2_B3.getKey());
	     keyList.add(PropertyId.KEY_P2_B4.getKey());
	     keyList.add(PropertyId.KEY_P2_B5.getKey());
	     keyList.add(PropertyId.KEY_P2_B6.getKey());
	     keyList.add(PropertyId.KEY_P2_B7.getKey());
	     keyList.add(PropertyId.KEY_P2_B8.getKey());
	     keyList.add(PropertyId.KEY_P2_START.getKey());
	     keyList.add(PropertyId.KEY_P2_SELECT.getKey());
	     
	     keyList.add(PropertyId.KEY_COMB_VOLUME_UP.getKey());
	     keyList.add(PropertyId.KEY_COMB_VOLUME_DOWN.getKey());
	     keyList.add(PropertyId.KEY_COMB_QUIT_GAME.getKey());
	     keyList.add(PropertyId.KEY_COMB_FAVORITE.getKey());

	     return keyList.elements();
	  }
	
}
