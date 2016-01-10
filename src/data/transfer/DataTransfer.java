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
	
	private FTPSettings ftpSettings;
	private FileTransferClient ftpClient;
	
	public DataTransfer() {
		this.setFtpSettings(new FTPSettings());
	}

	public FTPSettings getFtpSettings() {
		return ftpSettings;
	}

	public void setFtpSettings(FTPSettings ftpSettings) {
		this.ftpSettings = ftpSettings;
	}
	
	public void setListener(EventListener listener) {
		this.ftpClient.setEventListener(listener);
	}

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
	
	public CompletionResult goToDirectory(String path, String directory) {
		String fullPath = path + "/" + directory;
		if (path.endsWith("/")) {
			fullPath = path + directory;
		}
		return this.goToDirectory(fullPath);
	}
	
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
	
	public FileListingResult getFilesList(String directoryName, String childName) {
		String fullPath = directoryName + "/" + childName;
		if (directoryName.endsWith("/")) {
			fullPath = directoryName + childName;
		}
		return this.getFilesList(fullPath);
	}
	
	public CompletionResult transferFile(String filePath) {
		return this.transferFile(filePath, filePath);
	}
	
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
	
	public CompletionResult getFile(String filePath) {
		return this.getFile(filePath, filePath);
	}
	
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
	
	public CompletionResult createDirectory(String path, String directory) {
		String fullPath = path + "/" + directory;
		if (path.endsWith("/")) {
			fullPath = path + directory;
		}
		return this.createDirectory(fullPath);
	}
	
	public static Map<String, Number> ftpFileListToFilesNameAndSize(FTPFile[] ftpList) {
		HashMap<String, Number> map = new HashMap<String, Number>();
		for (FTPFile ftpFile : ftpList) {
			map.put(ftpFile.getName(), ftpFile.size());
		}
		return map;
	}
	
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
