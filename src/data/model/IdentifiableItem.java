package data.model;

/**
 * Simple abstraction for objects that must be uniquely identified by a number identifier.
 * @author Thomas Debouverie
 *
 */
public abstract class IdentifiableItem {

	private long identifier;
	
	protected IdentifiableItem(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Two IdentifiableItem are equals simply if they are of the same class, and have the same identifier.
	 */
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		} else if (anObject != null && this.getClass().equals(anObject.getClass())) {
			IdentifiableItem otherItem = (IdentifiableItem)anObject;
			return otherItem.getIdentifier() == this.getIdentifier();
		} else {
			return false;
		}
	}
	
}
