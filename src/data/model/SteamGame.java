package data.model;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game running on the Steam platform.
 * @author Thomas Debouverie
 *
 */
public class SteamGame extends Game {

	/**
	 * This is the Steam application ID.
	 */
	private final StringProperty appId = new SimpleStringProperty();
	
	/**
	 * This is the name of the process that is started by the game. It can be found in the Task Manager when the game is running.
	 */
	private final StringProperty processName = new SimpleStringProperty();
	
	public SteamGame(long identifier) {
		super(identifier);
	}
	
	public SteamGame(Game model) {
		super(model);
	}

	public void setAppId(String appId) {
		this.appId.setValue(appId);
	}
	
	public String appId() {
		return this.appId.getValue();
	}
	
	public StringProperty getAppIdProperty() {
		return this.appId;
	}
	
	public void setProcessName(String processName) {
		this.processName.setValue(processName);
	}
	
	public String processName() {
		return this.processName.getValue();
	}
	
	public StringProperty getProcessNameProperty() {
		return this.processName;
	}
	
	@Override
	public Platform getPlatform() {
		return Platform.STEAM;
	}

	/**
	 * Running a Steam game is just a command line to the Steam app, with the -applaunch command and the relevant Steam application ID.
	 * @throws Exception 
	 */
	@Override
	public void execute() throws Exception {
		String executable = Settings.getSetting(PropertyId.STEAM_PATH) + " -applaunch " + this.appId();
		this.execute(executable, null);
	}

	/**
	 * To terminate a Steam game, the Steam process started must be killed, but the actual Windows process specific to the game must also be killed.
	 */
	@Override
	public void terminate() throws Exception {
		super.terminate();
		if (this.processName() == null) return;
		Runtime.getRuntime().exec("taskkill /im " + this.processName() +" /f");
	}

}
