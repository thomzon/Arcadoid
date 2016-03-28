package data.settings.frontend;

import java.util.ArrayList;
import java.util.List;

import data.input.KeyboardDelegate;
import data.input.KeyboardListener;
import data.settings.Settings;
import data.settings.Settings.PropertyId;

public class InputSettingsValidator implements KeyboardDelegate {

	private InputSettings inputSettings;
	private List<PropertyId> inputsToConsider;
	private List<PropertyId> inputsToValidate;
	private InputSettingsValidatorDelegate delegate;
	private KeyboardListener keyboardListener;
	private boolean recordingCombination = false;
	private boolean recording = false;
	private PropertyId currentRecordedInput;
	
	public InputSettingsValidator() {
		this.inputSettings = new InputSettings();
		this.inputsToConsider = this.inputSettings.allInputProperties();
		this.checkInputsToValidate();
	}
	
	private void checkInputsToValidate() {
		ArrayList<PropertyId> properties = new ArrayList<PropertyId>();
		for (PropertyId propertyId : this.inputsToConsider) {
			String value = Settings.getSetting(propertyId);
			if (value == null || value.length() == 0) {
				properties.add(propertyId);
			}
		}
		this.inputsToValidate = properties;
	}
	
	public boolean inputSettingsValid() {
		return this.inputsToValidate.isEmpty();
	}
	
	public void startRecordingWithDelegate(InputSettingsValidatorDelegate delegate) {
		this.delegate = delegate;
		this.keyboardListener = new KeyboardListener(this);
		this.recordNextKey();
	}
	
	public void activateRecording() {
		// Only activate recording if we are not recording a key combination, so recording will start when first key is pressed
		if (!this.recordingCombination) {
			this.recording = true;
		}
		this.keyboardListener.start();
	}
	
	private void recordNextKey() {
		if (this.inputsToValidate.isEmpty()) {
			this.delegate.inputSettingsValidatorDidFinish(this);
		} else {
			this.currentRecordedInput = this.inputsToValidate.remove(0);
			this.recordingCombination = this.inputSettings.inputPropertySupportsCombination(this.currentRecordedInput);
			this.delegate.inputSettingsValidatorDidStartRecordingInputProperty(this, this.currentRecordedInput, this.recordingCombination);
		}
	}
	
	private void keyRecorded() {
		this.recording = false;
		this.keyboardListener.stop();
		this.recordNextKey();
	}

	@Override
	public void keyReleased(int keyCode, String keyName) {
		if (!this.recordingCombination || !this.recording) return;
		List<Integer> keyCodes = this.keyboardListener.getPressedKeyCodes();
		Settings.setSettingForList(this.currentRecordedInput, keyCodes.toArray());
		this.keyRecorded();
	}

	@Override
	public void keyPressed(int keyCode, String keyName) {
		// If recording a combination, recording starts as soon as a first key is pressed
		if (this.recordingCombination) {
			this.recording = true;
			this.delegate.inputSettingsValidatorDidAddPressedKey(this, this.keyboardListener.getPressedKeyNames());
			return;
		}
		if (!this.recording) return;
		Integer integerValue = new Integer(keyCode);
		Settings.setSetting(this.currentRecordedInput, integerValue.toString());
		this.keyRecorded();
	}
		
}
