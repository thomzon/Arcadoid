package data.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.access.ArcadoidData;
import data.model.Game;
import data.model.NavigationItem;
import data.model.Tag;

/**
 * JsonSerializer implementation to handle ArcadoidData object.
 * @author Thomas Debouverie
 *
 */
public class ArcadoidDataSerializer implements JsonSerializer<ArcadoidData> {

	@Override
	public JsonElement serialize(ArcadoidData src, Type srcType, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		this.serializeVersionNumber(src, jsonObject);
		this.serializeTags(src, jsonObject, context);
		this.serializeGames(src, jsonObject, context);
		this.serializeNavigationItems(src, jsonObject, context);
		return jsonObject;
	}
	
	private void serializeVersionNumber(ArcadoidData src, JsonObject jsonObject) {
		jsonObject.addProperty(JsonConstants.PROPERTY_VERSION_NUMBER, src.getArcadoidDataVersionNumber());
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
	
	/**
	 * Recursively serializes the whole NavigationItem hierarchy for the given ArcadoidData instance.
	 * @param src ArcadoidData instance.
	 * @param jsonObject JsonObject that must contain the serialization result.
	 * @param context Current JsonSerializationContext.
	 */
	private void serializeNavigationItems(ArcadoidData src, JsonObject jsonObject, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		NavigationItemSerializer serializer = new NavigationItemSerializer();
		this.serializeNavigationItems(src.getRootNavigationItems(), serializer, array, context);
		jsonObject.add(JsonConstants.PROPERTY_NAVIGATION_ITEMS, array);
	}
	
	private void serializeNavigationItems(List<NavigationItem> navigationItems, NavigationItemSerializer serializer, JsonArray jsonArray, JsonSerializationContext context) {
		for (NavigationItem navigationItem : navigationItems) {
			jsonArray.add(serializer.serialize(navigationItem, NavigationItem.class, context));
			this.serializeNavigationItems(navigationItem.getSubItems(), serializer, jsonArray, context);
		}
	}

}
