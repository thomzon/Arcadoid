package data.input;

import java.util.ArrayList;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import controllers.frontend.UIService;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.settings.frontend.InputSettingsValidator;
import javafx.application.Platform;
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
	
	public static PlayerInputService getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new PlayerInputService();
		}
		return sharedInstance;
	}
	
	/**
	 * Starts the service (listening to input, checking settings validity...)
	 */
	public void startService() {
		this.startKeyboardHook();
		this.checkInputSettings();
	}
	
	/**
	 * Switch the service to 'Full' mode, i.e. all player inputs are forwarded to observers.
	 */
	public void switchToFullService() {
		
	}
	
	/**
	 * Switch the service to 'Background' mode, i.e. only vital player inputs are forwarded to observers.
	 * Vital inputs only contains game shutdown.
	 */
	public void switchToBackgroundService() {
		
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
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(4);
		}
		this.keyboardListener = new KeyboardListener(this);
		this.keyboardListener.start();
	}
	
	private void checkInputSettings() {
		InputSettingsValidator validator = new InputSettingsValidator();
		if (!validator.inputSettingsValid()) {
			this.keyboardListener.stop();
			InputValidationPopup popup = new InputValidationPopup(validator, () -> {
				this.keyboardListener.start();
			});
			UIService.getInstance().displayPopup(popup);
		}
	}

	@Override
	public void combinationPressed(String combinationKey) {
		
	}

	@Override
	public void keyPressed(int keyCode, String keyName) {
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