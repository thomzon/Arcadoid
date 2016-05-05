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
		} else if (this.frontendSettings.genesisRomsFolderPath == null || this.frontendSettings.genesisRomsFolderPath.isEmpty() || !new File(this.frontendSettings.genesisRomsFolderPath).exists()) {
			result.errorType = ErrorType.GENESIS_ROMS_FOLDER_PATH_NOT_FOUND;
		} else if (this.frontendSettings.mameExecutablePath == null || this.frontendSettings.mameExecutablePath.isEmpty() || !new File(this.frontendSettings.mameExecutablePath).exists()) {
			result.errorType = ErrorType.MAME_EXECUTABLE_NOT_FOUND;
		} else if (this.frontendSettings.steamExecutablePath == null || this.frontendSettings.steamExecutablePath.isEmpty() || !new File(this.frontendSettings.steamExecutablePath).exists()) {
			result.errorType = ErrorType.STEAM_EXECUTABLE_NOT_FOUND;
		} else if (this.frontendSettings.snes9xExecutablePath == null || this.frontendSettings.snes9xExecutablePath.isEmpty() || !new File(this.frontendSettings.snes9xExecutablePath).exists()) {
			result.errorType = ErrorType.SNES9X_EXECUTABLE_NOT_FOUND;
		} else if (this.frontendSettings.kegaExecutablePath == null || this.frontendSettings.kegaExecutablePath.isEmpty() || !new File(this.frontendSettings.kegaExecutablePath).exists()) {
			result.errorType = ErrorType.KEGA_EXECUTABLE_NOT_FOUND;
		} else {
			result.success = true;
		}
		return result;
	}

}
