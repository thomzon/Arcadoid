package data.transfer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.enterprisedt.net.ftp.FTPFile;

import data.access.ArcadoidData;
import data.model.BaseItem;
import data.settings.FTPSettings;
import data.settings.Settings;
import data.settings.Settings.PropertyId;

public class FileTransferTracker {

	private DataTransfer transfer;
	private FTPSettings ftpSettings;
	private long totalNumberOfBytesToTransfer;
	private Map<String, Number> artworksToTransfer = new HashMap<String, Number>();
	
	
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
		this.compareLocalAndRemoteArtworks(existingArtworkFiles.foundFiles);
		this.compareLocalAndRemoteMameRoms(existingMameRomsDirectories.foundFiles);
		return null;
	}
	
	private void compareLocalAndRemoteArtworks(FTPFile[] remoteArtworks) {
		Map<String, Number> remoteFilesList = this.ftpFileListToFilesNameAndSize(remoteArtworks);
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		for (BaseItem item : ArcadoidData.sharedInstance().getAllItems()) {
			this.checkAndAddFileToListIfNeeded(item.getBackgroundArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
			this.checkAndAddFileToListIfNeeded(item.getThumbnailArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
		}
	}
	
	private void compareLocalAndRemoteMameRoms(FTPFile[] remoteMameFolders) {
		
	}
	
	private Map<String, Number> ftpFileListToFilesNameAndSize(FTPFile[] ftpList) {
		HashMap<String, Number> map = new HashMap<String, Number>();
		for (FTPFile ftpFile : ftpList) {
			map.put(ftpFile.getName(), ftpFile.size());
		}
		return map;
	}
	
	private void checkAndAddFileToListIfNeeded(String fileName, String fileDirectory, Map<String, Number> remoteFilesList, Map<String, Number> pendingTransferList) {
		long localFileSize = this.getLocalFileSize(fileName, fileDirectory);
		if (localFileSize == 0) return;
		Number remoteFileSize = remoteFilesList.get(fileName);
		if (remoteFileSize == null || remoteFileSize.longValue() != localFileSize) {
			pendingTransferList.put(fileName, localFileSize);
		}
	}
	
	private long getLocalFileSize(String fileName, String directory) {
		try {
			return new File(directory, fileName).length();
		} catch (Exception e) {
			return 0;
		}
	}
	
}
