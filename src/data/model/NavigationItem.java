package data.model;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents one item in the Arcadoid front-end navigation.
 * If can be used to show specific subitems, or to show games that match one or several tags.
 * @author Thomas Debouverie
 *
 */
public class NavigationItem extends BaseItem {

	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
	private NavigationItem parentItem;
	private final ObservableList<NavigationItem> subItems = FXCollections.observableArrayList();
	private List<BaseItem> allChildItems;
	private boolean showEligibleGames = true;
	private boolean gamesMustMatchAllTags = true;
	
	public NavigationItem(long identifier) {
		super(identifier);
	}
	
	public ObservableList<NavigationItem> getSubItems() {
		return this.subItems;
	}

	public ObservableList<Tag> getAssignedTags() {
		return this.assignedTags;
	}
	
	public List<BaseItem> getAllChildItems() {
		return this.allChildItems;
	}
	
	public void setAllChildItems(List<BaseItem> allChildItems) {
		this.allChildItems = allChildItems;
	}
	
	public List<Tag> getAllFamilyTags() {
		List<Tag> familyTags = new ArrayList<Tag>();
		familyTags.addAll(this.assignedTags);
		NavigationItem parentItem = this.parentItem;
		while (parentItem != null) {
			for (Tag tag : parentItem.assignedTags) {
				if (!familyTags.contains(tag)) {
					familyTags.add(tag);
				}
			}
			parentItem = parentItem.parentItem;
		}
		return familyTags;
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
		if (this.parentItem != null) {
			this.parentItem.getSubItems().remove(this);
		}
		this.parentItem = parentItem;
		if (this.parentItem != null) {
			this.parentItem.getSubItems().add(this);
		}
	}
	
	public boolean getShowEligibleGames() {
		return showEligibleGames;
	}

	public void setShowEligibleGames(boolean showEligibleGames) {
		this.showEligibleGames = showEligibleGames;
	}

	public boolean getGamesMustMatchAllTags() {
		return gamesMustMatchAllTags;
	}

	public void setGamesMustMatchAllTags(boolean gamesMustMatchAllTags) {
		this.gamesMustMatchAllTags = gamesMustMatchAllTags;
	}

	public boolean hasOwnName() {
		return super.getName() != null && !super.getName().isEmpty();
	}
	
	public boolean hasOwnThumbnailArtworkPath() {
		return super.getThumbnailArtworkPath() != null && !super.getThumbnailArtworkPath().isEmpty();
	}
	
	public boolean hasOwnBackgroundArtworkPath() {
		return super.getBackgroundArtworkPath() != null && !super.getBackgroundArtworkPath().isEmpty();
	}
	
	@Override
	public String getName() {
		if (this.hasOwnName()) {
			return super.getName();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getName();
		} else {
			return null;
		}
	}
	
	@Override
	public String getThumbnailArtworkPath() {
		if (this.hasOwnThumbnailArtworkPath()) {
			return super.getThumbnailArtworkPath();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getThumbnailArtworkPath();
		} else {
			return null;
		}
	}
	
	@Override
	public String getBackgroundArtworkPath() {
		if (this.hasOwnBackgroundArtworkPath()) {
			return super.getBackgroundArtworkPath();
		} else if (this.getMainTag() != null) {
			return this.getMainTag().getBackgroundArtworkPath();
		} else {
			return null;
		}
	}

}
