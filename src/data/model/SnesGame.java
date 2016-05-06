package data.model;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game running on the SuperNes platform.
 * @author Thomas Debouverie
 *
 */
public class SnesGame extends Game {

	/**
	 * Exact rom file name.
	 */
	private StringProperty romFileName = new SimpleStringProperty();
	
	public SnesGame(long identifier) {
		super(identifier);
	}
	
	public SnesGame(Game model) {
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
		return Platform.SNES;
	}

	@Override
	public void execute() throws Exception {
		String snes9xPath = Settings.getSetting(PropertyId.SNES9X_PATH);
		String romsPath = Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
		File romsFolder = new File(romsPath);
		File romFile = new File(romsFolder, this.romFileName());
		String executable = "\"" + snes9xPath + "\" \"" + romFile.getAbsolutePath() + "\" -fullscreen";
		this.execute(executable, null);
	}

}
