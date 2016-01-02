package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.access.ArcadoidData;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.model.SteamGame;
import data.model.Tag;

public class GameDeserializer implements JsonDeserializer<Game> {

	@Override
	public Game deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = element.getAsJsonObject();
		long identifier = BaseItemSerializer.identifierFromObject(jsonObject);
		Game game = this.createPlatformSpecificGameObject(identifier, jsonObject);
		BaseItemSerializer.deserializeFromObject(game, jsonObject);
		this.deserializeAssignedTags(game, jsonObject);
		return game;
	}
	
	private void deserializeAssignedTags(Game game, JsonObject jsonObject) {
		JsonArray tagsArray = jsonObject.get(JsonConstants.PROPERTY_TAGS).getAsJsonArray();
		for (JsonElement jsonElement : tagsArray) {
			long tagIdentifier = jsonElement.getAsLong();
			Tag tag = ArcadoidData.sharedInstance().getTagByIdentifier(tagIdentifier);
			if (tag != null) {
				game.getAssignedTags().add(tag);
			}
		}
	}
	
	private Game createPlatformSpecificGameObject(long identifier, JsonObject jsonObject) {
		int platformId = jsonObject.get(JsonConstants.PROPERTY_PLATFORM).getAsInt();
		Platform platform = Platform.values()[platformId-1];
		switch (platform) {
			case MAME:
				return this.createMameGame(identifier, jsonObject);
			case STEAM:
				return this.createSteamGame(identifier, jsonObject);
			default:
				return null;
		}
	}
	
	private MameGame createMameGame(long identifier, JsonObject jsonObject) {
		MameGame mameGame = new MameGame(identifier);
		mameGame.setGameName(jsonObject.get(JsonConstants.PROPERTY_MAME_ROM_NAME).getAsString());
		return mameGame;
	}
	
	private SteamGame createSteamGame(long identifier, JsonObject jsonObject) {
		SteamGame steamGame = new SteamGame(identifier);
		steamGame.setAppId(jsonObject.get(JsonConstants.PROPERTY_STEAM_APP_ID).getAsString());
		steamGame.setProcessName(jsonObject.get(JsonConstants.PROPERTY_STEAM_PROCESS_NAME).getAsString());
		return steamGame;
	}

}
