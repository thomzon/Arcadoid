package data.transfer.updater;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;

public class ApplicationUpdateData {

	private static final String DATA_KEY_FRONTEND = "arcadoid_frontend";
	private static final String DATA_KEY_UPDATER = "arcadoid_updater";
	private static final String DATA_KEY_EDITOR = "arcadoid_editor";
	private static final String DATA_KEY_VERSION = "version";
	private static final String DATA_KEY_FILES = "files";
	
	public int updaterVersionNumber, frontEndVersionNumber, editorVersionNumber;
	public Map<String, List<String>> updaterFiles, frontEndFiles, editorFiles;
	
	public static ApplicationUpdateData dataFromFile(String filePath) throws IOException {
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		ApplicationUpdateData data = new ApplicationUpdateData();
		data.updaterVersionNumber = retrieveVersionNumberForKey(DATA_KEY_UPDATER, jsonReader);
		data.frontEndVersionNumber = retrieveVersionNumberForKey(DATA_KEY_FRONTEND, jsonReader);
		data.editorVersionNumber = retrieveVersionNumberForKey(DATA_KEY_EDITOR, jsonReader);
		data.updaterFiles = retrieveFilesForKey(DATA_KEY_UPDATER, jsonReader);
		data.frontEndFiles = retrieveFilesForKey(DATA_KEY_FRONTEND, jsonReader);
		data.editorFiles = retrieveFilesForKey(DATA_KEY_EDITOR, jsonReader);
		return data;
	}
	
	private static int retrieveVersionNumberForKey(String key, JsonReader jsonReader) throws IOException {
		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String fieldName = jsonReader.nextName();
			if (fieldName != null && fieldName.equals(key)) {
				jsonReader.beginObject();
				while (jsonReader.hasNext()) {
					fieldName = jsonReader.nextName();
					if (fieldName != null && fieldName.equals(DATA_KEY_VERSION)) {
						jsonReader.close();
						return jsonReader.nextInt();
					}
				}
			}
		}
		jsonReader.close();
		return 0;
	}
	
	private static Map<String, List<String>> retrieveFilesForKey(String key, JsonReader jsonReader) throws IOException {
		Map<String, List<String>> filesMap = new HashMap<String, List<String>>();
		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String fieldName = jsonReader.nextName();
			if (fieldName != null && fieldName.equals(key)) {
				jsonReader.beginObject();
				while (jsonReader.hasNext()) {
					fieldName = jsonReader.nextName();
					if (fieldName != null && fieldName.equals(DATA_KEY_FILES)) {
						jsonReader.beginObject();
						while (jsonReader.hasNext()) {
							String directoryName = jsonReader.nextName();
							List<String> files = new ArrayList<String>();
							jsonReader.beginArray();
							while (jsonReader.hasNext()) {
								files.add(jsonReader.nextString());
							}
							jsonReader.endArray();
							filesMap.put(directoryName, files);
						}
					}
				}
			}
		}
		jsonReader.close();
		return filesMap;
	}
}
