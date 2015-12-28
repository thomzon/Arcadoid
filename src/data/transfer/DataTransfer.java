package data.transfer;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class DataTransfer { 
	
	private String ftpAddress;
	private String ftpPort;
	private String ftpUser;
	private String ftpPassword;
	private FileTransferClient ftpClient;
	
	public DataTransfer() {
		this.ftpAddress = Settings.getSetting(PropertyId.REPOSITORY_FTP_ADDRESS);
		this.ftpPort = Settings.getSetting(PropertyId.REPOSITORY_FTP_PORT_NUMBER);
		this.ftpUser = Settings.getSetting(PropertyId.REPOSITORY_FTP_USER);
		this.ftpPassword = Settings.getSetting(PropertyId.REPOSITORY_FTP_PASSWORD);
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

	public void connectWithCompletion(Callable<Void> completion) {
		Task<Void> task = new Task<Void>() {
			protected Void call() throws Exception {
				doConnect();
				Platform.runLater(new Runnable() {
					public void run() {
						try {
							completion.call();
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
	
	private void doConnect() throws Exception {
		try {
			this.ftpClient = new FileTransferClient();
			this.ftpClient.setRemoteHost(this.ftpAddress);
			this.ftpClient.setRemotePort(Integer.parseInt(this.ftpPort));
			this.ftpClient.setUserName(this.ftpUser);
			this.ftpClient.setPassword(this.ftpPassword);
			this.ftpClient.connect();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
