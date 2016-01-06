package data.access;

import data.model.BaseItem;

public class IdentifierProvider {

	private static long lastUsedIdentifier = 0;
	
	private IdentifierProvider() {
	}
	
	protected static void setHighestIdentifier(long identifier) {
		lastUsedIdentifier = identifier;
	}
	
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
