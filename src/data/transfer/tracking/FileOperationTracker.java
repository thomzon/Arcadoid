package data.transfer.tracking;

import java.util.HashMap;
import java.util.Map;

import data.settings.FTPSettings;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;
import data.transfer.TransferProgressCallable;
import data.transfer.TransferProgressListener;

/**
 * Object responsible of:
 * - Detecting all files that must be transferred to or from the FTP server.
 * - Keep track of file transfer progress and forwards the information using a TrackerProgressCallable object.
 * - Execute the actual file transfers.
 * - Forwards any error that happen along the way.
 * @author Thomas Debouverie
 *
 */
abstract class FileOperationTracker {

	DataTransfer transfer;
	FTPSettings ftpSettings;
	long totalNumberOfBytesToTransfer;
	private long totalNumberOfBytesTransferred;
	Map<String, Number> artworksToTransfer = new HashMap<String, Number>();
	Map<String, Map<String, Number>> mameRomsToTransfer = new HashMap<String, Map<String, Number>>();
	TrackerProgressCallable progressCallable;
	TransferProgressListener progressListener;
	long totalNumberOfBytesTransferredBeforeStartOfCurrentTransfer;
	long currentTransferFileSize;
	long currentTransferBytesTransferred;
	
	FileOperationTracker(DataTransfer transfer) {
		this.transfer = transfer;
		this.ftpSettings = new FTPSettings();
		this.progressListener = new TransferProgressListener(transfer, new TransferProgressCallable() {
			@Override public Void call() throws Exception {
				trackCurrentTransferProgress(this.getBytesTransferred());
				return null;
			}
		});
	}
	
	private void trackCurrentTransferProgress(long bytesTransferred) {
		this.currentTransferBytesTransferred = bytesTransferred;
		this.totalNumberOfBytesTransferred = this.totalNumberOfBytesTransferredBeforeStartOfCurrentTransfer + bytesTransferred;
		this.reportProgress();
	}
	
	protected void transferWillStart(long fileSize) {
		this.totalNumberOfBytesTransferredBeforeStartOfCurrentTransfer = this.totalNumberOfBytesTransferred;
		this.currentTransferFileSize = fileSize;
	}
	
	protected void transferDidEnd() {
		this.totalNumberOfBytesTransferred = this.totalNumberOfBytesTransferredBeforeStartOfCurrentTransfer + this.currentTransferFileSize;
		this.reportProgress();
	}
	
	protected void reportProgress() {
		if (this.progressCallable != null) {
			this.progressCallable.percentageDone = this.percentComplete();
			this.progressCallable.currentTransferPercentageDone = this.currentTransferPercentComplete();
			try {
				this.progressCallable.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setProgressCallable(TrackerProgressCallable callable) {
		this.progressCallable = callable;
	}
	
	public long percentComplete() {
		float percent = (float)this.totalNumberOfBytesTransferred / (float)totalNumberOfBytesToTransfer;
		return (long) (percent * 100);
	}
	
	public long currentTransferPercentComplete() {
		float percent = (float)this.currentTransferBytesTransferred / (float)currentTransferFileSize;
		return (long) (percent * 100);
	}
	
	public CompletionResult prepareForDataFileOperation() {
		return this.transfer.goToDirectory(this.ftpSettings.catalogDataPath);
	}
	
	public String nextArtworkFileToTransfer() {
		if (this.artworksToTransfer.isEmpty()) {
			return null;
		} else {
			String next = (String)this.artworksToTransfer.keySet().toArray()[0];
			return next;
		}
	}
	
	public CompletionResult prepareForArtworkOperation() {
		return this.transfer.goToDirectory(this.ftpSettings.artworksDataPath);
	}
	
	public String nextMameRomFileToTransfer() {
		String romName = this.nextMameRomToTransfer();
		if (romName == null) {
			return null;
		} else {
			String fileName = (String)this.mameRomsToTransfer.get(romName).keySet().toArray()[0];
			return fileName;
		}
	}
	
	public String nextMameRomToTransfer() {
		if (this.mameRomsToTransfer.isEmpty()) {
			return null;
		} else {
			String romName = (String)this.mameRomsToTransfer.keySet().toArray()[0];
			return romName;
		}
	}
	
	public CompletionResult prepareForMameRomsOperation() {
		return this.transfer.goToDirectory(this.ftpSettings.mameDataPath);
	}

}