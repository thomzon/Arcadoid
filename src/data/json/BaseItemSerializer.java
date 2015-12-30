package data.json;

import com.google.gson.JsonObject;

import data.model.BaseItem;

public class BaseItemSerializer {

	protected static void serializeInObject(BaseItem item, JsonObject jsonObject) {
		jsonObject.addProperty(JsonConstants.PROPERTY_ID, item.getIdentifier());
		jsonObject.addProperty(JsonConstants.PROPERTY_NAME, item.getName());
		jsonObject.addProperty(JsonConstants.PROPERTY_THUMBNAIL_PATH, item.getThumbnailArtworkPath());
		jsonObject.addProperty(JsonConstants.PROPERTY_BACKGROUND_PATH, item.getBackgroundArtworkPath());
	}
	
	protected static long identifierFromObject(JsonObject jsonObject) {
		return jsonObject.get(JsonConstants.PROPERTY_ID).getAsLong();
	}
	
	protected static void deserializeFromObject(BaseItem item, JsonObject jsonObject) {
		item.setName(jsonObject.get(JsonConstants.PROPERTY_NAME).getAsString());
		item.setThumbnailArtworkPath(jsonObject.get(JsonConstants.PROPERTY_THUMBNAIL_PATH).getAsString());
		item.setBackgroundArtworkPath(jsonObject.get(JsonConstants.PROPERTY_BACKGROUND_PATH).getAsString());
	}

}
