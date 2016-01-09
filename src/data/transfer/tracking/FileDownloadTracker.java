package data.transfer.tracking;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.enterprisedt.net.ftp.FTPFile;

import data.access.ArcadoidData;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;
import data.transfer.FileListingResult;

/**
 * Like FileUploadTracker, but simpler has all files found on the FTP server and not already present locally will be downloaded.
 * No targetted download is made for simplicity's sake, as it would require first downloading the catalog,
 * then scanning local and FTP folders to see what needs to be downloaded.
 * @author Thomas
 *
 */
public class FileDownloadTracker extends FileOperationTracker {

	private long dataFileLength;
	
	public FileDownloadTracker(DataTransfer transfer) {
		super(transfer);
	}

	public CompletionResult prepare() {
		FileListingResult existingDataFiles = this.transfer.getFilesList(this.ftpSettings.catalogDataPath);
		if (!existingDataFiles.success) {
			return existingDataFiles;
		}
		FileListingResult existingArtworkFiles = this.transfer.getFilesList(this.ftpSettings.artworksDataPath);
		if (!existingArtworkFiles.success) {
			return existingArtworkFiles;
		}
		FileListingResult existingMameRomsDirectories = this.transfer.getFilesList(this.ftpSettings.mameDataPath);
		if (!existingMameRomsDirectories.success) {
			return existingMameRomsDirectories;
		}
		this.analyzeRemoteDataFile(existingDataFiles.foundFiles);
		this.compareLocalAndRemoteArtworks(existingArtworkFiles.foundFiles);
		CompletionResult mameCompareResult = this.compareLocalAndRemoteMameRoms(existingMameRomsDirectories.foundFiles);
		if (mameCompareResult != null) {
			return mameCompareResult;
		}
		return null;
	}
	
	public CompletionResult getDataFile() {
		this.transferWillStart(this.dataFileLength);
		CompletionResult result = this.transfer.getFile(ArcadoidData.DATA_FILE_PATH);
		this.transferDidEnd();
		return result;
	}
	
	public CompletionResult getNextArtworkFile() {
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		String next = this.nextArtworkFileToTransfer();
		String fullPath = Settings.fullPathWithRootAndLeaf(artworksDirectoryPath, next);
		long fileSize = this.artworksToTransfer.get(next).longValue();
		this.artworksToTransfer.remove(next);
		this.transferWillStart(fileSize);
		CompletionResult result = this.transfer.getFile(next, fullPath);
		this.transferDidEnd();
		return result;
	}
	
	public CompletionResult getNextMameRomFile() {
		String romName = this.nextMameRomToTransfer();
		CompletionResult result = this.transfer.goToDirectory(this.ftpSettings.mameDataPath, romName);
		if (result != null && !result.success) {
			return result;
		}
		String fileName = this.nextMameRomFileToTransfer();
		String mameRoot = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		String mameRomPath = Settings.fullPathWithRootAndLeaf(mameRoot, romName);
		String filePath = Settings.fullPathWithRootAndLeaf(mameRomPath, fileName);
		long fileSize = this.mameRomsToTransfer.get(romName).get(fileName).longValue();
		this.mameRomsToTransfer.get(romName).remove(fileName);
		if (this.mameRomsToTransfer.get(romName).isEmpty()) {
			this.mameRomsToTransfer.remove(romName);
		}
		this.transferWillStart(fileSize);
		result = this.transfer.getFile(fileName, filePath);
		this.transferDidEnd();
		return result;
	}
	
	private void analyzeRemoteDataFile(FTPFile[] remoteDataFiles) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteDataFiles);
		if (remoteFilesList.get(ArcadoidData.DATA_FILE_PATH) != null) {
			this.totalNumberOfBytesToTransfer = remoteFilesList.get(ArcadoidData.DATA_FILE_PATH).longValue();
			this.dataFileLength = this.totalNumberOfBytesToTransfer;
		}
	}
	
	private void compareLocalAndRemoteArtworks(FTPFile[] remoteArtworks) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteArtworks);
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		for (Entry<String, Number> remoteFile : remoteFilesList.entrySet()) {
			this.checkAndAddFileToListIfNeeded(remoteFile, artworksDirectoryPath, this.artworksToTransfer);
		}
	}
	
	private CompletionResult compareLocalAndRemoteMameRoms(FTPFile[] remoteMameFolders) {
		String mameRoot = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		Map<String, Number> remoteFoldersList = DataTransfer.ftpFileListToFilesNameAndSize(remoteMameFolders);
		for (String remoteFolder : remoteFoldersList.keySet()) {
			String localPath = Settings.fullPathWithRootAndLeaf(mameRoot, remoteFolder);
			new File(localPath).mkdir();
			CompletionResult result = this.compareLocalAndRemoteMameRoms(localPath, remoteFolder);
			if (result != null && !result.success) {
				return result;
			}
		}
		return null;
	}
	
	private CompletionResult compareLocalAndRemoteMameRoms(String localPath, String remoteFolder) {
		FileListingResult remoteFolderContent = this.transfer.getFilesList(this.ftpSettings.mameDataPath, remoteFolder);
		if (!remoteFolderContent.success) {
			return remoteFolderContent;
		}
		Map<String, Number> remoteRomFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteFolderContent.foundFiles);
		Map<String, Number> romFilesToTransfer = new HashMap<String, Number>();
		for (Entry<String, Number> remoteRomFile : remoteRomFilesList.entrySet()) {
			this.checkAndAddFileToListIfNeeded(remoteRomFile, localPath, romFilesToTransfer);
		}
		if (!romFilesToTransfer.isEmpty()) {
			this.mameRomsToTransfer.put(remoteFolder, romFilesToTransfer);
		}
		return null;
	}
	
	private void checkAndAddFileToListIfNeeded(Entry<String, Number> remoteFile, String localDirectoryPath, Map<String, Number> transferMap) {
		long localFileSize = DataTransfer.getLocalFileSize(remoteFile.getKey(), localDirectoryPath);
		if (localFileSize != remoteFile.getValue().longValue()) {
			transferMap.put(remoteFile.getKey(), remoteFile.getValue());
			this.totalNumberOfBytesToTransfer += remoteFile.getValue().longValue();
		}
	}
	
}
