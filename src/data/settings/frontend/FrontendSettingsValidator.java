package data.settings.frontend;

import java.io.File;

import data.transfer.CompletionCallable.ErrorType;
import data.transfer.CompletionResult;

/**
 * Handles validation of front-end mandatory settings.
 * Will check that all required paths and executable are defined.
 * @author Thomas Debouverie
 *
 */
public class FrontendSettingsValidator {

	private FrontendSettings frontendSettings;
	
	public FrontendSettingsValidator(FrontendSettings frontendSettings) {
		this.frontendSettings = frontendSettings;
	}
	
	public CompletionResult validate() {
		CompletionResult result = new CompletionResult();
		result.success = false;
		if (this.frontendSettings.artworksFolderPath == null || this.frontendSettings.artworksFolderPath.isEmpty() || !new File(this.frontendSettings.artworksFolderPath).exists()) {
			result.errorType = ErrorType.ARTWORKS_FOLDER_PATH_NOT_FOUND;
		} else if (this.frontendSettings.mameRomsFolderPath == null || this.frontendSettings.mameRomsFolderPath.isEmpty() || !new File(this.frontendSettings.mameRomsFolderPath).exists()) {
			result.errorType = ErrorType.MAME_ROMS_FOLDER_PATH_NOT_FOUND;
		} else if (this.frontendSettings.snesRomsFolderPath == null || this.frontendSettings.snesRomsFolderPath.isEmpty() || !new File(this.frontendSettings.snesRomsFolderPath).exists()) {
			result.errorType = ErrorType.SNES_ROMS_FOLDER_PATH_NOT_FOUND;
		} else if (this.frontendSettings.fusionRomsFolderPath == null || this.frontendSettings.fusionRomsFolderPath.isEmpty() || !new File(this.frontendSettings.fusionRomsFolderPath).exists()) {
			result.errorType = ErrorType.FUSION_ROMS_FOLDER_PATH_NOT_FOUND;
		} else if (this.frontendSettings.nesRomsFolderPath == null || this.frontendSettings.nesRomsFolderPath.isEmpty() || !new File(this.frontendSettings.nesRomsFolderPath).exists()) {
			result.errorType = ErrorType.NES_ROMS_FOLDER_PATH_NOT_FOUND;
		} else {
			result.success = true;
		}
		return result;
	}

}
