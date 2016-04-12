package data.settings.frontend;

import java.util.ArrayList;
import java.util.List;

import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Wraps settings properties related to user input.
 * @author Thomas Debouverie
 *
 */
public class InputSettings {

	private List<PropertyId> singleInputProperties;
	private List<PropertyId> combinationInputProperties;
	
	public InputSettings() {
		this.fillSingleInputProperties();
		this.fillCombinationInputProperties();
	}
	
	/**
	 * @return List of all properties that relates to user input.
	 */
	public List<PropertyId> allInputProperties() {
		ArrayList<PropertyId> allProperties = new ArrayList<PropertyId>(this.singleInputProperties);
		allProperties.addAll(this.combinationInputProperties);
		return allProperties;
	}
	
	/**
	 * @return List of all properties that relate to a key combination input
	 */
	public List<PropertyId> allCombinationProperties() {
		return this.combinationInputProperties;
	}
	
	/**
	 * @param inputProperty The property to check
	 * @return True if given property supports a key combination rather than a simple one-key input
	 */
	public boolean inputPropertySupportsCombination(PropertyId inputProperty) {
		return this.combinationInputProperties.contains(inputProperty);
	}
	
	/**
	 * @param key The keyboard key for which we want a nice display name
	 * @return A nice display name for the input property related to given keyboard key
	 */
	public String displayNameForKey(Integer key) {
		for (PropertyId property : this.singleInputProperties) {
			Integer currentInputValue = Integer.parseInt(Settings.getSetting(property));
			if (currentInputValue.intValue() == key.intValue()) {
				return property.getDescription();
			}
		}
		return null;
	}
	
	private void fillSingleInputProperties() {
		ArrayList<PropertyId> properties = new ArrayList<PropertyId>();
		properties.add(PropertyId.KEY_P1_UP);
		properties.add(PropertyId.KEY_P1_DOWN);
		properties.add(PropertyId.KEY_P1_LEFT);
		properties.add(PropertyId.KEY_P1_RIGHT);
		properties.add(PropertyId.KEY_P1_B1);
		properties.add(PropertyId.KEY_P1_B2);
		properties.add(PropertyId.KEY_P1_B3);
		properties.add(PropertyId.KEY_P1_B4);
		properties.add(PropertyId.KEY_P1_B5);
		properties.add(PropertyId.KEY_P1_B6);
		properties.add(PropertyId.KEY_P1_B7);
		properties.add(PropertyId.KEY_P1_B8);
		properties.add(PropertyId.KEY_P1_START);
		properties.add(PropertyId.KEY_P1_SELECT);
		properties.add(PropertyId.KEY_P2_UP);
		properties.add(PropertyId.KEY_P2_DOWN);
		properties.add(PropertyId.KEY_P2_LEFT);
		properties.add(PropertyId.KEY_P2_RIGHT);
		properties.add(PropertyId.KEY_P2_B1);
		properties.add(PropertyId.KEY_P2_B2);
		properties.add(PropertyId.KEY_P2_B3);
		properties.add(PropertyId.KEY_P2_B4);
		properties.add(PropertyId.KEY_P2_B5);
		properties.add(PropertyId.KEY_P2_B6);
		properties.add(PropertyId.KEY_P2_B7);
		properties.add(PropertyId.KEY_P2_B8);
		properties.add(PropertyId.KEY_P2_START);
		properties.add(PropertyId.KEY_P2_SELECT);
		this.singleInputProperties = properties;
	}
	
	private void fillCombinationInputProperties() {
		ArrayList<PropertyId> properties = new ArrayList<PropertyId>();
		properties.add(PropertyId.KEY_COMB_VOLUME_UP);
		properties.add(PropertyId.KEY_COMB_VOLUME_DOWN);
		properties.add(PropertyId.KEY_COMB_QUIT_GAME);
		properties.add(PropertyId.KEY_COMB_FAVORITE);
		this.combinationInputProperties = properties;
	}
	
}