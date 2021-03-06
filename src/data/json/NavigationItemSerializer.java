package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.model.NavigationItem;
import data.model.Tag;

/**
 * Transforms NavigationItem objects into JsonElement objects.
 * It will not handle serialization of children NavigationItems, this is done at the ArcaoidDataSerializer level.
 * @author Thomas Debouverie
 *
 */
public class NavigationItemSerializer implements JsonSerializer<NavigationItem> {

	@Override
	public JsonElement serialize(NavigationItem src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		BaseItemSerializer.serializeInObject(src, jsonObject);
		if (src.getParentItem() != null) {
			jsonObject.addProperty(JsonConstants.PROPERTY_PARENT_NAVIGATION_ITEM, src.getParentItem().getIdentifier());
		} else {
			jsonObject.addProperty(JsonConstants.PROPERTY_PARENT_NAVIGATION_ITEM, 0);
		}
		jsonObject.addProperty(JsonConstants.PROPERTY_SHOW_GAMES, src.getShowEligibleGames());
		jsonObject.addProperty(JsonConstants.PROPERTY_MUST_MATCH_ALL_TAGS, src.getGamesMustMatchAllTags());
		jsonObject.addProperty(JsonConstants.PROPERTY_IS_FAVORITES, src.isFavorites());
		jsonObject.addProperty(JsonConstants.PROPERTY_IS_NEW_GAMES, src.isUnseenGames());
		JsonArray tagsArray = new JsonArray();
		for (Tag tag : src.getAssignedTags()) {
			tagsArray.add(tag.getIdentifier());
		}
		jsonObject.add(JsonConstants.PROPERTY_TAGS, tagsArray);
		return jsonObject;
	}

}
