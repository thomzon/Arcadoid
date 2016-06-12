package data.input;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import controllers.frontend.UIService;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.settings.frontend.InputSettings;
import data.settings.frontend.InputSettingsValidator;
import javafx.application.Platform;
import utils.global.GlobalUtils;
import views.frontend.InputValidationPopup;

/**
 * Handles conversion of keyboard input into commands usable by the UI.
 * @author Thomas Debouverie
 *
 */
public class PlayerInputService implements KeyboardDelegate {

	private static PlayerInputService sharedInstance = null;
	
	private ArrayList<PlayerInputObserver> inputObservers;
	private KeyboardListener keyboardListener;
	
	private PlayerInputService() {
		this.inputObservers = new ArrayList<PlayerInputObserver>();
	}
	
	public static PlayerInputService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new PlayerInputService();
		}
		return sharedInstance;
	}
	
	/**
	 * Starts the service (listening to input, checking settings validity...).
	 */
	public void startService() {
		this.stopKeyboardHook();
		this.startKeyboardHook();
		this.checkInputSettings();
	}
	
	/**
	 * Stops the service.
	 */
	public void stopService() {
		this.stopKeyboardHook();
	}
	
	/**
	 * Adds given input observer to list of observers notified each time a relevant player input is detected.
	 * @param observer Observer to add
	 */
	public void addInputObserver(PlayerInputObserver observer) {
		this.inputObservers.add(observer);
	}
	
	/**
	 * Removes given input observer from list of observers notified each time a relevant player input is detected.
	 * @param observer Observer to add
	 */
	public void removeInputObserver(PlayerInputObserver observer) {
		this.inputObservers.remove(observer);
	}
	
	private void startKeyboardHook() {
		if (SystemUtils.IS_OS_WINDOWS) {
			try {
				GlobalScreen.registerNativeHook();
			} catch (NativeHookException e) {
	        	GlobalUtils.simpleErrorAlertForKeys("error.header.inputError", "error.body.keyboardHookInitFailure", true);
			}
			this.keyboardListener = new KeyboardListener(this);
			this.setupListenedCombinations();
			this.keyboardListener.start();
		}
	}
	
	private void stopKeyboardHook() {
		if (SystemUtils.IS_OS_WINDOWS) {
			GlobalScreen.unregisterNativeHook();
		}
	}
	
	private void setupListenedCombinations() {
		this.keyboardListener.resetListenedCombinations();
		InputSettings inputSettings = new InputSettings();
		for (PropertyId property : inputSettings.allCombinationProperties()) {
			List<Integer> keyCodes = Settings.getSettingAsIntegerList(property);
			this.keyboardListener.addListenedKeyCombination(keyCodes, property.getKey());
		}
	}
	
	private void checkInputSettings() {
		InputSettingsValidator validator = new InputSettingsValidator();
		if (!validator.inputSettingsValid()) {
			this.keyboardListener.stop();
			InputValidationPopup popup = new InputValidationPopup(validator, () -> {
				this.setupListenedCombinations();
				this.keyboardListener.start();
			});
			UIService.sharedInstance().displayPopup(popup);
		}
	}

	@Override
	public void combinationPressed(String combinationKey) {
		if (combinationKey.equals(PropertyId.KEY_COMB_QUIT_GAME.getKey())) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.quitGame();
				});
			});
		} else if (combinationKey.equals(PropertyId.KEY_COMB_VOLUME_DOWN.getKey())) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.lowerVolume();
				});
			});
		} else if (combinationKey.equals(PropertyId.KEY_COMB_VOLUME_UP.getKey())) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.raiseVolume();
				});
			});
		} else if (combinationKey.equals(PropertyId.KEY_COMB_FAVORITE.getKey())) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.addFavorite();
				});
			});
		}
	}

	@Override
	public void keyPressed(int keyCode, String keyName) {
		Platform.runLater(() -> {
			this.inputObservers.forEach((observer) -> {
				observer.anyInputEntered();
			});
		});
		if (this.keyCodeMatchesOneOfTheseProperties(keyCode, PropertyId.KEY_P1_UP, PropertyId.KEY_P2_UP)) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.navigateUp();
				});
			});
		} else if (this.keyCodeMatchesOneOfTheseProperties(keyCode, PropertyId.KEY_P1_DOWN, PropertyId.KEY_P2_DOWN)) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.navigateDown();
				});
			});
		} else if (this.keyCodeMatchesOneOfTheseProperties(keyCode, PropertyId.KEY_P1_LEFT, PropertyId.KEY_P2_LEFT)) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.navigateLeft();
				});
			});
		} else if (this.keyCodeMatchesOneOfTheseProperties(keyCode, PropertyId.KEY_P1_RIGHT, PropertyId.KEY_P2_RIGHT)) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.navigateRight();
				});
			});
		} else if (this.keyCodeMatchesOneOfTheseProperties(keyCode, PropertyId.KEY_P1_B1, PropertyId.KEY_P2_B1)) {
			Platform.runLater(() -> {
				this.inputObservers.forEach((observer) -> {
					observer.confirm();
				});
			});
		}
	}
	
	private boolean keyCodeMatchesOneOfTheseProperties(int keyCode, PropertyId... properties) {
		for (PropertyId propertyId : properties) {
			String stringValue = Settings.getSetting(propertyId);
			if (stringValue != null && Integer.parseInt(stringValue) == keyCode) {
				return true;
			}
		}
		return false;
	}
	
}