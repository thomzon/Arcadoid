package data.transfer;

import java.io.IOException;
import java.net.UnknownHostException;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;

import data.settings.FTPSettings;
import data.transfer.CompletionCallable.ErrorType;
import javafx.concurrent.Task;

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

	public void connectWithCompletion(CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult connectResult = doConnect();
				completion.call(connectResult);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	public void goToDirectoryWithCompletion(String fullPath, CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult result = doGoToDirectory(fullPath);
				completion.call(result);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	public void createDirectoryWithCompletion(String fullPath, CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult result = doCreateDirectory(fullPath);
				completion.call(result);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	private CompletionResult doConnect() {
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
	
	private CompletionResult doGoToDirectory(String fullPath) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.changeDirectory(this.fixPath(fullPath));
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.UNKNOWN_DIRECTORY;
		}
		return result;
	}
	
	private CompletionResult doCreateDirectory(String fullPath) {
		CompletionResult result = new CompletionResult();
		try {
			this.ftpClient.createDirectory(this.fixPath(fullPath));
			result.success = true;
		} catch (FTPException | IOException e) {
			result.errorType = ErrorType.OTHER_ERROR;
		}
		return result;
	}
	
	private String fixPath(String path) {
		if (path.startsWith("/")) {
			return path;
		} else {
			return "/" + path;
		}
	}

}
