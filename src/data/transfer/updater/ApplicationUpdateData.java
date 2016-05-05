package data.transfer.updater;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * Simple wrapper class for data required when updating an application. That includes the version
 * number of each of the executables, but also the additional resource files.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateData {

	public static final String DATA_KEY_FRONTEND = "arcadoid_frontend";
	public static final String DATA_KEY_UPDATER = "arcadoid_updater";
	public static final String DATA_KEY_EDITOR = "arcadoid_editor";
	public static final String DATA_KEY_VERSION = "version";
	public static final String DATA_KEY_FILES = "files";
	
	public int updaterVersionNumber, frontEndVersionNumber, editorVersionNumber;
	public Map<String, List<String>> updaterFiles, frontEndFiles, editorFiles;
	
	public static ApplicationUpdateData dataFromFile(String filePath) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ApplicationUpdateData.class, new ApplicationUpdateDataDeserializer());
		Gson gson = builder.create();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		ApplicationUpdateData updateData = gson.fromJson(jsonReader, ApplicationUpdateData.class);
		jsonReader.close();
		return updateData;
	}
	
}
