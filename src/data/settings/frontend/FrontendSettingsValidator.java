package data.settings.frontend;

import java.io.File;

import data.transfer.CompletionCallable.ErrorType;
import data.transfer.CompletionResult;

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
		} else if (this.frontendSettings.mameExecutablePath == null || this.frontendSettings.mameExecutablePath.isEmpty() || !new File(this.frontendSettings.mameExecutablePath).exists()) {
			result.errorType = ErrorType.MAME_EXECUTABLE_NOT_FOUND;
		} else if (this.frontendSettings.steamExecutablePath == null || this.frontendSettings.steamExecutablePath.isEmpty() || !new File(this.frontendSettings.steamExecutablePath).exists()) {
			result.errorType = ErrorType.STEAM_EXECUTABLE_NOT_FOUND;
		} else {
			result.success = true;
		}
		return result;
	}

}