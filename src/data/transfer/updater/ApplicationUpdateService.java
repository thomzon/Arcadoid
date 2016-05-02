package data.transfer.updater;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.enterprisedt.net.ftp.FTPFile;

import data.settings.FTPSettings;
import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionCallable.ErrorType;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;
import data.transfer.FileListingResult;
import data.transfer.TransferProgressCallable;
import data.transfer.TransferProgressListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that handles downloading an application applicationExecutable from the remote FTP repository.
 * The work is actually done by the ApplicationUpdateTask inner class. The idea is:
 * - At all steps, if anything FTP related goes wrong, stop everything and forwards the faulty CompletionResult object.
 * - Verify that FTP settings are OK.
 * - Download remote file with detailed progress updates.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateService extends Service<Void> {

	private CompletionCallable completion;
	private ApplicationExecutable applicationExecutable;
	
	public ApplicationUpdateService(CompletionCallable completion, String applicationName) {
		this.completion = completion;
	}
	
	public ApplicationUpdateService(CompletionCallable completion, ApplicationExecutable executable) {
		this.completion = completion;
		this.applicationExecutable = executable;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new ApplicationUpdateTask(this.applicationExecutable);
	}
	
	private class ApplicationUpdateTask extends Task<Void> {

		private static final String TEMPORARY_VERSION_FILE_NAME = "tmp_version";
		private DataTransfer transfer;
		private FTPSettings ftpSettings = new FTPSettings();
		private ApplicationExecutable applicationExecutable;
		private long expectedFileSize;
		private Map<String, List<String>> otherFiles;
		private String currentOtherFilesDirectory;
		private List<String> currentOtherFiles;
		
		private ApplicationUpdateTask(ApplicationExecutable applicationExecutable) {
			this.applicationExecutable = applicationExecutable;
		}
		
		@Override
		protected Void call() throws Exception {
			this.transfer = new DataTransfer();
			this.connect();
			return null;
		}
		
		private void connect() {
	        updateMessage(Messages.get("progress.body.verifyingFtpSettings"));
	        CompletionResult result = this.transfer.connect();
	        if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.prepareFileDownload();
	        }
		}
		
		private void prepareFileDownload() {
			FileListingResult result = this.transfer.getFilesList(ftpSettings.applicationPath);
			if (!result.success) {
				completion.call(result);
			} else {
				this.findRemoteFileSize(result);
			}
		}
		
		private void findRemoteFileSize(FileListingResult fileListingResult) {
			for (FTPFile file : fileListingResult.foundFiles) {
				if (file.getName().equals(this.applicationExecutable.getExecutableName())) {
					this.expectedFileSize = file.size();
				}
			}
			if (this.expectedFileSize == 0) {
				CompletionResult error = new CompletionResult();
				error.errorType = ErrorType.CANNOT_READ_REMOTE_FILE;
				error.success = false;
				completion.call(error);
			} else {
				this.goToApplicationDirectory();
			}
		}
		
		private void goToApplicationDirectory() {
			CompletionResult result = this.transfer.goToDirectory(ftpSettings.applicationPath);
			if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	this.getTemporaryDataFile();
	        }
		}
		
		private void getTemporaryDataFile() {
			CompletionResult result = this.transfer.getFile(ApplicationUpdater.REMOTE_VERSION_FILE, TEMPORARY_VERSION_FILE_NAME);
			if (result != null && !result.success) {
	        	completion.call(result);
	        } else {
	        	try {
	    			ApplicationUpdateData updateData = ApplicationUpdateData.dataFromFile(TEMPORARY_VERSION_FILE_NAME);
	    			this.checkOtherFilesToDownloadFromUpdateData(updateData);
	    			new File(TEMPORARY_VERSION_FILE_NAME).delete();
	    			this.startApplicationDownload();
	    		} catch (IOException e) {
	    			result = new CompletionResult();
	    			result.success = false;
	    			result.errorType = ErrorType.CANNOT_PARSE_APPLICATION_UPDATE_DATA;
	    			completion.call(result);
	    		}
	        }
		}
		
		private void checkOtherFilesToDownloadFromUpdateData(ApplicationUpdateData updateData) {
			switch (this.applicationExecutable) {
			case EDITOR:
				this.otherFiles = updateData.editorFiles;
				break;
			case FRONTEND:
				this.otherFiles = updateData.frontEndFiles;
				break;
			case UPDATER:
				this.otherFiles = updateData.updaterFiles;
				break;
			}
		}
		
		private void startApplicationDownload() {
			updateMessage(Messages.get("progress.body.downloadingFile", this.applicationExecutable.getExecutableName()));
			new TransferProgressListener(this.transfer, new TransferProgressCallable() {
				@Override
				public Void call() throws Exception {
					updateProgressForBytesTransferred(this.getBytesTransferred());
					return null;
				}
			});
			this.transfer.getFile(this.applicationExecutable.getExecutableName());
			this.startOtherFilesDownload();
		}
		
		private void updateProgressForBytesTransferred(long bytesTransferred) {
			updateProgress(bytesTransferred, this.expectedFileSize);
		}
		
		private void startOtherFilesDownload() {
			if (this.otherFiles.size() > 0) {
				this.startNextOtherFilesDirectory();
			} else {
				this.finish();
			}
		}
		
		private void startNextOtherFilesDirectory() {
			if (this.otherFiles.isEmpty()) {
				this.finish();
				return;
			}
			String directory = (String)this.otherFiles.keySet().toArray()[0];
			this.currentOtherFilesDirectory = directory;
			this.currentOtherFiles = this.otherFiles.remove(directory);
			new File(directory).mkdirs();
			CompletionResult result = this.transfer.goToDirectory(ftpSettings.applicationPath, directory);
			if (result != null && !result.success) {
	        	completion.call(result);
			} else {
				this.downloadNextOtherFile();
			}
		}
		
		private void downloadNextOtherFile() {
			if (this.currentOtherFiles.isEmpty()) {
				this.startNextOtherFilesDirectory();
				return;
			}
			String fileName = this.currentOtherFiles.remove(0);
			String localPath = new File(this.currentOtherFilesDirectory, fileName).getPath();
			updateMessage(Messages.get("progress.body.downloadingFile", fileName));
			new TransferProgressListener(this.transfer, new TransferProgressCallable() {
				@Override
				public Void call() throws Exception {
					return null;
				}
			});
			CompletionResult result = this.transfer.getFile(fileName, localPath);
			if (result != null && !result.success) {
	        	completion.call(result);
			} else {
				this.downloadNextOtherFile();
			}
		}
		
		private void finish() {
	        updateMessage(Messages.get("progress.body.allDone"));
	        updateProgress(100, 100);
	        try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			completion.call(null);
		}
		
	}

}
