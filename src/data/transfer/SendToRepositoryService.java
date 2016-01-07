package data.transfer;

import data.access.ArcadoidData;
import data.settings.Messages;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SendToRepositoryService extends Service<Void> {

	private CompletionCallable completion;
	
	public SendToRepositoryService(CompletionCallable completion) {
		this.completion = completion;
	}

	@Override
	protected Task<Void> createTask() {
		return new SendToRepositoryTask();
	}
	
	private class SendToRepositoryTask extends Task<Void> {
		
		private DataTransfer transfer;
		private FileTransferTracker tracker;
		
		@Override protected Void call() throws InterruptedException {
        	this.transfer = new DataTransfer();
        	this.connect();
            return null;
        }
		
		private void connect() {
	        updateMessage(Messages.get("progress.body.verifyingFtpSettings"));
	        CompletionResult result = this.transfer.connect();
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.prepareFileTracker();
	        }
		}
		
		private void prepareFileTracker() {
	        updateMessage(Messages.get("progress.body.checkingFilesToTransfer"));
	        this.tracker = new FileTransferTracker(this.transfer);
	        CompletionResult result = tracker.prepare();
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.goToCatalogDirectory();
	        }
		}
		
		private void goToCatalogDirectory() {
	        CompletionResult result = this.transfer.goToDirectory(this.transfer.getFtpSettings().catalogDataPath);
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.sendDataFile();
	        }
		}
		
		private void sendDataFile() {
	        updateMessage(Messages.get("progress.body.sendingCatalogFile"));
	        updateProgress(0, 100);
	        CompletionResult result = this.transfer.transferFile(ArcadoidData.DATA_FILE_PATH);
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.goToArtworksDirectory();
	        }
		}
		
		private void goToArtworksDirectory() {
			CompletionResult result = this.transfer.goToDirectory(this.transfer.getFtpSettings().artworksDataPath);
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.finish();
	        }
		}
		
		private void finish() {
	        updateMessage(Messages.get("progress.body.allDone"));
	        updateProgress(100, 100);
	        try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			completion.call(null);
		}
		
	}
	
}
