package data.transfer;

import java.io.File;

import data.access.ArcadoidData;
import data.settings.FTPSettings;

public class FileTransferTracker {

	private DataTransfer transfer;
	private FTPSettings ftpSettings;
	private long totalNumberOfBytesToTransfer;
	
	FileTransferTracker(DataTransfer transfer) {
		this.transfer = transfer;
		this.ftpSettings = new FTPSettings();
	}

	long getTotalNumberOfBytesToTransfer() {
		return totalNumberOfBytesToTransfer;
	}
	
	CompletionResult prepare() {
		this.totalNumberOfBytesToTransfer = new File(ArcadoidData.DATA_FILE_PATH).length();
		FileListingResult existingArtworkFiles = this.transfer.getFilesList(this.ftpSettings.artworksDataPath);
		if (!existingArtworkFiles.success) {
			return existingArtworkFiles;
		}
		FileListingResult existingMameRomsDirectories = this.transfer.getFilesList(this.ftpSettings.mameDataPath);
		if (!existingMameRomsDirectories.success) {
			return existingMameRomsDirectories;
		}
		return null;
	}
	
}
