package data.transfer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enterprisedt.net.ftp.FTPFile;

import data.access.ArcadoidData;
import data.model.BaseItem;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.settings.FTPSettings;
import data.settings.Settings;
import data.settings.Settings.PropertyId;

public class FileUploadTracker {

	private DataTransfer transfer;
	private FTPSettings ftpSettings;
	private long totalNumberOfBytesToTransfer;
	private long totalNumberOfBytesTransferred;
	private Map<String, Number> artworksToTransfer = new HashMap<String, Number>();
	private List<String> mameRomsFolderToCreate = new ArrayList<String>();
	private Map<String, Map<String, Number>> mameRomsToTransfer = new HashMap<String, Map<String, Number>>();
	
	
	FileUploadTracker(DataTransfer transfer) {
		this.transfer = transfer;
		this.ftpSettings = new FTPSettings();
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
		CompletionResult mameCompareResult = this.compareLocalAndRemoteMameRoms(existingMameRomsDirectories.foundFiles);
		if (mameCompareResult != null) {
			return mameCompareResult;
		}
		return null;
	}
	
	long percentComplete() {
		float percent = (float)this.totalNumberOfBytesTransferred / (float)totalNumberOfBytesToTransfer;
		return (long) (percent * 100);
	}
	
	CompletionResult prepareForDataFileUpload() {
		return this.transfer.goToDirectory(this.ftpSettings.catalogDataPath);
	}
	
	CompletionResult sendDataFile() {
		return this.transfer.transferFile(ArcadoidData.DATA_FILE_PATH);
	}
	
	String nextArtworkFileToTransfer() {
		if (this.artworksToTransfer.isEmpty()) {
			return null;
		} else {
			String next = (String)this.artworksToTransfer.keySet().toArray()[0];
			return next;
		}
	}
	
	CompletionResult prepareForArtworkUpload() {
		return this.transfer.goToDirectory(this.ftpSettings.artworksDataPath);
	}
	
	CompletionResult sendNextArtworkFile() {
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		String next = this.nextArtworkFileToTransfer();
		String fullPath = Settings.fullPathWithRootAndLeaf(artworksDirectoryPath, next);
		long fileSize = this.artworksToTransfer.get(next).longValue();
		this.totalNumberOfBytesTransferred += fileSize;
		this.artworksToTransfer.remove(next);
		return this.transfer.transferFile(fullPath, next);
	}
	
	String nextMameRomFileToTransfer() {
		String romName = this.nextMameRomToTransfer();
		if (romName == null) {
			return null;
		} else {
			String fileName = (String)this.mameRomsToTransfer.get(romName).keySet().toArray()[0];
			return fileName;
		}
	}
	
	String nextMameRomToTransfer() {
		if (this.mameRomsToTransfer.isEmpty()) {
			return null;
		} else {
			String romName = (String)this.mameRomsToTransfer.keySet().toArray()[0];
			return romName;
		}
	}
	
	CompletionResult prepareForMameRomsUpload() {
		return this.transfer.goToDirectory(this.ftpSettings.mameDataPath);
	}
	
	CompletionResult sendNextMameRomFile() {
		String romName = this.nextMameRomToTransfer();
		CompletionResult result = null;
		if (this.mameRomsFolderToCreate.contains(romName)) {
			result = this.transfer.createDirectory(this.ftpSettings.mameDataPath, romName);
			if (result != null && !result.success) {
				return result;
			}
			this.mameRomsFolderToCreate.remove(romName);
		}
		result = this.transfer.goToDirectory(this.ftpSettings.mameDataPath, romName);
		if (result != null && !result.success) {
			return result;
		}
		String fileName = this.nextMameRomFileToTransfer();
		String mameRoot = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		String mameRomPath = Settings.fullPathWithRootAndLeaf(mameRoot, romName);
		String filePath = Settings.fullPathWithRootAndLeaf(mameRomPath, fileName);
		long fileSize = this.mameRomsToTransfer.get(romName).get(fileName).longValue();
		this.totalNumberOfBytesTransferred += fileSize;
		this.mameRomsToTransfer.get(romName).remove(fileName);
		if (this.mameRomsToTransfer.get(romName).isEmpty()) {
			this.mameRomsToTransfer.remove(romName);
		}
		return this.transfer.transferFile(filePath, fileName);
	}
	
