package data.access;

import data.model.BaseItem;

/**
 * Simple static unique identifier generator.
 * These identifiers are not randomly generated, but sequentially incremented.
 * Initially, it scans the entire catalog to get the current highest identifier used, and starts from there.
 * @author Thomas Debouverie
 *
 */
public class IdentifierProvider {

	private static long lastUsedIdentifier = 0;
	
	private IdentifierProvider() {
	}
	
	protected static void setHighestIdentifier(long identifier) {
		lastUsedIdentifier = identifier;
	}
	
	/**
	 * Goes through all items available in the ArcadoidData interface, and check what is the highest identifer used.
	 * The next available identifier is then set to this highest identifier + 1.
	 */
	protected static void updateHighestIdentifier() {
		long highestIdentifier = lastUsedIdentifier;
		for (BaseItem baseItem : ArcadoidData.sharedInstance().getAllItems()) {
			if (baseItem.getIdentifier() > highestIdentifier) {
				highestIdentifier = baseItem.getIdentifier();
			}
		}
		setHighestIdentifier(highestIdentifier);
	}
	
	protected static long newIdentifier() {
		long newIdentifier = lastUsedIdentifier + 1;
		lastUsedIdentifier = newIdentifier;
		return newIdentifier;
	}

}
