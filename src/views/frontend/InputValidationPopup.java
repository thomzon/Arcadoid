package views.frontend;

import java.io.IOException;
import java.util.Collection;

import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.settings.frontend.InputSettings;
import data.settings.frontend.InputSettingsValidator;
import data.settings.frontend.InputSettingsValidatorDelegate;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import utils.frontend.UIUtils;

/**
 * Handles recording of all necessary user inputs.
 * @author Thomas Debouverie
 *
 */
public class InputValidationPopup extends InfoPopup implements InputSettingsValidatorDelegate {

	/**
	 * Text field to display currently entered combination
	 */
	private Text pressedCombinationText;
	
	private InputSettings inputSettings = new InputSettings();
	private InputSettingsValidator inputSettingsValidator;
	private Runnable completion;
	
	public InputValidationPopup(InputSettingsValidator validator, Runnable completion) {
		super(600, 400, "", false);
		this.inputSettingsValidator = validator;
		this.completion = completion;
		this.createPressedCombinationText();
	}
	
	private void createPressedCombinationText() {
		this.pressedCombinationText = new Text("");
		this.pressedCombinationText.setWrappingWidth(this.getWidth() - UIUtils.POPUP_TEXT_MARGIN);
		this.pressedCombinationText.setTextAlignment(TextAlignment.CENTER);
		UIUtils.assignStyleClassToNodes("popup-text", this.pressedCombinationText);
		this.getChildren().add(this.pressedCombinationText);
	}
	
	@Override
	public void makeAppearAfterStandardDelay() {
		super.makeAppearAfterStandardDelay();
		this.pressedCombinationText.setLayoutX(getWidth()/2 - this.pressedCombinationText.getLayoutBounds().getWidth()/2);
		this.pressedCombinationText.setLayoutY(getHeight() - this.pressedCombinationText.getLayoutBounds().getHeight() - UIUtils.POPUP_TEXT_MARGIN);
	}

	@Override
	public void hasAppeared() {
		super.hasAppeared();
		this.inputSettingsValidator.startRecordingWithDelegate(this);
	}
	
	@Override
	public void inputSettingsValidatorDidStartRecordingInputProperty(InputSettingsValidator validator,
			PropertyId inputProperty, boolean isCombination) {
		String explanationText = Messages.get("frontend.inputValidation.singleKeyExplanation", inputProperty.getDescription());
		if (isCombination) {
			explanationText = Messages.get("frontend.inputValidation.combinationExplanation", inputProperty.getDescription());
		}
		this.setMessage(explanationText);
		this.inputSettingsValidator.activateRecording();
		this.pressedCombinationText.setText("");
	}
	
	@Override
	public void inputSettingsValidatorDidAddPressedKey(InputSettingsValidator validator, Collection<String> keyNames, Collection<Integer> keyCodes) {
		String allPressedKeys = "";
		Integer[] keyCodesList = keyCodes.toArray(new Integer[keyCodes.size()]);
		String[] keyNamesList = keyNames.toArray(new String[keyNames.size()]);
		for (int index = 0; index < keyCodes.size(); ++index) {
			Integer keyCode = keyCodesList[index];
			String keyName = keyNamesList[index];
			if (allPressedKeys.length() > 0) {
				allPressedKeys += " + ";
			}
			String displayName = this.inputSettings.displayNameForKey(keyCode);
			if (displayName != null) {
				allPressedKeys += displayName;
			} else {
				allPressedKeys += keyName;
			}
		}
		this.pressedCombinationText.setText(allPressedKeys);
	}

	@Override
	public void inputSettingsValidatorDidFinish(InputSettingsValidator validator) {
		try {
			Settings.saveSettings();
			completion.run();
			this.discard();
		} catch (IOException e) {
			this.reportSaveError();
		}
	}

	private void reportSaveError() {
		this.setMessage(Messages.get("frontend.inputValidation.saveError"));
		this.pressedCombinationText.setText("");
		UIUtils.callMethodAfterTime(this, "confirmSaveError", 3000);
	}
	
	public void confirmSaveError() {
		System.exit(0);
	}
	
}
