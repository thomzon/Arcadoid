package data.transfer;

import data.settings.Messages;
import data.transfer.tracking.FileDownloadTracker;
import data.transfer.tracking.TrackerProgressCallable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoadFromRepositoryService extends Service<Void> {

	private CompletionCallable completion;

	public LoadFromRepositoryService(CompletionCallable completion) {
		this.completion = completion;
	}

	@Override
	protected Task<Void> createTask() {
		return new LoadFromRepositoryTask();
	}
	
	private class LoadFromRepositoryTask extends Task<Void> {
		
		private DataTransfer transfer;
		private FileDownloadTracker tracker;
		private TrackerProgressCallable progressCallable;

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
	        this.progressCallable = new TrackerProgressCallable() {
	        	@Override public Void call() throws Exception {
	        		updateProgress(this.getPercentageDone(), 100);
	        		updateMessage(this.getCurrentMessage() + ": " + this.getCurrentTransferPercentageDone() + "%");
	        		return null;
	        	}
	        };
	        this.tracker = new FileDownloadTracker(this.transfer);
	        this.tracker.setProgressCallable(this.progressCallable);
	        CompletionResult result = tracker.prepare();
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.getDataFile();
	        }
		}
		
		private void getDataFile() {
			updateMessage(Messages.get("progress.body.downloadingCatalogFile"));
	        updateProgress(0, 100);
	        CompletionResult result = this.tracker.prepareForDataFileOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				result = this.tracker.getDataFile();
				if (result != null && !result.success) {
					completion.call(result);
				} else {
					this.getArtworkFiles();
				}
			}
		}
		
		private void getArtworkFiles() {
			CompletionResult result = this.tracker.prepareForArtworkOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextArtworkFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.downloadingArtwork", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.getNextArtworkFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextArtworkFileToTransfer();
					}
				}
				this.sendMameRomsFiles();
			}
		}
		
		private void sendMameRomsFiles() {
			CompletionResult result = this.tracker.prepareForMameRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextMameRomFileToTransfer();
				String nextRomName = this.tracker.nextMameRomToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.downloadingMameRomFile", nextRomName, nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.getNextMameRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextMameRomFileToTransfer();
						nextRomName = this.tracker.nextMameRomToTransfer();
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
