package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.model.Game;
import data.model.MameGame;
import data.model.SteamGame;
import data.model.Tag;

/**
 * Transforms Game objects into JsonElement objects.
 * @author Thomas Debouverie
 *
 */
public class GameSerializer implements JsonSerializer<Game> {

	@Override
	public JsonElement serialize(Game src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		BaseItemSerializer.serializeInObject(src, jsonObject);
		jsonObject.addProperty(JsonConstants.PROPERTY_PLATFORM, src.getPlatform().intValue);
		this.serializePlatformSpecificData(src, jsonObject);
		JsonArray tagsArray = new JsonArray();
		for (Tag tag : src.getAssignedTags()) {
			tagsArray.add(tag.getIdentifier());
		}
		jsonObject.add(JsonConstants.PROPERTY_TAGS, tagsArray);
		return jsonObject;
	}
	
	private void serializePlatformSpecificData(Game src, JsonObject jsonObject) {
		this.serializeMameSpecificData(src, jsonObject);
		this.serializeSteamSpecificData(src, jsonObject);
	}
	
	private void serializeMameSpecificData(Game src, JsonObject jsonObject) {
		if (src instanceof MameGame) {
			MameGame mameGame = (MameGame)src;
			jsonObject.addProperty(JsonConstants.PROPERTY_MAME_ROM_NAME, mameGame.gameName());
		}
	}
	
	private void serializeSteamSpecificData(Game src, JsonObject jsonObject) {
		if (src instanceof SteamGame) {
			SteamGame steamGame = (SteamGame)src;
			jsonObject.addProperty(JsonConstants.PROPERTY_STEAM_APP_ID, steamGame.appId());
			jsonObject.addProperty(JsonConstants.PROPERTY_STEAM_PROCESS_NAME, steamGame.processName());
		}
	}

}
