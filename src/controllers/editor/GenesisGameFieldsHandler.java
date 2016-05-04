package controllers.editor;

import data.model.GenesisGame;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;

/**
 * Concrete SingleRomFileGameFieldsHandler implementation to handle Genesis games specific fields.
 * @author Thomas Debouverie
 *
 */
public class GenesisGameFieldsHandler extends SingleRomFileGameFieldsHandler {

	private GenesisGame getGenesisGame() {
		if (this.getEditedGame() instanceof GenesisGame) {
			return (GenesisGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	@Override
	protected String getRomsFolderPath() {
		return Settings.getSetting(PropertyId.GENESIS_ROMS_FOLDER_PATH);
	}

	@Override
	protected String getRomFileDescription() {
		return Messages.get("field.genesisRomFileDescription");
	}

	@Override
	protected String getExtensionFilter() {
		return "*.bin";
	}

	@Override
	protected String getRomFileName() {
		return this.getGenesisGame() != null ? this.getGenesisGame().romFileName() : null;
	}

	@Override
	protected void setNewRomFileName(String romFileName) {
		this.getGenesisGame().setRomFileName(romFileName);
	}

	@Override
	protected void clearRomFileName() {
		this.getGenesisGame().setRomFileName("");
	}

}
