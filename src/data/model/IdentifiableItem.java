package data.model;

public abstract class IdentifiableItem {

	private long identifier;
	
	protected IdentifiableItem(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return this.identifier;
	}
	
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
