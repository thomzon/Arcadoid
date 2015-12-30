package data.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.access.ArcadoidData;
import data.model.Tag;

public class ArcadoidDataDeserializer implements JsonDeserializer<ArcadoidData> {

	@Override
	public ArcadoidData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		ArcadoidData.sharedInstance().setAllTags(this.deserializeTags(element.getAsJsonObject(), context));
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

}
