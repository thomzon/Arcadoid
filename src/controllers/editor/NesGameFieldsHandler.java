package controllers.editor;

import java.util.ArrayList;
import java.util.List;

import data.model.NesGame;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.stage.FileChooser.ExtensionFilter;

public class NesGameFieldsHandler extends SingleRomFileGameFieldsHandler {

	private NesGame getNesGame() {
		if (this.getEditedGame() instanceof NesGame) {
			return (NesGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	@Override
	protected String getRomsFolderPath() {
		return Settings.getSetting(PropertyId.NES_ROMS_FOLDER_PATH);
	}
	
	@Override
	protected List<ExtensionFilter> getAllExtensionFilters() {
		List<ExtensionFilter> filters = new ArrayList<ExtensionFilter>();
		List<String> extensions = new ArrayList<String>();
		extensions.add("*.nes");
		filters.add(new ExtensionFilter(Messages.get("field.nesRomFileDescription"), extensions));
		return filters;
	}

	@Override
	protected String getRomFileName() {
		return this.getNesGame() != null ? this.getNesGame().romFileName() : null;
	}

	@Override
	protected void setNewRomFileName(String romFileName) {
		this.getNesGame().setRomFileName(romFileName);
	}

	@Override
	protected void clearRomFileName() {
		this.getNesGame().setRomFileName("");
	}

}
