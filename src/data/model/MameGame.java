package data.model;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game running on the MAME platform.
 * @author Thomas Debouverie
 *
 */
public class MameGame extends Game {

	/**
	 * This is the name of the folder containing all ROM files for that game.
	 */
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
	
	/**
	 * MAME games are run using the base MAME emulator via command-line.
	 */
	@Override
	public void execute() throws Exception {
		String mamePath = Settings.getSetting(PropertyId.MAME_PATH);
		String executable = mamePath + " " + this.gameName();
		this.execute(executable, new File(mamePath).getParentFile());
	}

}
