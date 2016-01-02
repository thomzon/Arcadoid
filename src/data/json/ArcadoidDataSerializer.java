package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.access.ArcadoidData;
import data.model.Game;
import data.model.Tag;

public class ArcadoidDataSerializer implements JsonSerializer<ArcadoidData> {

	@Override
	public JsonElement serialize(ArcadoidData src, Type srcType, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		this.serializeTags(src, jsonObject, context);
		this.serializeGames(src, jsonObject, context);
		return jsonObject;
	}
	
	private void serializeTags(ArcadoidData src, JsonObject jsonObject, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		TagSerializer serializer = new TagSerializer();
		for (Tag tag : src.getAllTags()) {
			array.add(serializer.serialize(tag, Tag.class, context));
		}
		jsonObject.add(JsonConstants.PROPERTY_TAGS, array);
	}
	
	private void serializeGames(ArcadoidData src, JsonObject jsonObject, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		GameSerializer serializer = new GameSerializer();
		for (Game game : src.getAllGames()) {
			array.add(serializer.serialize(game, Game.class, context));
		}
		jsonObject.add(JsonConstants.PROPERTY_GAMES, array);
	}

}
