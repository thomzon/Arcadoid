package data.settings.editor;

import java.io.File;

import data.transfer.CompletionResult;
import data.transfer.CompletionCallable.ErrorType;

public class EditorSettingsValidator {

	private EditorSettings editorSettings;
	
	public EditorSettingsValidator(EditorSettings editorSettings) {
		this.editorSettings = editorSettings;
	}
	
	public CompletionResult validate() {
		CompletionResult result = new CompletionResult();
		result.success = false;
		if (this.editorSettings.artworksFolderPath == null || this.editorSettings.artworksFolderPath.isEmpty() || !new File(this.editorSettings.artworksFolderPath).exists()) {
			result.errorType = ErrorType.ARTWORKS_FOLDER_PATH_NOT_FOUND;
		} else if (this.editorSettings.mameRomsFolderPath == null || this.editorSettings.mameRomsFolderPath.isEmpty() || !new File(this.editorSettings.mameRomsFolderPath).exists()) {
			result.errorType = ErrorType.MAME_ROMS_FOLDER_PATH_NOT_FOUND;
		} else {
			result.success = true;
		}
		return result;
	}

}
