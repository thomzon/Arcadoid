package data.settings.frontend;

import java.util.Collection;

import data.settings.Settings.PropertyId;

public interface InputSettingsValidatorDelegate {

	public void inputSettingsValidatorDidStartRecordingInputProperty(InputSettingsValidator validator, PropertyId inputProperty, boolean isCombination);
	public void inputSettingsValidatorDidAddPressedKey(InputSettingsValidator validator, Collection<String> keyNames);
	public void inputSettingsValidatorDidFinish(InputSettingsValidator validator);
	
}
