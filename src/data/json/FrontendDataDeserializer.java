package data.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.access.FrontendData;

/**
 * JsonDeserializer implementation to handle FrontendData object.
 * No new object is actually created during deserialization.
 * The FrontendData singleton is directly updated from data found in the JSON element.
 * @author Thomas Debouverie
 *
 */
public class FrontendDataDeserializer implements JsonDeserializer<FrontendData> {

	@Override
	public FrontendData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		this.deserializeFavorites(element.getAsJsonObject());
		this.deserializeSeenGames(element.getAsJsonObject());
		return FrontendData.sharedInstance();
	}
	
	private void deserializeFavorites(JsonObject object) {
		List<Long> gameIdentifiers = this.deserializeLongListWithName(object, JsonConstants.PROPERTY_FAVORITES);
		FrontendData.sharedInstance().setAllFavorites(gameIdentifiers);
	}
	
	private void deserializeSeenGames(JsonObject object) {
		List<Long> gameIdentifiers = this.deserializeLongListWithName(object, JsonConstants.PROPERTY_SEEN_GAMES);
		FrontendData.sharedInstance().setAllSeenGamesIdentifiers(gameIdentifiers);
	}
	
	private List<Long> deserializeLongListWithName(JsonObject object, String elementName) {
		List<Long> gameIdentifiers = new ArrayList<Long>();
		JsonArray array = object.getAsJsonArray(elementName);
		for (JsonElement jsonElement : array) {
			long gameIdentifier = jsonElement.getAsLong();
			gameIdentifiers.add(gameIdentifier);
		}
		return gameIdentifiers;
	}
	
}
