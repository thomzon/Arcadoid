package data.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import data.access.ArcadoidData;
import data.access.FrontendData;

/**
 * Public access to saving and loading data from and to file.
 * Uses GSON to serialize and deserialize data in JSON format.
 * @author Thomas Debouverie
 *
 */
public class DataPersistence {

	public static void saveArcadoidDataToFile(ArcadoidData data, String filePath) throws IOException {
		DataPersistence.saveDataToFileWithSerializer(data, filePath, new ArcadoidDataSerializer());
	}
	
	public static void saveFrontendDataToFile(FrontendData data, String filePath) throws IOException {
		DataPersistence.saveDataToFileWithSerializer(data, filePath, new FrontendDataSerializer());
	}
	
	private static <T> void saveDataToFileWithSerializer(Object data, String filePath, JsonSerializer<T> serializer) throws IOException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.registerTypeAdapter(data.getClass(), serializer);
		Gson gson = gsonBuilder.create();
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		JsonWriter jsonWriter = new JsonWriter(writer);
		gson.toJson(data, data.getClass(), jsonWriter);
		jsonWriter.close();
	}
	
	public static void loadArcadoidDataFromFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException {
		DataPersistence.loadDataFromFileWithDeserializer(ArcadoidData.class, filePath, new ArcadoidDataDeserializer());
	}
	
	public static void loadFrontendDataFromFile(String filePath) throws IOException {
		DataPersistence.loadDataFromFileWithDeserializer(FrontendData.class, filePath, new FrontendDataDeserializer());
	}
	
	private static <T> void loadDataFromFileWithDeserializer(Class<T> dataClass, String filePath, JsonDeserializer<T> deserializer) throws UnsupportedEncodingException, FileNotFoundException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(dataClass, deserializer);
		Gson gson = builder.create();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		gson.fromJson(jsonReader, ArcadoidData.class);
	}
	
	public static int getVersionNumberFromFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		int versionNumber = -1;
		try {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String fieldName = jsonReader.nextName();
				if (fieldName != null && fieldName.equals(JsonConstants.PROPERTY_VERSION_NUMBER)) {
					versionNumber = jsonReader.nextInt();
				}
			}
		} catch (Exception e) {
		} finally {
			jsonReader.close();
		}
		return versionNumber;
	}

}
