package data.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class BaseItem extends IdentifiableItem {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty thumbnailArtworkPath = new SimpleStringProperty();
	private final StringProperty backgroundArtworkPath = new SimpleStringProperty();
	
	protected BaseItem(long identifier) {
		super(identifier);
		this.setName("");
		this.setThumbnailArtworkPath("");
		this.setBackgroundArtworkPath("");
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
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
