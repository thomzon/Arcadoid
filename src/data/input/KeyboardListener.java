package data.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Uses the JNativeHook library to listen to system-wide keyboard events and notify its delegate
 * A KeyboardListener assumes that the JNativeHook library has already been initialized
 * @author Thomas
 *
 */
public class KeyboardListener implements NativeKeyListener
{

	/**
	 * Key combinations that must be monitored
	 */
	private Map<String,List<Integer>> listenedCombinations;
	/**
	 * List of keys currently pressed
	 */
	private Map<Integer,String> pressedCodes;
	/**
	 * Delegate to notify of interesting events
	 */
	private KeyboardDelegate delegate;
	
	/**
	 * Creates a KeyboardListener
	 * @param delegate Delegate
	 */
	public KeyboardListener(KeyboardDelegate delegate) {
		this.pressedCodes = new HashMap<Integer,String>();
		this.listenedCombinations = new HashMap<String,List<Integer>>();
		this.delegate = delegate;
	}
	
	/**
	 * Asks the KeyboardListener to register itself from the NativeKeyListener list
	 */
	public void start() {
		GlobalScreen.getInstance().addNativeKeyListener(this);
	}
	
	/**
	 * Asks the KeyboardListener to unregister itself from the NativeKeyListener list
	 */
	public void stop() {
		GlobalScreen.getInstance().removeNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		this.pressedCodes.put(arg0.getKeyCode(), NativeKeyEvent.getKeyText(arg0.getKeyCode()));
		this.delegate.keyPressed(arg0.getKeyCode(), NativeKeyEvent.getKeyText(arg0.getKeyCode()));
		for (String key : this.listenedCombinations.keySet()) {
			boolean combinationPressed = true;
			for (int code : this.listenedCombinations.get(key))	{
				if (!this.pressedCodes.containsKey(code)) {
					combinationPressed = false;
					break;
				}
			}
			if (combinationPressed && this.pressedCodes.size() == this.listenedCombinations.get(key).size())	{
				this.delegate.combinationPressed(key);
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// Notify delegate first, so he can still access all pressed keys before release
		this.delegate.keyReleased(arg0.getKeyCode(), NativeKeyEvent.getKeyText(arg0.getKeyCode()));
		this.pressedCodes.remove(arg0.getKeyCode());
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
	}
	
	/**
	 * Adds given key combination to monitored combinations
	 * @param combination List of key codes that make up the combination
	 * @param key Combination identifier
	 */
	public void addListenedKeyCombination(List<Integer> combination, String key) {
		this.listenedCombinations.put(key, combination);
	}
	
	/**
	 * Removes all key combinations that are monitored
	 */
	public void resetListenedCombinations() {
		this.listenedCombinations.clear();
	}
	
	/**
	 * @return A list of all currently pressed key codes
	 */
	public List<Integer> getPressedKeyCodes() {
		return new ArrayList<Integer>(this.pressedCodes.keySet());
	}
	
	/**
	 * @return A list of all currently pressed key names
	 */
	public Collection<String> getPressedKeyNames() {
		return this.pressedCodes.values();
	}
	
	/**
	 * @return A list of all currently pressed key codes, with corresponding key text
	 */
	public Set<Entry<Integer,String>> getPressedKeysWithText() {
		return new HashSet<Entry<Integer,String>>(this.pressedCodes.entrySet());
	}

}
