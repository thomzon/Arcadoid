package data.model;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SteamGame extends Game {

	private final StringProperty appId = new SimpleStringProperty();
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

	@Override
	public void execute() {
		if (this.process != null) return;
//		String executable = AppSettings.getSetting(AppSettings.PropertyId.STEAM_PATH) + " -applaunch " + _appId;
//		try {
//			_process = Runtime.getRuntime().exec(executable);
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(4);
//		}
	}

	@Override
	public void terminate() {
		if (this.process != null) this.process.destroy();
		this.process = null;
		if (this.processName() == null) return;
		try {
			Runtime.getRuntime().exec("taskkill /im " + this.processName() +" /f");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(4);
		}
	}

}
