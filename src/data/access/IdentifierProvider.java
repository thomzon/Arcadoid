package data.access;

public class IdentifierProvider {

	private static long lastUsedIdentifier = 0;
	
	private IdentifierProvider() {
	}
	
	protected static void setHighestIdentifier(long identifier) {
		lastUsedIdentifier = identifier;
	}
	
	protected static long newIdentifier() {
		long newIdentifier = lastUsedIdentifier + 1;
		lastUsedIdentifier = newIdentifier;
		return newIdentifier;
	}

}
