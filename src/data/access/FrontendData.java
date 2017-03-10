package data.access;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import data.json.DataPersistence;
import data.model.Game;

public class FrontendData {

	private static FrontendData sharedInstance = null;
	/**
	 * Local path to the favorites and seen game IDs data.
	 */
	public static final String LOCAL_DATA_FILE_PATH = "local_data.json";
	
	private List<Long> favoritesIdentifiers = new ArrayList<Long>();
	private List<Long> seenGamesIdentifiers = new ArrayList<Long>();
	
	private FrontendData() {
	}

	/**
	 * Singleton access.
	 * @return ArcadoidData unique instance.
	 */
	public static FrontendData sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new FrontendData();
		}
		return sharedInstance;
	}
	
	/**
	 * Add given game to list of favorites.
	 * @param game Game to add to favorite list.
	 */
	public void addFavorite(Game game) {
		this.favoritesIdentifiers.add(game.getIdentifier());
	}
	
	/**
	 * Checks if given game is in favorite list.
	 * @param game Game to check.
	 * @return True if given game is in favorite list.
	 */
	public boolean isFavorite(Game game) {
		return this.favoritesIdentifiers.contains(game.getIdentifier());
	}
	
	/**
	 * Remove given game from list of favorites.
	 * @param game Game to remove from favorite list.
	 */
	public void removeFavorite(Game game) {
		this.favoritesIdentifiers.remove(game.getIdentifier());
	}
	
	/**
	 * Get all game identifiers for favorite games.
	 * @return Favorite game identifiers list.
	 */
	public List<Long> getAllFavoriteGameIdentifiers() {
		return new ArrayList<>(this.favoritesIdentifiers);
	}
	
	/**
	 * Get all games marked as favorite.
	 * @return Favorite list.
	 */
	public List<Game> getAllFavorites() {
		List<Game> favoriteGames = new ArrayList<Game>();
		for (Game game : ArcadoidData.sharedInstance().getAllGames()) {
			if (this.favoritesIdentifiers.contains(game.getIdentifier())) {
				favoriteGames.add(game);
			}
		}
		return favoriteGames;
	}
	
	/**
	 * Replaces all current favorites by given IDs.
	 * @param favorites New favorite list.
	 */
	public void setAllFavorites(List<Long> favorites) {
		this.favoritesIdentifiers = new ArrayList<>(favorites);
	}
	
	/**
	 * Mark given game as being seen.
	 * @param game Game to add to seen list.
	 */
	public void markGameAsSeen(Game game) {
		this.seenGamesIdentifiers.add(game.getIdentifier());
	}
	
	/**
	 * Get all games that were never seen.
	 * @return Unseen games list.
	 */
	public List<Game> getAllUnseenGames() {
		List<Game> unseenGames = new ArrayList<Game>();
		for (Game game : ArcadoidData.sharedInstance().getAllGames()) {
			if (!this.seenGamesIdentifiers.contains(game.getIdentifier())) {
				unseenGames.add(game);
			}
		}
		return unseenGames;
	}
	
	/**
	 * Get all seen game identifiers.
	 * @return Seen game identifiers list.
	 */
	public List<Long> getAllSeenGameIdentifiers() {
		return new ArrayList<>(this.seenGamesIdentifiers);
	}
	
	/**
	 * Replaces list of seen game identifiers with given one.
	 * @param seenGameIdentifiers New seen games list.
	 */
	public void setAllSeenGamesIdentifiers(List<Long> seenGameIdentifiers) {
		this.seenGamesIdentifiers = new ArrayList<>(seenGameIdentifiers);
	}
	
	/**
	 * Saves all data to a serialized JSON file. Exceptions are forwarded from the DataPersitence layer.
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalStateException
	 */
	public void saveData() throws UnsupportedEncodingException, IOException, FileNotFoundException, IllegalStateException {
		DataPersistence.saveFrontendDataToFile(this, LOCAL_DATA_FILE_PATH);
	}
	
	/**
	 * Reads the serialized JSON file and replace all current data with the parsed data.
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadData() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		DataPersistence.loadFrontendDataFromFile(LOCAL_DATA_FILE_PATH);
	}
	
}
