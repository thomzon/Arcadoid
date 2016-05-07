package controllers.editor;

import java.util.ArrayList;
import java.util.List;

import data.model.FusionGame;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Concrete SingleRomFileGameFieldsHandler implementation to handle Fusion games specific fields.
 * @author Thomas Debouverie
 *
 */
public class FusionGameFieldsHandler extends SingleRomFileGameFieldsHandler {

	private FusionGame getFusionGame() {
		if (this.getEditedGame() instanceof FusionGame) {
			return (FusionGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	@Override
	protected String getRomsFolderPath() {
		return Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH);
	}
	
	@Override
	protected List<ExtensionFilter> getAllExtensionFilters() {
		List<ExtensionFilter> filters = new ArrayList<ExtensionFilter>();
		List<String> extensions = new ArrayList<String>();
		extensions.add("*.bin");
		extensions.add("*.sms");
		extensions.add("*.gg");
		filters.add(new ExtensionFilter(Messages.get("field.fusionRomFileDescription"), extensions));
		return filters;
	}

	@Override
	protected String getRomFileName() {
		return this.getFusionGame() != null ? this.getFusionGame().romFileName() : null;
	}

	@Override
	protected void setNewRomFileName(String romFileName) {
		this.getFusionGame().setRomFileName(romFileName);
	}

	@Override
	protected void clearRomFileName() {
		this.getFusionGame().setRomFileName("");
	}

}
