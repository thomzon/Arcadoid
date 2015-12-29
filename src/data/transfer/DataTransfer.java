package data.transfer;

import java.net.UnknownHostException;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable.ErrorType;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class DataTransfer { 
	
	private String ftpAddress;
	private String ftpPort;
	private String ftpUser;
	private String ftpPassword;
	private FileTransferClient ftpClient;
	
	public DataTransfer() {
		this.setFtpAddress(Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS));
		this.setFtpPort(Settings.getSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER));
		this.setFtpUser(Settings.getSetting(PropertyId.REPOSITORY_FTP_USER));
		this.setFtpPassword(Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD));
	}
	
	public String getFtpAddress() {
		return ftpAddress;
	}

	public void setFtpAddress(String ftpAddress) {
		this.ftpAddress = ftpAddress;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public void connectWithCompletion(CompletionCallable completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() {
				final CompletionResult connectResult = doConnect();
				Platform.runLater(new Runnable() {
					public void run() {
						try {
							completion.call(connectResult);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				return null;
			}
		};
		new Thread(task).start();
	}
	
	private CompletionResult doConnect() {
		CompletionResult result = new CompletionResult();
		result.success = false;
		result.errorType = ErrorType.NONE;
		try {
			this.ftpClient = new FileTransferClient();
			this.ftpClient.setRemoteHost(this.ftpAddress);
			if (this.ftpPort.length() > 0) {
				this.ftpClient.setRemotePort(Integer.parseInt(this.ftpPort));
			}
			this.ftpClient.setUserName(this.ftpUser);
			this.ftpClient.setPassword(this.ftpPassword);
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

}
