package data.transfer;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.enterprisedt.net.ftp.EventListener;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FileTransferClient;

import data.settings.FTPSettings;
import data.transfer.CompletionCallable.ErrorType;
import javafx.concurrent.Task;

/**
 * Wraps EDTFTPJ library with simple FTP operations method, with synchronous and asynchronous options.
 * @author Thomas Debouverie
 *
 */
public class DataTransfer { 
	
	/**
	 * All FTP settings required to operate.
	 */
	private FTPSettings ftpSettings;
	
	/**
	 * EDTFTPJ object.
	 */
	private FileTransferClient ftpClient;
	
	/**
	 * Creates a new DataTransfer object with default FTP settings.
	 */
	public DataTransfer() {
		this.setFtpSettings(new FTPSettings());
	}

	/**
	 * Get the FTP settings used by this object.
	 */
	public FTPSettings getFtpSettings() {
		return ftpSettings;
	}

	/**
	 * Redefine FTP settings used by this object.
	 */
	public void setFtpSettings(FTPSettings ftpSettings) {
		this.ftpSettings = ftpSettings;
	}
	
	/**
	 * Sets a listener on FTP events.
	 */
	public void setListener(EventListener listener) {
		this.ftpClient.setEventListener(listener);
	}

	/**
	 * Initialize basic FTP connection asynchronously.
	 * @param completion Completion to call when finished
	 */
	public void connectWithCompletion(CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult connectResult = connect();
				completion.call(connectResult);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	/**
	 * Navigate to remote directory for given path asynchronously.
	 * @param fullPath Path to navigate to
	 * @param completion Completion to call when finished
	 */
	public void goToDirectoryWithCompletion(String fullPath, CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult result = goToDirectory(fullPath);
				completion.call(result);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	/**
	 * Creates a new remote directory asynchronously.
	 * @param fullPath Full directory path
	 * @param completion Completion to call when finished
	 */
	public void createDirectoryWithCompletion(String fullPath, CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult result = createDirectory(fullPath);
				completion.call(result);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	/**
	 * Downloads a remote file asynchronously. Must have navigated to the directory containing the file first
	 * using a "goToDirectory..." method.
	 * @param remoteFileName The remote file name to download
	 * @param localFileName The local file name to which file will be written
	 * @param completion Completion to call when finished
	 */
	public void getFileWithCompletion(String remoteFileName, String localFileName, CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult result = getFile(remoteFileName, localFileName);
				completion.call(result);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	/**
	 * Initialize basic connection to the remote FTP host.
	 * @return Result of the connection
	 */
	public CompletionResult connect() {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient = new FileTransferClient();
			this.ftpClient.setRemoteHost(this.ftpSettings.address);
			if (this.ftpSettings.portNumber.length() > 0) {
				this.ftpClient.setRemotePort(Integer.parseInt(this.ftpSettings.portNumber));
			}
			this.ftpClient.setUserName(this.ftpSettings.user);
			this.ftpClient.setPassword(this.ftpSettings.password);
			this.ftpClient.connect();
			result.success = true;
		} catch (UnknownHostException e1) {
			result.errorType = ErrorType.UNKNOWN_HOST;
		} catch (FTPException e1) {
			result.errorType = ErrorType.WRONG_LOGIN;
		} catch (Exception e1) {
			result.errorType = ErrorType.OTHER_ERROR;
		}
		return result;
	}
	
	/**
	 * Navigates to remote directory.
	 * @param fullPath Full path of the directory
	 * @return Result of the navigation attempt
	 */
	public CompletionResult goToDirectory(String fullPath) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.changeDirectory(DataTransfer.fixPath(fullPath));
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.UNKNOWN_DIRECTORY;
		}
		return result;
	}
	
	/**
	 * Navigates to remote directory.
	 * @param path Base path of the directory.
	 * @param directory Name of the directory.
	 * @return Result of the navigation attempt.
	 */
	public CompletionResult goToDirectory(String path, String directory) {
		String fullPath = path + "/" + directory;
		if (path.endsWith("/")) {
			fullPath = path + directory;
		}
		return this.goToDirectory(fullPath);
	}
	
	/**
	 * Retrieves a list of files and basic metadatas for all files in a directory.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param directoryName Name of the directory
	 * @return Result of the file listing attempt
	 */
	public FileListingResult getFilesList(String directoryName) {
		FileListingResult result = new FileListingResult();
		try {
			result.foundFiles = this.ftpClient.directoryList(directoryName);
			result.success = true;
		} catch (FTPException | IOException e) {
			e.printStackTrace();
			result.errorType = ErrorType.UNKNOWN_DIRECTORY;
		} catch (ParseException e) {
			e.printStackTrace();
			result.errorType = ErrorType.OTHER_ERROR;
		}
		return result;
	}
	
	/**
	 * Retrieves a list of files and basic metadatas for all files in a directory.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param directoryName Name of the base directory path
	 * @param childName Name of the directory to list
	 * @return Result of the file listing attempt
	 */
	public FileListingResult getFilesList(String directoryName, String childName) {
		String fullPath = directoryName + "/" + childName;
		if (directoryName.endsWith("/")) {
			fullPath = directoryName + childName;
		}
		return this.getFilesList(fullPath);
	}
	
	/**
	 * Uploads local file.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param filePath Path of the remote file, and name of the local file
	 * @return Result of the upload
	 */
	public CompletionResult transferFile(String filePath) {
		return this.transferFile(filePath, filePath);
	}
	
	/**
	 * Uploads remote file.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param filePath Local path of the file to upload
	 * @param remoteName Remote file name to save
	 * @return Result of the upload
	 */
	public CompletionResult transferFile(String filePath, String remoteName) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.uploadFile(filePath, remoteName);
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.CANNOT_WRITE_REMOTE_FILE;
		}
		return result;
	}
	
	/**
	 * Downloads remote file.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param filePath Path of the remote file, and name of the local file
	 * @return Result of the download
	 */
	public CompletionResult getFile(String filePath) {
		return this.getFile(filePath, filePath);
	}
	
	/**
	 * Downloads remote file.
	 * Must first have navigated to the path containing the directory using one of the "goToDirectory.." method.
	 * @param remoteFileName Name of the remote file to download
	 * @param localFilePath Path of the local file to which downloaded file will be saved
	 * @return Result of the download
	 */
	public CompletionResult getFile(String remoteFileName, String localFilePath) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.downloadFile(localFilePath, remoteFileName);
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.CANNOT_READ_REMOTE_FILE;
		}
		return result;
	}
	
	/**
	 * Creates a remote directory.
	 * @param fullPath Path of the remote directory to create
	 * @return Result of the directory creation attempt
	 */
	public CompletionResult createDirectory(String fullPath) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.createDirectory(DataTransfer.fixPath(fullPath));
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.OTHER_ERROR;
		}
		return result;
	}
	
	/**
	 * Creates a remote directory.
	 * @param path Base path of the directory
	 * @param directory Name of the directory
	 * @return Result of the directory creation attempt
	 */
	public CompletionResult createDirectory(String path, String directory) {
		String fullPath = path + "/" + directory;
		if (path.endsWith("/")) {
			fullPath = path + directory;
		}
		return this.createDirectory(fullPath);
	}
	
	/**
	 * Transforms a list of FTPFile metadata object into a simple Map where keys are file names,
	 * and values are file sizes in bytes.
	 * @param ftpList List of FTPFile objects
	 * @return Map of file names and sizes
	 */
	public static Map<String, Number> ftpFileListToFilesNameAndSize(FTPFile[] ftpList) {
		HashMap<String, Number> map = new HashMap<String, Number>();
		for (FTPFile ftpFile : ftpList) {
			map.put(ftpFile.getName(), ftpFile.size());
		}
		return map;
	}
	
	/**
	 * Determine size in bytes of a local file.
	 * @param fileName Name of the file to check
	 * @param directory Directory that contains the file to check
	 * @return Size of the file, or 0 if there was a problem
	 */
	public static long getLocalFileSize(String fileName, String directory) {
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
	
	private static String fixPath(String path) {
		if (path.startsWith("/")) {
			return path;
		} else {
			return "/" + path;
		}
	}

}
