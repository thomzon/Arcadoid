package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.access.ArcadoidData;
import data.model.NavigationItem;
import data.model.Tag;

/**
 * Transforms JsonElement objects into NavigationItem objects.
 * It will not handle deserialization of children NavigationItems, this is done at the ArcaoidDataDeserializer level.
 * @author Thomas Debouverie
 *
 */
public class NavigationItemDeserializer implements JsonDeserializer<NavigationItem> {

	@Override
	public NavigationItem deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {		
		JsonObject jsonObject = element.getAsJsonObject();
		long identifier = BaseItemSerializer.identifierFromObject(jsonObject);
		NavigationItem item = new NavigationItem(identifier);
		BaseItemSerializer.deserializeFromObject(item, jsonObject);
		item.setGamesMustMatchAllTags(this.getBooleanIfExistsForPropertyName(jsonObject, JsonConstants.PROPERTY_MUST_MATCH_ALL_TAGS, false));
		item.setShowEligibleGames(this.getBooleanIfExistsForPropertyName(jsonObject, JsonConstants.PROPERTY_SHOW_GAMES, false));
		item.setFavorites(this.getBooleanIfExistsForPropertyName(jsonObject, JsonConstants.PROPERTY_IS_FAVORITES, false));
		item.setUnseenGames(this.getBooleanIfExistsForPropertyName(jsonObject, JsonConstants.PROPERTY_IS_NEW_GAMES, false));
		this.deserializeAssignedTags(item, jsonObject);
		return item;
	}
	
	public long parentItemIdentifier(JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		return jsonObject.get(JsonConstants.PROPERTY_PARENT_NAVIGATION_ITEM).getAsLong();
	}
	
	private void deserializeAssignedTags(NavigationItem item, JsonObject jsonObject) {
		JsonElement tagsElement = jsonObject.get(JsonConstants.PROPERTY_TAGS);
		if (tagsElement == null) return;
		JsonArray tagsArray = tagsElement.getAsJsonArray();
		for (JsonElement jsonElement : tagsArray) {
			long tagIdentifier = jsonElement.getAsLong();
			Tag tag = ArcadoidData.sharedInstance().getTagByIdentifier(tagIdentifier);
			if (tag != null) {
				item.getAssignedTags().add(tag);
			}
		}
	}
	
	private boolean getBooleanIfExistsForPropertyName(JsonObject object, String propertyName, boolean defaultValue) {
		JsonElement element = object.get(propertyName);
		if (element == null) {
			return defaultValue;
		} else {
			return element.getAsBoolean();
		}
	}

}
