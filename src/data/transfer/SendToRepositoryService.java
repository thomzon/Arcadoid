package data.transfer;

import data.settings.Messages;
import data.transfer.tracking.FileUploadTracker;
import data.transfer.tracking.TrackerProgressCallable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that handles sending all required Arcadoid data to the remote FTP repository.
 * The work is actually done by the SendToRepositoryTask inner class. The idea is:
 * - At all steps, if anything FTP related goes wrong, stop everything and forwards the faulty CompletionResult object.
 * - Verify that FTP settings are OK.
 * - Compare existing local and remote data to build a list of all files that must be transferred.
 * - Send all files sequentially with detaild progress updates.
 * @author Thomas Debouverie
 *
 */
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
	        this.tracker = new FileUploadTracker(this.transfer);
	        this.tracker.setProgressCallable(this.progressCallable);
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
	        CompletionResult result = this.tracker.prepareForDataFileOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				result = this.tracker.sendDataFile();
				if (result != null && !result.success) {
					completion.call(result);
				} else {
					this.sendArtworkFiles();
				}
			}
		}
		
		private void sendArtworkFiles() {
			CompletionResult result = this.tracker.prepareForArtworkOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextArtworkFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.sendingArtwork", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.sendNextArtworkFile();
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
					String newMessage = Messages.get("progress.body.sendingMameRomFile", nextRomName, nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.sendNextMameRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextMameRomFileToTransfer();
						nextRomName = this.tracker.nextMameRomToTransfer();
					}
				}
				this.sendSnesRomsFiles();
			}
		}
		
		private void sendSnesRomsFiles() {
			CompletionResult result = this.tracker.prepareForSnesRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextSnesRomFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.sendingSnesRomFile", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.sendNextSnesRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextSnesRomFileToTransfer();
					}
				}
				this.sendFusionRomsFiles();
			}
		}
		
		private void sendFusionRomsFiles() {
			CompletionResult result = this.tracker.prepareForFusionRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextFusionRomFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.sendingFusionRomFile", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.sendNextFusionRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextFusionRomFileToTransfer();
					}
				}
				this.sendNesRomsFiles();
			}
		}
		
		private void sendNesRomsFiles() {
			CompletionResult result = this.tracker.prepareForNesRomsOperation();
			if (result != null && !result.success) {
				completion.call(result);
			} else {
				String nextFileName = this.tracker.nextNesRomFileToTransfer();
				while (nextFileName != null) {
					String newMessage = Messages.get("progress.body.sendingNesRomFile", nextFileName);
					updateMessage(newMessage);
					progressCallable.setCurrentMessage(newMessage);
					result = this.tracker.sendNextNesRomFile();
					if (result != null && !result.success) {
						completion.call(result);
						return;
					} else {
						nextFileName = this.tracker.nextNesRomFileToTransfer();
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
