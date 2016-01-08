package data.transfer;

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
		private FileUploadTracker tracker;
		
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
	        this.tracker = new FileUploadTracker(this.transfer);
	        CompletionResult result = tracker.prepare();
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.sendDataFile();
	        }
		}
		
		private void sendDataFile() {
			updateMessage(Messages.get("progress.body.sendingCatalogFile"));
	        updateProgress(0, 100);
	        CompletionResult result = this.tracker.prepareForDataFileUpload();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				result = this.tracker.sendDataFile();
				if (result != null && !result.success) {
					completion.call(result);
				} else {
					updateProgress(this.tracker.percentComplete(), 100);
					this.sendArtworkFiles();
				}
			}
		}
		
		private void sendArtworkFiles() {
			CompletionResult result = this.tracker.prepareForArtworkUpload();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextArtworkFileToTransfer();
				while (nextFileName != null) {
					updateMessage(Messages.get("progress.body.sendingArtwork", nextFileName));
					result = this.tracker.sendNextArtworkFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						updateProgress(this.tracker.percentComplete(), 100);
						nextFileName = this.tracker.nextArtworkFileToTransfer();
					}
				}
				this.sendMameRomsFiles();
			}
		}
		
		private void sendMameRomsFiles() {
			CompletionResult result = this.tracker.prepareForMameRomsUpload();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextMameRomFileToTransfer();
				while (nextFileName != null) {
					updateMessage(Messages.get("progress.body.sendingMameRomFile", nextFileName));
					result = this.tracker.sendNextMameRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						updateProgress(this.tracker.percentComplete(), 100);
						nextFileName = this.tracker.nextMameRomFileToTransfer();
					}
				}
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
