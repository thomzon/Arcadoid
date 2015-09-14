package data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class BaseItem {

	private long identifier;
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty thumbnailArtworkPath = new SimpleStringProperty();
	private final StringProperty backgroundArtworkPath = new SimpleStringProperty();
	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
	
	protected BaseItem(long identifier) {
		this.identifier = identifier;
		this.setName("");
		this.setThumbnailArtworkPath("");
		this.setBackgroundArtworkPath("");
	}
	
	public long getIdentifier() {
		return identifier;
	}
	
	public String getName() {
		return name.getValue();
	}

	public void setName(String name) {
		this.name.setValue(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}

	public String getThumbnailArtworkPath() {
		return thumbnailArtworkPath.getValue();
	}

	public void setThumbnailArtworkPath(String thumbnailArtworkPath) {
		this.thumbnailArtworkPath.setValue(thumbnailArtworkPath);
	}
	
	public StringProperty thumbnailArtworkPathProperty() {
		return this.thumbnailArtworkPath;
	}

	public String getBackgroundArtworkPath() {
		return backgroundArtworkPath.getValue();
	}
	
	public void setBackgroundArtworkPath(String backgroundArtworkPath) {
		this.backgroundArtworkPath.setValue(backgroundArtworkPath);
	}
	
	public StringProperty backgroundArtworkPathProperty() {
		return this.backgroundArtworkPath;
	}
	
	public ObservableList<Tag> getAssignedTags() {
		return this.assignedTags;
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		} else if (anObject instanceof Tag) {
			Tag otherTag = (Tag)anObject;
			return otherTag.getIdentifier() == this.getIdentifier();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
