package data.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NavigationItem extends BaseItem {

	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
	private NavigationItem parentItem;
	private final ObservableList<NavigationItem> subItems = FXCollections.observableArrayList();
	
	public NavigationItem(long identifier) {
		super(identifier);
	}
	
	public ObservableList<NavigationItem> getSubItems() {
		return this.subItems;
	}

	public ObservableList<Tag> getAssignedTags() {
		return this.assignedTags;
	}

	public NavigationItem getParentItem() {
		return parentItem;
	}
	
	public Tag getMainTag() {
		if (this.assignedTags.size() > 0) {
			return this.assignedTags.get(0);
		} else {
			return null;
		}
	}

	public void setParentItem(NavigationItem parentItem) {
		this.parentItem = parentItem;
	}
	
	@Override
	public String getName() {
		if (super.getName() != null) {
			return super.getName();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getName();
		} else {
			return null;
		}
	}
	
	@Override
	public String getThumbnailArtworkPath() {
		if (super.getThumbnailArtworkPath() != null) {
			return super.getThumbnailArtworkPath();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getThumbnailArtworkPath();
		} else {
			return null;
		}
	}
	
	@Override
	public String getBackgroundArtworkPath() {
		if (super.getBackgroundArtworkPath() != null) {
			return super.getBackgroundArtworkPath();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getBackgroundArtworkPath();
		} else {
			return null;
		}
	}

}
