package data.transfer.updater;

import java.io.IOException;

import org.controlsfx.dialog.ProgressDialog;

import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import javafx.concurrent.Service;
import javafx.stage.Modality;
import javafx.stage.Window;
import utils.transfer.TransferUtils;

public class ApplicationUpdater {

	public static final String REMOTE_VERSION_FILE = "versions.json";
	public static final String EDITOR_EXECUTABLE_NAME = "AracoidEditor.jar";
	public static final String FRONTEND_EXECUTABLE_NAME = "Arcadoid.jar";
	public static final String UPDATER_EXECUTABLE_NAME = "ArcadoidUpdater.jar";
	
	public enum ApplicationExecutable {
		EDITOR("ArcadoidEditor.jar"),
		FRONTEND("Arcadoid.jar"),
		UPDATER("ArcadoidUpdater.jar");
		
		private final String executableName;
		private ApplicationExecutable(final String executableName) {
			this.executableName = executableName;
		}
	}
	
	private ApplicationExecutable executable;
	
	public ApplicationUpdater(ApplicationExecutable executable) {
		this.executable = executable;
	}
	
	public ApplicationUpdater(String executableName) throws IllegalArgumentException {
		if (executableName.equals(ApplicationExecutable.EDITOR.executableName)) {
			this.executable = ApplicationExecutable.EDITOR;
		} else if (executableName.equals(ApplicationExecutable.FRONTEND.executableName)) {
			this.executable = ApplicationExecutable.FRONTEND;
		} else if (executableName.equals(ApplicationExecutable.UPDATER.executableName)) {
			this.executable = ApplicationExecutable.UPDATER;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static void launchUpdaterForExecutable(ApplicationExecutable executable) {
		try {
			Runtime.getRuntime().exec("java -jar " + ApplicationExecutable.UPDATER.executableName + " --update " + executable.executableName, null, null);
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startUpdate(Window window, boolean executeWhenDone, Runnable completionRunnable) {
		CompletionCallable updateCompletion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				handleUpdateResult(this.result, executeWhenDone, completionRunnable);
				return null;
			}
		};
		
		Service<Void> service = new ApplicationUpdateService(updateCompletion, this.executable.executableName);
		ProgressDialog dialog = new ProgressDialog(service);
		dialog.initOwner(window);
		dialog.setTitle(Messages.get("alert.title"));
		dialog.setHeaderText(Messages.get("progress.header.updateApplication", this.executable.executableName));
		dialog.initModality(Modality.APPLICATION_MODAL);
		service.start();
	}
	
	private void handleUpdateResult(CompletionResult result, boolean executeWhenDone, Runnable completionRunnable) {
		if (result != null && !result.success) {
			TransferUtils.showRepositoryOperationError(result);
		} else if (executeWhenDone) {
			try {
				Runtime.getRuntime().exec("java -jar " + this.executable.executableName, null, null);
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (completionRunnable != null) {
			completionRunnable.run();
		}
	}
	
}
