package controllers.editor;

import data.model.SnesGame;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Concrete SingleRomFileGameFieldsHandler implementation to handle SuperNes games specific fields.
 * @author Thomas Debouverie
 *
 */
public class SnesGameFieldsHandler extends SingleRomFileGameFieldsHandler {

	private SnesGame getSnesGame() {
		if (this.getEditedGame() instanceof SnesGame) {
			return (SnesGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	@Override
	protected String getRomsFolderPath() {
		return Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
	}

	@Override
	protected String getRomFileDescription() {
		return Messages.get("field.snesRomFileDescription");
	}

	@Override
	protected String getExtensionFilter() {
		return "*.smc";
	}

	@Override
	protected String getRomFileName() {
		return this.getSnesGame() != null ? this.getSnesGame().romFileName() : null;
	}

	@Override
	protected void setNewRomFileName(String romFileName) {
		this.getSnesGame().setRomFileName(romFileName);
	}

	@Override
	protected void clearRomFileName() {
		this.getSnesGame().setRomFileName("");
	}

}
