package data.transfer.updater;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ApplicationUpdateDataDeserializer implements JsonDeserializer<ApplicationUpdateData> {

	@Override
	public ApplicationUpdateData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject rootObject = element.getAsJsonObject();
		JsonObject frontEndObject = rootObject.get(ApplicationUpdateData.DATA_KEY_FRONTEND).getAsJsonObject();
		JsonObject updaterObject = rootObject.get(ApplicationUpdateData.DATA_KEY_UPDATER).getAsJsonObject();
		JsonObject editorObject = rootObject.get(ApplicationUpdateData.DATA_KEY_EDITOR).getAsJsonObject();
		
		ApplicationUpdateData updateData = new ApplicationUpdateData();
		updateData.frontEndVersionNumber = frontEndObject.get(ApplicationUpdateData.DATA_KEY_VERSION).getAsInt();
		updateData.updaterVersionNumber = updaterObject.get(ApplicationUpdateData.DATA_KEY_VERSION).getAsInt();
		updateData.editorVersionNumber = editorObject.get(ApplicationUpdateData.DATA_KEY_VERSION).getAsInt();
		updateData.frontEndFiles = this.filesFromApplicationObject(frontEndObject);
		updateData.updaterFiles = this.filesFromApplicationObject(updaterObject);
		updateData.editorFiles = this.filesFromApplicationObject(editorObject);
		
		return updateData;
	}
	
	public Map<String, List<String>> filesFromApplicationObject(JsonObject applicationObject) {
		Map<String, List<String>> filesMap = new HashMap<String, List<String>>();
		JsonElement filesElement = applicationObject.get(ApplicationUpdateData.DATA_KEY_FILES);
		if (filesElement == null) return filesMap;
		
		JsonObject filesObject = filesElement.getAsJsonObject();
		for (Entry<String, JsonElement> fileElement : filesObject.entrySet()) {
			String directoryName = fileElement.getKey();
			List<String> fileNames = new ArrayList<String>();
			JsonArray fileList = fileElement.getValue().getAsJsonArray();
			for (JsonElement file : fileList) {
				String fileName = file.getAsString();
				fileNames.add(fileName);
			}
			filesMap.put(directoryName, fileNames);
		}
		return filesMap;
	}

}
