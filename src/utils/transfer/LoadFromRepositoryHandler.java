package utils.transfer;

import org.controlsfx.dialog.ProgressDialog;

import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.LoadFromRepositoryService;
import javafx.concurrent.Service;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Handles creating and managing UI feedback for a LoadFromRepositoryService
 * @author Thomas Debouverie
 *
 */
public class LoadFromRepositoryHandler {

	public void startInWindow(Window window) {
		CompletionCallable sendCompletion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				handleLoadFromRepositoryResult(this.result);
				return null;
			}
		};
		Service<Void> service = new LoadFromRepositoryService(sendCompletion);
		ProgressDialog dialog = new ProgressDialog(service);
		dialog.initOwner(window);
		dialog.setTitle(Messages.get("alert.title"));
		dialog.setHeaderText(Messages.get("progress.header.loadFromRepo"));
		dialog.initModality(Modality.APPLICATION_MODAL);
		service.start();
	}
	
	private void handleLoadFromRepositoryResult(CompletionResult result) {
		if (result != null && !result.success) {
			TransferUtils.showRepositoryOperationError(result);
		} else {
			TransferUtils.resetFromFileWithUnknownFileAlert(true);
		}
	}	
		
}
