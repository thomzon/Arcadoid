package data.transfer;

import data.settings.Messages;
import data.transfer.tracking.FileDownloadTracker;
import data.transfer.tracking.TrackerProgressCallable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that handles loading all required Arcadoid data from the remote FTP repository.
 * The work is actually done by the LoadFromRepositoryTask inner class. The idea is:
 * - At all steps, if anything FTP related goes wrong, stop everything and forwards the faulty CompletionResult object.
 * - Verify that FTP settings are OK.
 * - Compare existing local and remote data to build a list of all files that must be transferred.
 * - Get all files sequentially with detailed progress updates.
 * @author Thomas Debouverie
 *
 */
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
				this.getMameRomsFiles();
			}
		}
		
		private void getMameRomsFiles() {
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
				this.getSnesRomsFiles();
			}
		}
		
		private void getSnesRomsFiles() {
			CompletionResult result = this.tracker.prepareForSnesRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextSnesRomFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.downloadingSnesRomFile", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.getNextSnesRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextSnesRomFileToTransfer();
					}
				}
				this.getFusionRomsFiles();
			}
		}
		
		private void getFusionRomsFiles() {
			CompletionResult result = this.tracker.prepareForFusionRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextFusionRomFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.downloadingFusionRomFile", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.getNextFusionRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextFusionRomFileToTransfer();
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
