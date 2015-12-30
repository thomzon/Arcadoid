package data.access;

import java.util.ArrayList;

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
		ArrayList<BaseItem> allItems = new ArrayList<BaseItem>();
		allItems.addAll(ArcadoidData.sharedInstance().getAllTags());
		allItems.addAll(ArcadoidData.sharedInstance().getAllGames());
		for (BaseItem baseItem : allItems) {
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
