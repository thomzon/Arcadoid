package data.model;

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

	/**
	 * Terminating a MAME emulator process is as simple as killing it.
	 */
	@Override
	public void terminate() {
		if (this.process != null) {
			this.process.destroy();
		}
		this.process = null;
	}

}
