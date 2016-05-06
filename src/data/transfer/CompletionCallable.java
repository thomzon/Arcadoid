package data.transfer;

import java.util.concurrent.Callable;

import javafx.application.Platform;
import utils.global.GlobalUtils;

/**
 * Represents code that is executed after an operation (most often asynchronous) has been completed.
 * When the operation is finished, the code will be executed on the main thread.
 * The result of the call is included in the result property.
 * @author Thomas Debouverie
 *
 */
public class CompletionCallable implements Callable<Void> {

	public enum ErrorType {
		NONE,
		UNKNOWN_HOST,
		WRONG_LOGIN,
		UNKNOWN_DIRECTORY,
		INCOMPLETE_PATHS_CHECK,
		CANNOT_WRITE_REMOTE_FILE,
		CANNOT_READ_REMOTE_FILE,
		ARTWORKS_FOLDER_PATH_NOT_FOUND,
		MAME_ROMS_FOLDER_PATH_NOT_FOUND,
		SNES_ROMS_FOLDER_PATH_NOT_FOUND,
		GENESIS_ROMS_FOLDER_PATH_NOT_FOUND,
		MAME_EXECUTABLE_NOT_FOUND,
		STEAM_EXECUTABLE_NOT_FOUND,
		SNES9X_EXECUTABLE_NOT_FOUND,
		KEGA_EXECUTABLE_NOT_FOUND,
		CANNOT_PARSE_APPLICATION_UPDATE_DATA,
		OTHER_ERROR;
	}
	
	protected CompletionResult result;
	
	public CompletionCallable() {
	}
	
	public void call(CompletionResult result) {
		this.result = result;
		Platform.runLater(new Runnable() {
			public void run() {
				try {
					call();
				} catch (Exception e) {
					GlobalUtils.simpleErrorAlertForKeys("error.header.unknown", "error.body.unknownError", true);
				}
			}
		});
	}

	@Override
	public Void call() throws Exception {
		return null;
	}

}
