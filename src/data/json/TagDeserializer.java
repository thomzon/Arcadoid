package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.model.Tag;

public class TagDeserializer implements JsonDeserializer<Tag> {

	@Override
	public Tag deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = element.getAsJsonObject();
		long identifier = BaseItemSerializer.identifierFromObject(jsonObject);
		Tag tag = new Tag(identifier);
		BaseItemSerializer.deserializeFromObject(tag, jsonObject);
		return tag;
	}

}
