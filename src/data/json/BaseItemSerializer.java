package data.json;

import com.google.gson.JsonObject;

import data.model.BaseItem;

/**
 * Offers static methods to transfer BaseItem properties in a JsonObject, and vice versa.
 * @author Thomas Debouverie
 *
 */
class BaseItemSerializer {

	static void serializeInObject(BaseItem item, JsonObject jsonObject) {
		jsonObject.addProperty(JsonConstants.PROPERTY_ID, item.getIdentifier());
		jsonObject.addProperty(JsonConstants.PROPERTY_NAME, item.getName());
		jsonObject.addProperty(JsonConstants.PROPERTY_THUMBNAIL_PATH, item.getThumbnailArtworkPath());
		jsonObject.addProperty(JsonConstants.PROPERTY_BACKGROUND_PATH, item.getBackgroundArtworkPath());
	}
	
	static long identifierFromObject(JsonObject jsonObject) {
		return jsonObject.get(JsonConstants.PROPERTY_ID).getAsLong();
	}
	
	static void deserializeFromObject(BaseItem item, JsonObject jsonObject) {
		item.setName(jsonObject.get(JsonConstants.PROPERTY_NAME).getAsString());
		item.setThumbnailArtworkPath(jsonObject.get(JsonConstants.PROPERTY_THUMBNAIL_PATH).getAsString());
		item.setBackgroundArtworkPath(jsonObject.get(JsonConstants.PROPERTY_BACKGROUND_PATH).getAsString());
	}

}
