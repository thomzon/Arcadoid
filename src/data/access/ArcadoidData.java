package data.access;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.json.DataPersistence;
import data.model.BaseItem;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.model.NavigationItem;
import data.model.SteamGame;
import data.model.Tag;
import data.settings.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Main Arcadoid data object, in charge of giving access to the UI to all required data types.
 * @author Thomas Debouverie
 *
 */
public class ArcadoidData {

	private static ArcadoidData sharedInstance = null;
	/**
	 * Local path to the serialized JSON data.
	 */
	public static final String DATA_FILE_PATH = "data.json";
	/**
	 * Notification sent when the data has been loaded from local file.
	 */
	public static final String DATA_LOADED_NOTIFICATION = "DATA_LOADED_NOTIFICATION";
	/**
	 * Notification sent when a tag has been modified.
	 */
	public static final String TAG_MODIFIED_NOTIFICATION = "TAG_MODIFIED_NOTIFICATION";
	
	private int arcadoidDataVersionNumber = 0;
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
	
	public int getArcadoidDataVersionNumber() {
		return arcadoidDataVersionNumber;
	}

	public void setArcadoidDataVersionNumber(int arcadoidDataVersionNumber) {
		this.arcadoidDataVersionNumber = arcadoidDataVersionNumber;
	}
	
	public void incrementArcadoidDataVersionNumber() {
		this.arcadoidDataVersionNumber += 1;
	}
	
	/**
	 * @return A uniform list of all tags, games and navigation items.
	 */
	public List<BaseItem> getAllItems() {
		ArrayList<BaseItem> allItems = new ArrayList<BaseItem>();
		allItems.addAll(this.allTags);
		allItems.addAll(this.allGames);
		this.addNavigationItemsToList(this.rootNavigationItems, allItems);
		return allItems;
	}
	
	private void addNavigationItemsToList(List<NavigationItem> navigationItems, ArrayList<BaseItem> list) {
		list.addAll(navigationItems);
		for (NavigationItem navigationItem : navigationItems) {
			this.addNavigationItemsToList(navigationItem.getSubItems(), list);
		}
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
	
	/**
	 * Creates a new Tag object and insert it immediately in the list of tags.
	 * @return The created Tag object.
	 */
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
	
	public List<Game> getAllGamesForPlatform(Platform platform) {
		return this.allGames.filtered((game) -> game.getPlatform() == platform);
	}
	
	public List<Game> getAllGamesForNavigationItem(NavigationItem item) {
		List<Game> games = new ArrayList<Game>();
		if (item.getShowEligibleGames()) {
			List<Tag> itemTags = item.getGamesMustMatchAllTags() ? item.getAllFamilyTags() : item.getAssignedTags();
			for (Game game : this.allGames) {
				List<Tag> gameTags = game.getAssignedTags();
				boolean hasAllTags = false;
				for (Tag tag : itemTags) {
					boolean gameHasTag = gameTags.contains(tag);
					if (gameHasTag && !item.getGamesMustMatchAllTags()) {
						games.add(game);
						break;
					} else if (gameHasTag) {
						hasAllTags = true;
					} else if (!gameHasTag && item.getGamesMustMatchAllTags()) {
						hasAllTags = false;
						break;
					}
				}
				if (hasAllTags) {
					games.add(game);
				}
			}
		}
		return games;
	}
		
	public Game createNewGame() {
		long newIdentifier = IdentifierProvider.newIdentifier();
		Game newGame = new MameGame(newIdentifier);
		newGame.setName(Messages.get("default.gameName"));
		this.allGames.add(newGame);
		return newGame;
	}
	
	/**
	 * Changes the platform of the given game.
	 * The returned Game object is actually not the same object anymore, although it will keep the same identifier.
	 * @param currentGame Game that must be changed.
	 * @param newPlatform Platform that must be used for the change.
	 * @return A new Game object specific to the new platform.
	 */
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
	
	public List<BaseItem> getRootItems() {
		List<BaseItem> items = new ArrayList<BaseItem>();
		items.addAll(this.rootNavigationItems);
		return items;
	}
	
	public List<BaseItem> getChildrenForNavigationItem(NavigationItem navigationItem) {
		List<BaseItem> children = new ArrayList<BaseItem>();
		children.addAll(navigationItem.getSubItems());
		children.addAll(this.getAllGamesForNavigationItem(navigationItem));
		return children;
	}
	
	public List<BaseItem> getSiblingsForNavigationItem(NavigationItem navigationItem) {
		List<BaseItem> siblings = new ArrayList<BaseItem>();
		NavigationItem parentItem = navigationItem.getParentItem();
		if (parentItem != null) {
			siblings.addAll(this.getChildrenForNavigationItem(parentItem));
		} else {
			siblings.addAll(this.rootNavigationItems);
		}
		return siblings;
	}
	
	public NavigationItem createNewNavigationItemWithParent(NavigationItem parent) {
		long newIdentifier = IdentifierProvider.newIdentifier();
		NavigationItem item = new NavigationItem(newIdentifier);
		item.setName(Messages.get("default.navigationItemName"));
		item.setParentItem(parent);
		if (parent == null) {
			this.rootNavigationItems.add(item);
		}
		return item;
	}
	
	public void deleteNavigationItem(NavigationItem navigationItem) {
		if (navigationItem.getParentItem() == null) {
			this.rootNavigationItems.remove(navigationItem);
		} else {
			navigationItem.setParentItem(null);
		}
	}
	
	public void setRootNavigationItems(List<NavigationItem> rootItems) {
		this.rootNavigationItems.clear();
		this.rootNavigationItems.addAll(rootItems);
	}
	
	/**
	 * Saves all data to a serialized JSON file. Exceptions are forwarded from the DataPersitence layer.
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalStateException
	 */
	public void saveData() throws UnsupportedEncodingException, IOException, FileNotFoundException, IllegalStateException {
		this.incrementArcadoidDataVersionNumber();
		DataPersistence.saveDataToFile(this, DATA_FILE_PATH);
	}
	
	/**
	 * Reads the serialized JSON file and replace all current data with the parsed data.
	 * The highest used identifier is also updated after the load by checking all retrieved objects.
	 * Once loading is finished, the DATA_LOADED_NOTIFICATION notification is posted.
	 * Exceptions are forwarded from the DataPersistence layer.
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public void loadData() throws UnsupportedEncodingException, FileNotFoundException, IOException, NullPointerException {
		DataPersistence.loadDataFromFile(DATA_FILE_PATH);
		IdentifierProvider.updateHighestIdentifier();
		NotificationCenter.sharedInstance().postNotification(DATA_LOADED_NOTIFICATION, null);
	}

}
