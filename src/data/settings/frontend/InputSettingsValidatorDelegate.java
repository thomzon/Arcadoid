package data.settings.frontend;

import data.settings.Settings.PropertyId;

public interface InputSettingsValidatorDelegate {

	public void inputSettingsValidatorDidStartRecordingInputProperty(InputSettingsValidator validator, PropertyId inputProperty, boolean isCombination);
	public void inputSettingsValidatorDidFinish(InputSettingsValidator validator);
	
}
