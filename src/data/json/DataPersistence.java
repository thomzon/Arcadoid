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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import data.access.ArcadoidData;

public class DataPersistence {

	public static void saveDataToFile(ArcadoidData data, String filePath) throws UnsupportedEncodingException, IOException, FileNotFoundException, IllegalStateException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.registerTypeAdapter(ArcadoidData.class, new ArcadoidDataSerializer());
		Gson gson = gsonBuilder.create();
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		JsonWriter jsonWriter = new JsonWriter(writer);
		gson.toJson(data, data.getClass(), jsonWriter);
		jsonWriter.close();
	}
	
	public static void loadDataFromFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ArcadoidData.class, new ArcadoidDataDeserializer());
		Gson gson = builder.create();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		gson.fromJson(jsonReader, ArcadoidData.class);
	}

}