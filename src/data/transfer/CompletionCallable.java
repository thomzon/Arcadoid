package data.transfer;

import java.util.concurrent.Callable;

import javafx.application.Platform;

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
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public Void call() throws Exception {
		return null;
	}

}
