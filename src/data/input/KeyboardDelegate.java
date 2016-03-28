package data.input;

/**
 * Used by a KeyboardListener to notify of key events
 * @author Thomas
 *
 */
public interface KeyboardDelegate
{

	/**
	 * Called by the KeyboardListener when a specific key combination has been pressed
	 * @param combinationKey Combination ID registered to the KeyboardListener
	 */
	public void combinationPressed(String combinationKey);
	/**
	 * Called by the KeyboardListener when a key has been released
	 * @param keyCode Code of the released key
	 * @param keyName Name of the released key
	 */
	public void keyReleased(int keyCode, String keyName);
	/**
	 * Called by the KeyboardListener when a key has been pressed
	 * @param keyCode Code of the pressed key
	 * @param keyName Name of the pressed key
	 */
	public void keyPressed(int keyCode, String keyName);
	
}
