package data.settings.frontend;

import java.util.Collection;

import data.settings.Settings.PropertyId;

/**
 * Defines an object capable of following the steps of an InputSettingsValidator as it records and validates
 * user input keys.
 * @author Thomas Debouverie
 *
 */
public interface InputSettingsValidatorDelegate {

	public void inputSettingsValidatorDidStartRecordingInputProperty(InputSettingsValidator validator, PropertyId inputProperty, boolean isCombination);
	public void inputSettingsValidatorDidAddPressedKey(InputSettingsValidator validator, Collection<String> keyNames, Collection<Integer> keyCodes);
	public void inputSettingsValidatorDidFinish(InputSettingsValidator validator);
	
}
