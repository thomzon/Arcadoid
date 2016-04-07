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
	
	/**
	 * Creates a new instance that will only check for missing input settings.
	 */
	public InputSettingsValidator() {
		this(false);
	}
	
	/**
	 * Creates a new instance.
	 * @param redefineAllInputs If true, all inputs will be revalidated, otherwise only those missing.
	 */
	public InputSettingsValidator(boolean redefineAllInputs) {
		this.inputSettings = new InputSettings();
		this.inputsToConsider = this.inputSettings.allInputProperties();
		if (redefineAllInputs) {
			this.inputsToValidate = this.inputsToConsider;
		} else {
			this.checkInputsToValidate();
		}
	}
	
	/**
	 * @return True if all input settings have been validated.
	 */
	public boolean inputSettingsValid() {
		return this.inputsToValidate.isEmpty();
	}
	
	/**
	 * Starts recording user input for all inputs to validate.
	 * @param delegate Delegate that will be notified each time a key is validated.
	 */
	public void startRecordingWithDelegate(InputSettingsValidatorDelegate delegate) {
		this.delegate = delegate;
		this.keyboardListener = new KeyboardListener(this);
		this.recordNextKey();
	}
	
	/**
	 * Asks the instance to start recording keyboard inputs.
	 */
	public void activateRecording() {
		// Only activate recording if we are not recording a key combination, so recording will start when first key is pressed
		if (!this.recordingCombination) {
			this.recording = true;
		}
		this.keyboardListener.start();
	}
	
	/**
	 * Goes through all available input settings and fills the list of inputs that are not yet validated.
	 */
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
	
	/**
	 * Start recording next input settings. If none remain, notify delegate that validator is finished.
	 */
	private void recordNextKey() {
		if (this.inputsToValidate.isEmpty()) {
			this.delegate.inputSettingsValidatorDidFinish(this);
		} else {
			this.currentRecordedInput = this.inputsToValidate.remove(0);
			this.recordingCombination = this.inputSettings.inputPropertySupportsCombination(this.currentRecordedInput);
			this.delegate.inputSettingsValidatorDidStartRecordingInputProperty(this, this.currentRecordedInput, this.recordingCombination);
		}
	}
	
	/**
	 * Stops listening for keyboard inputs and prepare itself for the next setting that needs validation.
	 */
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
			this.delegate.inputSettingsValidatorDidAddPressedKey(this, this.keyboardListener.getPressedKeyNames(), this.keyboardListener.getPressedKeyCodes());
			return;
		}
		if (!this.recording) return;
		Integer integerValue = new Integer(keyCode);
		Settings.setSetting(this.currentRecordedInput, integerValue.toString());
		this.keyRecorded();
	}
		
}
