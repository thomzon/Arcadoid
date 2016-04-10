package data.transfer.updater;

import data.transfer.CompletionCallable;
import data.transfer.DataTransfer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that handles downloading an application executable from the remote FTP repository.
 * The work is actually done by the ApplicationUpdateTask inner class. The idea is:
 * - At all steps, if anything FTP related goes wrong, stop everything and forwards the faulty CompletionResult object.
 * - Verify that FTP settings are OK.
 * - Download remote file with detailed progress updates.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateService extends Service<Void> {

	private CompletionCallable completion;
	
	public ApplicationUpdateService(CompletionCallable completion) {
		this.completion = completion;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new ApplicationUpdateTask();
	}
	
	private class ApplicationUpdateTask extends Task<Void> {

		private DataTransfer transfer;
		
		@Override
		protected Void call() throws Exception {
			this.transfer = new DataTransfer();
			
			return null;
		}
		
	}

}
