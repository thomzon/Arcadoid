package data.transfer.tracking;

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
import data.model.FusionGame;
import data.model.Game;
import data.model.Game.Platform;
import data.model.MameGame;
import data.model.NesGame;
import data.model.SnesGame;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;
import data.transfer.FileListingResult;

/**
 * Specializes FileOperationTracker for a full Arcadoid data upload to the FTP server.
 * Each local file is compared to its remote counterpart, and only transferred if it does not exist remotely, or if its sizes are different.
 * Only files that are relevant to the current local Arcadoid catalog will be considered.
 * @author Thomas Debouverie.
 *
 */
public class FileUploadTracker extends FileOperationTracker {

	private List<String> mameRomsFolderToCreate = new ArrayList<String>();	
	
	public FileUploadTracker(DataTransfer transfer) {
		super(transfer);
	}
	
	public CompletionResult prepare() {
		this.totalNumberOfBytesToTransfer = new File(ArcadoidData.DATA_FILE_PATH).length();
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
		FileListingResult existingNesRomsFiles = this.transfer.getFilesList(this.ftpSettings.nesDataPath);
		if (!existingNesRomsFiles.success) {
			return existingNesRomsFiles;
		}
		this.compareLocalAndRemoteArtworks(existingArtworkFiles.foundFiles);
		this.compareLocalAndRemoteSnesGames(existingSnesRomsFiles.foundFiles);
		this.compareLocalAndRemoteFusionGames(existingFusionRomsFiles.foundFiles);
		this.compareLocalAndRemoteNesGames(existingNesRomsFiles.foundFiles);
		CompletionResult mameCompareResult = this.compareLocalAndRemoteMameRoms(existingMameRomsDirectories.foundFiles);
		if (mameCompareResult != null) {
			return mameCompareResult;
		}
		return null;
	}
	
	public CompletionResult sendDataFile() {
		this.transferWillStart(new File(ArcadoidData.DATA_FILE_PATH).length());
		CompletionResult result = this.transfer.transferFile(ArcadoidData.DATA_FILE_PATH);
		this.transferDidEnd();
		return result;
	}
	
	public CompletionResult sendNextArtworkFile() {
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		String next = this.nextArtworkFileToTransfer();
		return this.sendNextFile(this.artworksToTransfer, artworksDirectoryPath, next);
	}
	
	public CompletionResult sendNextSnesRomFile() {
		String romDirectoryPath = Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
		String next = this.nextSnesRomFileToTransfer();
		return this.sendNextFile(this.snesRomFilesToTransfer, romDirectoryPath, next);
	}
	
	public CompletionResult sendNextFusionRomFile() {
		String romDirectoryPath = Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH);
		String next = this.nextFusionRomFileToTransfer();
		return this.sendNextFile(this.fusionRomFilesToTransfer, romDirectoryPath, next);
	}
	
	public CompletionResult sendNextNesRomFile() {
		String romDirectoryPath = Settings.getSetting(PropertyId.NES_ROMS_FOLDER_PATH);
		String next = this.nextNesRomFileToTransfer();
		return this.sendNextFile(this.nesRomFilesToTransfer, romDirectoryPath, next);
	}
	
	private CompletionResult sendNextFile(Map<String, Number> transferMap, String directoryPath, String nextFile) {
		String fullPath = Settings.fullPathWithRootAndLeaf(directoryPath, nextFile);
		long fileSize = transferMap.get(nextFile).longValue();
		transferMap.remove(nextFile);
		this.transferWillStart(fileSize);
		CompletionResult result = this.transfer.transferFile(fullPath, nextFile);
		this.transferDidEnd();
		return result;
	}
	
	public CompletionResult sendNextMameRomFile() {
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
		this.mameRomsToTransfer.get(romName).remove(fileName);
		if (this.mameRomsToTransfer.get(romName).isEmpty()) {
			this.mameRomsToTransfer.remove(romName);
		}
		this.transferWillStart(fileSize);
		result = this.transfer.transferFile(filePath, fileName);
		this.transferDidEnd();
		return result;
	}
	
	private void compareLocalAndRemoteArtworks(FTPFile[] remoteArtworks) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteArtworks);
		String artworksDirectoryPath = Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH);
		for (BaseItem item : ArcadoidData.sharedInstance().getAllItems()) {
			this.checkAndAddFileToListIfNeeded(item.getBackgroundArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
			this.checkAndAddFileToListIfNeeded(item.getThumbnailArtworkPath(), artworksDirectoryPath, remoteFilesList, this.artworksToTransfer);
		}
	}
	
	private void compareLocalAndRemoteSnesGames(FTPFile[] remoteGames) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteGames);
		String romDirectoryPath = Settings.getSetting(PropertyId.SNES_ROMS_FOLDER_PATH);
		for (Game game : ArcadoidData.sharedInstance().getAllGamesForPlatform(Platform.SNES)) {
			SnesGame snesGame = (SnesGame)game;
			this.checkAndAddFileToListIfNeeded(snesGame.romFileName(), romDirectoryPath, remoteFilesList, this.snesRomFilesToTransfer);
		}
	}
	
	private void compareLocalAndRemoteFusionGames(FTPFile[] remoteGames) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteGames);
		String romDirectoryPath = Settings.getSetting(PropertyId.FUSION_ROMS_FOLDER_PATH);
		for (Game game : ArcadoidData.sharedInstance().getAllGamesForPlatform(Platform.FUSION)) {
			FusionGame fusionGame = (FusionGame)game;
			this.checkAndAddFileToListIfNeeded(fusionGame.romFileName(), romDirectoryPath, remoteFilesList, this.fusionRomFilesToTransfer);
		}
	}
	
	private void compareLocalAndRemoteNesGames(FTPFile[] remoteGames) {
		Map<String, Number> remoteFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteGames);
		String romDirectoryPath = Settings.getSetting(PropertyId.NES_ROMS_FOLDER_PATH);
		for (Game game : ArcadoidData.sharedInstance().getAllGamesForPlatform(Platform.NES)) {
			NesGame nesGame = (NesGame)game;
			this.checkAndAddFileToListIfNeeded(nesGame.romFileName(), romDirectoryPath, remoteFilesList, this.nesRomFilesToTransfer);
		}
	}
	
	private CompletionResult compareLocalAndRemoteMameRoms(FTPFile[] remoteMameFolders) {
		String mameRoot = Settings.getSetting(PropertyId.MAME_ROMS_FOLDER_PATH);
		Map<String, Number> remoteFoldersList = DataTransfer.ftpFileListToFilesNameAndSize(remoteMameFolders);
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
			Map<String, Number> remoteRomFilesList = DataTransfer.ftpFileListToFilesNameAndSize(remoteRomFilesResult.foundFiles);
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
	
	private void checkAndAddFileToListIfNeeded(String fileName, String fileDirectory, Map<String, Number> remoteFilesList, Map<String, Number> pendingTransferList) {
		if (fileName == null || fileName.isEmpty()) return;
		long localFileSize = DataTransfer.getLocalFileSize(fileName, fileDirectory);
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
