package data.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MameGame extends Game {

	private StringProperty gameName = new SimpleStringProperty();
	
	public MameGame(long identifier) {
		super(identifier);
	}
	
	public MameGame(Game model) {
		super(model);
	}
	
	public void setGameName(String gameName) {
		this.gameName.setValue(gameName);
	}
	
	public String gameName() {
		return this.gameName.getValue();
	}
	
	public StringProperty getGameNameProperty() {
		return this.gameName;
	}

	@Override
	public Platform getPlatform() {
		return Platform.MAME;
	}
	
	@Override
	public void execute() {
		if (this.process != null) return;
//		String mamePath   = AppSettings.getSetting(AppSettings.PropertyId.MAME_PATH);
//		String executable = AppSettings.getSetting(AppSettings.PropertyId.MAME_PATH) + " " + _gameName;
//		try {
//			_process = Runtime.getRuntime().exec(executable, null, new File(mamePath).getParentFile());
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(4);
//		}
	}

	@Override
	public void terminate() {
		if (this.process != null) {
			this.process.destroy();
		}
		this.process = null;
	}

}
