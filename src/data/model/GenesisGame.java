package data.model;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game running on the Sega Genesis platform.
 * @author Thomas Debouverie
 *
 */
public class GenesisGame extends Game {

	/**
	 * Exact rom file name.
	 */
	private StringProperty romFileName = new SimpleStringProperty();
	
	public GenesisGame(long identifier) {
		super(identifier);
	}
	
	public GenesisGame(Game model) {
		super(model);
	}
	
	public void setRomFileName(String romFileName) {
		this.romFileName.setValue(romFileName);
	}
	
	public String romFileName() {
		return this.romFileName.getValue();
	}
	
	public StringProperty getromFileNameProperty() {
		return this.romFileName;
	}
	
	@Override
	public Platform getPlatform() {
		return Platform.GENESIS;
	}

	@Override
	public void execute() {
		String kegaPath = Settings.getSetting(PropertyId.KEGA_PATH);
		String romsPath = Settings.getSetting(PropertyId.GENESIS_ROMS_FOLDER_PATH);
		File romsFolder = new File(romsPath);
		File romFile = new File(romsFolder, this.romFileName());
		String executable = "\"" + kegaPath + "\" \"" + romFile.getAbsolutePath() + "\"";
		this.execute(executable, null);
	}

}
