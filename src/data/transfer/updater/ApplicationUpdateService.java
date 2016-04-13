package data.transfer.updater;

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
 * Service that handles downloading an application executable from the remote FTP repository.
 * The work is actually done by the ApplicationUpdateTask inner class. The idea is:
 * - At all steps, if anything FTP related goes wrong, stop everything and forwards the faulty CompletionResult object.
 * - Verify that FTP settings are OK.
 * - Download remote file with detailed progress updates.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateService extends Service<Void> {

	private CompletionCallable completion;
	private String applicationName;
	
	public ApplicationUpdateService(CompletionCallable completion, String applicationName) {
		this.completion = completion;
		this.applicationName = applicationName;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new ApplicationUpdateTask(this.applicationName);
	}
	
	private class ApplicationUpdateTask extends Task<Void> {

		private DataTransfer transfer;
		private FTPSettings ftpSettings;
		private String applicationName;
		private long expectedFileSize;
		
		private ApplicationUpdateTask(String applicationName) {
			this.applicationName = applicationName;
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
				if (file.getName().equals(this.applicationName)) {
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
	        	this.startApplicationDownload();
	        }
		}
		
		private void startApplicationDownload() {
			updateMessage(Messages.get("progress.body.downloadingFile", this.applicationName));
			new TransferProgressListener(this.transfer, new TransferProgressCallable() {
				@Override
				public Void call() throws Exception {
					updateProgressForBytesTransferred(this.getBytesTransferred());
					return null;
				}
			});
			this.transfer.getFile(this.applicationName);
			this.finish();
		}
		
		private void updateProgressForBytesTransferred(long bytesTransferred) {
			updateProgress(bytesTransferred, this.expectedFileSize);
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
