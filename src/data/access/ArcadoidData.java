package data.access;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import data.json.DataPersistence;
import data.model.Game;
import data.model.NavigationItem;
import data.model.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ArcadoidData {

	private static ArcadoidData sharedInstance = null;
	public static final String DATA_FILE_PATH = "data.json";
	
	private ObservableList<Tag> allTags = FXCollections.observableArrayList();
	private ObservableList<Game> allGames = FXCollections.observableArrayList();
	private ObservableList<NavigationItem> rootNavigationItems = FXCollections.observableArrayList();
	
	private ArcadoidData() {
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
	
	public void setAllTags(List<Tag> tags) {
		this.allTags.clear();
		this.allTags.addAll(tags);
	}
	
	public ObservableList<Game> getAllGames() {
		return this.allGames;
	}
	
	public Game createNewGame() {
//		long newIdentifier = IdentifierProvider.newIdentifier();
		return null;
	}
	
	public void deleteGame(Game game) {
		this.allGames.remove(game);
	}
	
	public void setAllGames(List<Game> games) {
		this.allGames.clear();
		this.allGames.addAll(games);
	}
	
	public ObservableList<NavigationItem> getRootNavigationItems() {
		return this.rootNavigationItems;
	}
	
	public void saveData() throws UnsupportedEncodingException, IOException, FileNotFoundException, IllegalStateException {
		DataPersistence.saveDataToFile(this, DATA_FILE_PATH);
	}
	
	public void loadData() throws UnsupportedEncodingException, FileNotFoundException, IOException, NullPointerException {
		DataPersistence.loadDataFromFile(DATA_FILE_PATH);
		IdentifierProvider.updateHighestIdentifier();
	}

}
