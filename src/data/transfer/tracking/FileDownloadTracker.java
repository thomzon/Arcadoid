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
 * Specializes FileOperationTracker for a full Arcadoid data upload to the FTP server.
 * Each remote file is compared to its remote counterpart, and only transferred if it does not exist remotely, or if its sizes are different.
 * Unlike the FileUploadTracker object, the remote catalog is not considered.
 * All remote files found in the different data paths will be considered for download.
 * @author Thomas Debouverie.
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
		FileListingResult existingSnesRomsFiles = this.transfer.getFilesList(this.ftpSettings.snesDataPath);
		if (!existingSnesRomsFiles.success) {
			return existingSnesRomsFiles;
		}
		FileListingResult existingFusionRomsFiles = this.transfer.getFilesList(this.ftpSettings.fusionDataPath);
		if (!existingFusionRomsFiles.success) {
			return existingFusionRomsFiles;
		}
		this.analyzeRemoteDataFile(existingDataFiles.foundFiles);
		this.compareLocalAndRemoteFiles(existingArtworkFiles.foundFiles, Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH), this.artworksToTransfer);
		this.compareLocalAndRemoteFiles(existingSnesRomsFiles.foundFiles, Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH), this.snesRomFilesToTransfer);
		this.compareLocalAndRemoteFiles(existingFusionRomsFiles.foundFiles, Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH), this.fusionRomFilesToTransfer);
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
		return this.getNextFile(this.artworksToTransfer, artworksDirectoryPath, next);
	}
	
	public CompletionResult getNextSnesRomFile() {
		String romDirectoryPath = Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
		String next = this.nextSnesRomFileToTransfer();
		return this.getNextFile(this.snesRomFilesToTransfer, romDirectoryPath, next);
	}
	
	public CompletionResult getNextFusionRomFile() {
		String romDirectoryPath = Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH);
		String next = this.nextFusionRomFileToTransfer();
		return this.getNextFile(this.fusionRomFilesToTransfer, romDirectoryPath, next);
	}
	
	private CompletionResult getNextFile(Map<String, Number> transferMap, String directoryPath, String nextFile) {
		String fullPath = Settings.fullPathWithRootAndLeaf(directoryPath, nextFile);
		long fileSize = transferMap.get(nextFile).longValue();
		transferMap.remove(nextFile);
		this.transferWillStart(fileSize);
		CompletionResult result = this.transfer.getFile(nextFile, fullPath);
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
	
	private void compareLocalAndRemoteFiles(FTPFile[] remoteFiles, String localDirectoryPath, Map<String, Number> transferMap) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteFiles);
		for (Entry<String, Number> remoteFile : remoteFilesList.entrySet()) {
			this.checkAndAddFileToListIfNeeded(remoteFile, localDirectoryPath, transferMap);
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
