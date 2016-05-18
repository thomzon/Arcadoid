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
import data.model.FusionGame;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.model.NavigationItem;
import data.model.NesGame;
import data.model.SnesGame;
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

	/**
	 * Singleton access.
	 * @return ArcadoidData unique instance.
	 */
	public static ArcadoidData sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ArcadoidData();
		}
		return sharedInstance;
	}
	
	/**
	 * Get the current version number for game catalog.
	 */
	public int getArcadoidDataVersionNumber() {
		return arcadoidDataVersionNumber;
	}

	/**
	 * Changes the current version number for game catalog.
	 * @param arcadoidDataVersionNumber New version number
	 */
	public void setArcadoidDataVersionNumber(int arcadoidDataVersionNumber) {
		this.arcadoidDataVersionNumber = arcadoidDataVersionNumber;
	}
	
	/**
	 * Increment by 1 the current version number for game catalog.
	 */
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
	
	/**
	 * Get all tags as an observable list.
	 */
	public ObservableList<Tag> getAllTags() {
		return this.allTags;
	}
	
	/**
	 * Retrieve a list of all tags, without given tags.
	 * @param tagsToIgnore The tags that must be in the received list.
	 * @return Observable list of tags.
	 */
	public ObservableList<Tag> getAllTagsExcept(List<Tag> tagsToIgnore) {
		ObservableList<Tag> relevantTags = FXCollections.observableArrayList();
		for (Tag tag : this.allTags) {
			if (!tagsToIgnore.contains(tag)) {
				relevantTags.add(tag);
			}
		}
		return relevantTags;
	}
	
	/**
	 * Get a specific tag.
	 * @param identifier Identifier of the required tag.
	 * @return Tag with given identifier, or null if not found.
	 */
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
	
	/**
	 * Delete given tag from the catalog.
	 * @param tag Tag to delete.
	 */
	public void deleteTag(Tag tag) {
		this.allTags.remove(tag);
	}
	
	/**
	 * Overwrites current list of tags.
	 * @param tags New list of tags.
	 */
	public void setAllTags(List<Tag> tags) {
		this.allTags.clear();
		this.allTags.addAll(tags);
		this.allTags.sort(BaseItem.defaultComparator());
		this.tagsByIdentifier.clear();
		for (Tag tag : tags) {
			this.tagsByIdentifier.put(tag.getIdentifier(), tag);
		}
	}
	
	/**
	 * Get all games in an observable list.
	 */
	public ObservableList<Game> getAllGames() {
		return this.allGames;
	}
	
	/**
	 * Get all games belonging to the given game platform.
	 * @param platform Platform for which a game list is requested.
	 * @return A list of games.
	 */
	public List<Game> getAllGamesForPlatform(Platform platform) {
		return this.allGames.filtered((game) -> game.getPlatform() == platform);
	}
	
	/**
	 * Get all games matching given navigation item.
	 * @param item The navigation item for which a game list is requested.
	 * @return A list of games.
	 */
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
		
	/**
	 * Creates a new Game object, by default for the MAME platform.
	 * @return A new Game object.
	 */
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
		case SNES:
			newGame = new SnesGame(currentGame);
			break;
		case FUSION:
			newGame = new FusionGame(currentGame);
			break;
		case NES:
			newGame = new NesGame(currentGame);
		}
		this.allGames.set(this.allGames.indexOf(currentGame), newGame);
		return newGame;
	}
	
	/**
	 * Deletes given game from the game catalog.
	 */
	public void deleteGame(Game game) {
		this.allGames.remove(game);
	}
	
	/**
	 * Overwrites list of all games in the game catalog.
	 * @param games The new list of games.
	 */
	public void setAllGames(List<Game> games) {
		this.allGames.clear();
		this.allGames.addAll(games);
		this.allGames.sort(BaseItem.defaultComparator());
	}
	
	/**
	 * Get observable list of all navigation items present on root level.

	 */
	public ObservableList<NavigationItem> getRootNavigationItems() {
		return this.rootNavigationItems;
	}
	
	/**
	 * Get simple list of all items that should be displayed at the root of a catalog representation.
	 */
	public List<BaseItem> getRootItems() {
		List<BaseItem> items = new ArrayList<BaseItem>();
		items.addAll(this.rootNavigationItems);
		return items;
	}
	
	/**
	 * Creates the complete catalog item tree and link it to the current root navigation items.
	 */
	public void buildCompleteCatalog() {
		for (NavigationItem navigationItem : this.rootNavigationItems) {
			this.fillChildrenForItem(navigationItem);
		}
	}
	
	private void fillChildrenForItem(BaseItem item) {
		if (item instanceof NavigationItem) {
			NavigationItem navigationItem = (NavigationItem)item;
			navigationItem.setAllChildItems(this.getChildrenForNavigationItem(navigationItem));
			for (BaseItem child : navigationItem.getAllChildItems()) {
				this.fillChildrenForItem(child);
			}
		}
	}
	
	private List<BaseItem> getChildrenForNavigationItem(NavigationItem navigationItem) {
		List<BaseItem> children = new ArrayList<BaseItem>();
		children.addAll(navigationItem.getSubItems());
		children.addAll(this.getAllGamesForNavigationItem(navigationItem));
		return children;
	}
	
	/**
	 * Get a list of all siblings of given navigation item.
	 * @param navigationItem Navigation item for which siblings are required.
	 * @return List of navigation items.
	 */
	public List<BaseItem> getSiblingsForNavigationItem(NavigationItem navigationItem) {
		List<BaseItem> siblings = new ArrayList<BaseItem>();
		NavigationItem parentItem = navigationItem.getParentItem();
		if (parentItem != null) {
			siblings.addAll(parentItem.getAllChildItems());
		} else {
			siblings.addAll(this.rootNavigationItems);
		}
		return siblings;
	}
	
	/**
	 * Creates a new navigation item with given parent. If parent is null, the newly created item will be considered root.
	 * @param parent Parent of navigation item to create. May be null.
	 * @return The newly created navigation item.
	 */
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
	
	/**
	 * Deletes given navigation item from the game catalog.
	 */
	public void deleteNavigationItem(NavigationItem navigationItem) {
		if (navigationItem.getParentItem() == null) {
			this.rootNavigationItems.remove(navigationItem);
		} else {
			navigationItem.setParentItem(null);
		}
	}
	
	/**
	 * Overwrites current list of root navigation items.
	 * @param rootItems New list of root navigation items.
	 */
	public void setRootNavigationItems(List<NavigationItem> rootItems) {
		this.rootNavigationItems.clear();
		this.rootNavigationItems.addAll(rootItems);
		this.rootNavigationItems.sort(BaseItem.defaultComparator());
		this.rootNavigationItems.forEach((item) -> {
			item.sortChildren();
		});
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
