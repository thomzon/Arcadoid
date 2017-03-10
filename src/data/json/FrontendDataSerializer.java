package data.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.access.FrontendData;

public class FrontendDataSerializer implements JsonSerializer<FrontendData> {

	@Override
	public JsonElement serialize(FrontendData src, Type srcType, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		this.serializeFavorites(src, jsonObject);
		this.serializeSeenGames(src, jsonObject);
		return jsonObject;
	}
	
	private void serializeFavorites(FrontendData src, JsonObject jsonObject) {
		List<Long> favoriteGameIdentifiers = src.getAllFavoriteGameIdentifiers();
		this.serializeIdentifiersListForPropertyName(jsonObject, favoriteGameIdentifiers, JsonConstants.PROPERTY_FAVORITES);
	}
	
	private void serializeSeenGames(FrontendData src, JsonObject jsonObject) {
		List<Long> favoriteGameIdentifiers = src.getAllSeenGameIdentifiers();
		this.serializeIdentifiersListForPropertyName(jsonObject, favoriteGameIdentifiers, JsonConstants.PROPERTY_SEEN_GAMES);
	}
	
	private void serializeIdentifiersListForPropertyName(JsonObject jsonObject, List<Long> identifiersList, String propertyName) {
		JsonArray array = new JsonArray();
		for (Long identifier : identifiersList) {
			array.add(identifier);
		}
		jsonObject.add(propertyName, array);
	}
	
}
