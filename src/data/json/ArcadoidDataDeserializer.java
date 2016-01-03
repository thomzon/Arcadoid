package data.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.access.ArcadoidData;
import data.model.Game;
import data.model.NavigationItem;
import data.model.Tag;

public class ArcadoidDataDeserializer implements JsonDeserializer<ArcadoidData> {

	@Override
	public ArcadoidData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		ArcadoidData.sharedInstance().setAllTags(this.deserializeTags(element.getAsJsonObject(), context));
		ArcadoidData.sharedInstance().setAllGames(this.deserializeGames(element.getAsJsonObject(), context));
		ArcadoidData.sharedInstance().setRootNavigationItems(this.deserializeRootNavigationItems(element.getAsJsonObject(), context));
		return ArcadoidData.sharedInstance();
	}
	
	private List<Tag> deserializeTags(JsonObject object, JsonDeserializationContext context) {
		TagDeserializer deserializer = new TagDeserializer();
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		JsonArray array = object.get(JsonConstants.PROPERTY_TAGS).getAsJsonArray();
		for (JsonElement jsonElement : array) {
			tagList.add(deserializer.deserialize(jsonElement, Tag.class, context));
		}
		return tagList;
	}
	
	private List<Game> deserializeGames(JsonObject object, JsonDeserializationContext context) {
		GameDeserializer deserializer = new GameDeserializer();
		ArrayList<Game> gameList = new ArrayList<Game>();
		JsonArray array = object.get(JsonConstants.PROPERTY_GAMES).getAsJsonArray();
		for (JsonElement jsonElement : array) {
			gameList.add(deserializer.deserialize(jsonElement, Game.class, context));
		}
		return gameList;
	}
	
	private List<NavigationItem> deserializeRootNavigationItems(JsonObject object, JsonDeserializationContext context) {
		NavigationItemDeserializer deserializer = new NavigationItemDeserializer();
		ArrayList<NavigationItem> rootItems = new ArrayList<NavigationItem>();
		Map<Number, NavigationItem> itemsByIdentifier = new HashMap<Number, NavigationItem>();
		JsonArray array = object.get(JsonConstants.PROPERTY_NAVIGATION_ITEMS).getAsJsonArray();
		for (JsonElement jsonElement : array) {
			NavigationItem item = deserializer.deserialize(jsonElement, NavigationItem.class, context);
			long parentItemIdentifier = deserializer.parentItemIdentifier(jsonElement);
			if (parentItemIdentifier != 0) {
				NavigationItem parentItem = itemsByIdentifier.get(parentItemIdentifier);
				item.setParentItem(parentItem);
			} else {
				rootItems.add(item);
			}
			itemsByIdentifier.put(item.getIdentifier(), item);
		}
		return rootItems;
	}

}
