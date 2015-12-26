package data.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NavigationItem extends BaseItem {

	private Tag tag;
	private NavigationItem parentItem;
	private final ObservableList<NavigationItem> subItems = FXCollections.observableArrayList();
	
	public NavigationItem(long identifier) {
		super(identifier);
	}
	
	public ObservableList<NavigationItem> getSubItems() {
		return this.subItems;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public NavigationItem getParentItem() {
		return parentItem;
	}

	public void setParentItem(NavigationItem parentItem) {
		this.parentItem = parentItem;
	}
	
	@Override
	public String getName() {
		if (super.getName() != null) {
			return super.getName();
		} else if (this.tag != null) {
			return this.tag.getName();
		} else {
			return null;
		}
	}
	
	@Override
	public String getThumbnailArtworkPath() {
		if (super.getThumbnailArtworkPath() != null) {
			return super.getThumbnailArtworkPath();
		} else if (this.tag != null) {
			return this.tag.getThumbnailArtworkPath();
		} else {
			return null;
		}
	}
	
	@Override
	public String getBackgroundArtworkPath() {
		if (super.getBackgroundArtworkPath() != null) {
			return super.getBackgroundArtworkPath();
		} else if (this.tag != null) {
			return this.tag.getBackgroundArtworkPath();
		} else {
			return null;
		}
	}

}