	private long getLocalFileSize(String fileName, String directory) {
		try {
			if (directory.isEmpty()) {
				return new File(fileName).length();
			} else {
				return new File(directory, fileName).length();
			}
		} catch (Exception e) {
			return 0;
		}
	}
	
	private void compareLocalAndRemoteArtworks(FTPFile[] remoteArtworks) {
		Map<String, Number> remoteFilesList = this.ftpFileListToFilesNameAndSize(remoteArtworks);
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		for (BaseItem item : ArcadoidData.sharedInstance().getAllItems()) {
			this.checkAndAddFileToListIfNeeded(item.getBackgroundArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
			this.checkAndAddFileToListIfNeeded(item.getThumbnailArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
		}
	}
	
	private CompletionResult compareLocalAndRemoteMameRoms(FTPFile[] remoteMameFolders) {
		String mameRoot = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		Map<String, Number> remoteFoldersList = this.ftpFileListToFilesNameAndSize(remoteMameFolders);
		for (Game game : ArcadoidData.sharedInstance().getAllGamesForPlatform(Platform.MAME)) {
			MameGame mameGame = (MameGame)game;
			CompletionResult comparisonResult = this.compareLocalAndRemoteMameRoms(mameRoot, remoteFoldersList, mameGame);
			if (comparisonResult != null) {
				return comparisonResult;
			}
		}
		return null;
	}
	
	private CompletionResult compareLocalAndRemoteMameRoms(String mameRoot, Map<String, Number> remoteFoldersList, MameGame mameGame) {
		String mameRomPath = Settings.fullPathWithRootAndLeaf(mameRoot, mameGame.gameName());
		if (Files.notExists(Paths.get(mameRomPath))) return null;
		Map<String, Number> romFilesList = this.listOfFilesInDirectory(mameRomPath);
		if (remoteFoldersList.get(mameGame.gameName()) == null) {
			this.mameRomsFolderToCreate.add(mameGame.gameName());
			Map<String, Number> transferList = new HashMap<String, Number>();
			for (String localFilePath : romFilesList.keySet()) {
				String fileName = new File(localFilePath).getName();
				this.checkAndAddFileToListIfNeeded(fileName, mameRomPath, new HashMap<String, Number>(), transferList);
			}
			this.mameRomsToTransfer.put(mameGame.gameName(), transferList);
		} else {
			FileListingResult remoteRomFilesResult = this.transfer.getFilesList(this.ftpSettings.mameDataPath, mameGame.gameName());
			if (!remoteRomFilesResult.success) return remoteRomFilesResult;
			Map<String, Number> remoteRomFilesList = this.ftpFileListToFilesNameAndSize(remoteRomFilesResult.foundFiles);
			Map<String, Number> transferList = new HashMap<String, Number>();
			for (String localFilePath : romFilesList.keySet()) {
				String fileName = new File(localFilePath).getName();
				this.checkAndAddFileToListIfNeeded(fileName, mameRomPath, remoteRomFilesList, transferList);
			}
			if (!transferList.isEmpty()) {
				this.mameRomsToTransfer.put(mameGame.gameName(), transferList);
			}
		}
		return null;
	}
	
	private Map<String, Number> ftpFileListToFilesNameAndSize(FTPFile[] ftpList) {
		HashMap<String, Number> map = new HashMap<String, Number>();
		for (FTPFile ftpFile : ftpList) {
			map.put(ftpFile.getName(), ftpFile.size());
		}
		return map;
	}
	
	private void checkAndAddFileToListIfNeeded(String fileName, String fileDirectory, Map<String, Number> remoteFilesList, Map<String, Number> pendingTransferList) {
		if (fileName.isEmpty()) return;
		long localFileSize = this.getLocalFileSize(fileName, fileDirectory);
		if (localFileSize == 0) return;
		Number remoteFileSize = remoteFilesList.get(fileName);
		if (remoteFileSize == null || remoteFileSize.longValue() != localFileSize) {
			this.totalNumberOfBytesToTransfer += localFileSize;
			pendingTransferList.put(fileName, localFileSize);
		}
	}
	
	private Map<String, Number> listOfFilesInDirectory(String directory) {
		try {
			File[] files = new File(directory).listFiles();
			Map<String, Number> list = new HashMap<String, Number>();
			for (File file : files) {
				list.put(file.getAbsolutePath(), new File(file.getAbsolutePath()).length());
			}
			return list;
		} catch (Exception e) {
			return new HashMap<String, Number>();
		}
		
	}
	
}
