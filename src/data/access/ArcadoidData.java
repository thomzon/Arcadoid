package data.access;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.json.DataPersistence;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.model.NavigationItem;
import data.model.SteamGame;
import data.model.Tag;
import data.settings.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ArcadoidData {

	private static ArcadoidData sharedInstance = null;
	public static final String DATA_FILE_PATH = "data.json";
	public static final String DATA_LOADED_NOTIFICATION = "DATA_LOADED_NOTIFICATION";
	public static final String TAG_MODIFIED_NOTIFICATION = "TAG_MODIFIED_NOTIFICATION";
	
	private ObservableList<Tag> allTags = FXCollections.observableArrayList();
	private Map<Number, Tag> tagsByIdentifier = new HashMap<Number, Tag>();
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
	
	public Tag getTagByIdentifier(long identifier) {
		return this.tagsByIdentifier.get(identifier);
	}
	
	public Tag createNewTag() {
		long newIdentifier = IdentifierProvider.newIdentifier();
		Tag newTag = new Tag(newIdentifier);
		newTag.setName(Messages.get("default.tagName"));
		this.allTags.add(newTag);
		this.tagsByIdentifier.put(newTag.getIdentifier(), newTag);
		return newTag;
	}
	
	public void deleteTag(Tag tag) {
		this.allTags.remove(tag);
	}
	
	public void setAllTags(List<Tag> tags) {
		this.allTags.clear();
		this.allTags.addAll(tags);
		this.tagsByIdentifier.clear();
		for (Tag tag : tags) {
			this.tagsByIdentifier.put(tag.getIdentifier(), tag);
		}
	}
	
	public ObservableList<Game> getAllGames() {
		return this.allGames;
	}
	
	public Game createNewGame() {
		long newIdentifier = IdentifierProvider.newIdentifier();
		Game newGame = new MameGame(newIdentifier);
		newGame.setName(Messages.get("default.gameName"));
		this.allGames.add(newGame);
		return newGame;
	}
	
	public Game changeGamePlatform(Game currentGame, Platform newPlatform) {
		Game newGame = null;
		this.allGames.indexOf(currentGame);
		switch (newPlatform) {
		case MAME:
			newGame = new MameGame(currentGame);
			break;
		case STEAM:
			newGame = new SteamGame(currentGame);
			break;		
		}
		this.allGames.set(this.allGames.indexOf(currentGame), newGame);
		return newGame;
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
		NotificationCenter.sharedInstance().postNotification(DATA_LOADED_NOTIFICATION, null);
	}

}
