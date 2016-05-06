package data.transfer.updater;

import java.io.IOException;

import org.controlsfx.dialog.ProgressDialog;

import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import javafx.concurrent.Service;
import javafx.stage.Modality;
import javafx.stage.Window;
import utils.global.GlobalUtils;
import utils.transfer.TransferUtils;

/**
 * Handles basic coordination for the update of one application.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdater {

	public static final String REMOTE_VERSION_FILE = "versions.json";
	
	private ApplicationExecutable applicationExecutable;
	
	public ApplicationUpdater(ApplicationExecutable executable) {
		this.applicationExecutable = executable;
	}
	
	/**
	 * Creates a new instance for given executable name.
	 * @param executableName Name of the executable for the application to update.
	 * @throws IllegalArgumentException If the executable name does not match any of the known applications.
	 */
	public ApplicationUpdater(String executableName) throws IllegalArgumentException {
		this.applicationExecutable = ApplicationExecutable.executableForExecutableName(executableName);
	}
	
	/**
	 * Launches the updater application to immediately start update for given application.
	 */
	public static void launchUpdaterForExecutable(ApplicationExecutable executable) {
		try {
			Runtime.getRuntime().exec("java -jar " + ApplicationExecutable.UPDATER.getExecutableName() + " --update " + executable.getExecutableName(), null, null);
			System.exit(0);
		} catch (IOException e) {
			reportUpdaterLaunchError();
		}
	}
	
	private static void reportUpdaterLaunchError() {
		GlobalUtils.simpleErrorAlertForKeys("error.header.updaterError", "error.body.updaterLaunch");
	}
	
	/**
	 * Starts the update.
	 * @param window Window in which any dialog will be presented
	 * @param executeWhenDone If true, the application that is updated will be started directly once the update is finished
	 * @param completionRunnable Completion to run when finished
	 */
	public void startUpdate(Window window, boolean executeWhenDone, Runnable completionRunnable) {
		CompletionCallable updateCompletion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				handleUpdateResult(this.result, executeWhenDone, completionRunnable);
				return null;
			}
		};
		
		Service<Void> service = new ApplicationUpdateService(updateCompletion, this.applicationExecutable);
		ProgressDialog dialog = new ProgressDialog(service);
		dialog.initOwner(window);
		dialog.setTitle(Messages.get("alert.title"));
		dialog.setHeaderText(Messages.get("progress.header.updateApplication", this.applicationExecutable.getExecutableName()));
		dialog.initModality(Modality.APPLICATION_MODAL);
		service.start();
	}
	
	private void handleUpdateResult(CompletionResult result, boolean executeWhenDone, Runnable completionRunnable) {
		if (result != null && !result.success) {
			TransferUtils.showRepositoryOperationError(result);
		} else if (executeWhenDone) {
			try {
				Runtime.getRuntime().exec("java -jar " + this.applicationExecutable.getExecutableName(), null, null);
				System.exit(0);
			} catch (IOException e) {
				reportExecutableLaunchError();
			}
		} else if (completionRunnable != null) {
			completionRunnable.run();
		}
	}
	
	private static void reportExecutableLaunchError() {
		GlobalUtils.simpleErrorAlertForKeys("error.header.updaterError", "error.body.executableLaunch");
	}
	
}
