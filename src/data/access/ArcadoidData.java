package data.access;

import java.util.List;

import data.model.Game;
import data.model.NavigationItem;
import data.model.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ArcadoidData {

	private static ArcadoidData sharedInstance = null;
	
	private ObservableList<Tag> allTags = FXCollections.observableArrayList();
	private ObservableList<Game> allGames = FXCollections.observableArrayList();
	private ObservableList<NavigationItem> rootNavigationItems = FXCollections.observableArrayList();
	
	private ArcadoidData() {
		for (int index = 0; index < 5; ++index) {
			Tag tag = this.createNewTag();
			tag.setName("Tag " + index);
		}
	}
	
	public static ArcadoidData sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ArcadoidData();
		}
		return sharedInstance;
	}
	
	public ObservableList<Tag> getAllTags() {
		return this.allTags;
	}
	
	public ObservableList<Tag> getAllTagsExcept(List<Tag> tagsToIgnore) {
		ObservableList<Tag> relevantTags = FXCollections.observableArrayList();
		for (Tag tag : this.allTags) {
			if (!tagsToIgnore.contains(tag)) {
				relevantTags.add(tag);
			}
		}
		return relevantTags;
	}
	
	public Tag createNewTag() {
		long newIdentifier = IdentifierProvider.newIdentifier();
		Tag newTag = new Tag(newIdentifier);
		newTag.setName("New tag");
		this.allTags.add(newTag);
		return newTag;
	}
	
	public void deleteTag(Tag tag) {
		this.allTags.remove(tag);
	}
	
	public ObservableList<Game> getAllGames() {
		return this.allGames;
	}
	
	public Game createNewGame() {
//		long newIdentifier = IdentifierProvider.newIdentifier();
		return null;
	}
	
	public ObservableList<NavigationItem> getRootNavigationItems() {
		return this.rootNavigationItems;
	}

}
