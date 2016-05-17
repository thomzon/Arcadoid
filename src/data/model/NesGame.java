package data.model;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NesGame extends Game {

	/**
	 * Exact rom file name.
	 */
	private StringProperty romFileName = new SimpleStringProperty();
	
	public NesGame(long identifier) {
		super(identifier);
	}
	
	public NesGame(Game model) {
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
		return Platform.NES;
	}

	@Override
	public void execute() throws Exception {
		String rocknesxPath = Settings.getSetting(PropertyId.ROCKNESX_PATH);
		String romsPath = Settings.getSetting(PropertyId.NES_ROMS_FOLDER_PATH);
		File romsFolder = new File(romsPath);
		File romFile = new File(romsFolder, this.romFileName());
		String executable = "\"" + rocknesxPath + "\" \"" + romFile.getAbsolutePath() + "\"";
		this.execute(executable, null);
	}

}
