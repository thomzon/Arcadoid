package data.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.model.Tag;

public class TagSerializer implements JsonSerializer<Tag> {

	public JsonElement serialize(Tag src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		BaseItemSerializer.serializeInObject(src, jsonObject);
		return jsonObject;
	}

}
