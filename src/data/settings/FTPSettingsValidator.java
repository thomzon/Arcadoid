package data.settings;

import data.transfer.CompletionCallable;
import data.transfer.CompletionCallable.ErrorType;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;

public class FTPSettingsValidator {

	private ConfirmationDialogCallable folderCreationCallback;
	private CompletionCallable completionCallback;
	private DataTransfer dataTransfer;
	private FTPSettings ftpSettings;
	private String[] pathsToCheck;
	
	public FTPSettingsValidator(ConfirmationDialogCallable folderCreationCallback, CompletionCallable completionCallback, FTPSettings ftpSettings) {
		this.folderCreationCallback = folderCreationCallback;
		this.completionCallback = completionCallback;
		this.ftpSettings = ftpSettings;
		this.pathsToCheck = new String[]{ftpSettings.catalogDataPath, ftpSettings.artworksDataPath, ftpSettings.mameDataPath};
	}
	
	public void validate() {
		this.dataTransfer = this.setupDataTransfer();
		this.startVerificationProcedure();
	}
	
	private DataTransfer setupDataTransfer() {
		DataTransfer dataTransfer = new DataTransfer();
		dataTransfer.setFtpSettings(this.ftpSettings);
		return dataTransfer;
	}
	
	private void startVerificationProcedure() {
		this.dataTransfer.connectWithCompletion(new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result.success) {
					checkPathsExistance();
				} else {
					completionCallback.call(this.result);
				}
				return null;
			}
		});
	}
	
	private void checkPathsExistance() {
		this.checkNextPathGivenCurrentIndex(-1);
	}
	
	private void checkPathAtIndex(int index) {
		dataTransfer.goToDirectoryWithCompletion(this.pathsToCheck[index], new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result.success) {
					checkNextPathGivenCurrentIndex(index);
				} else {
					askToCreatePathAtIndex(index);
				}
				return null;
			}
		});
	}
	
	private void askToCreatePathAtIndex(int index) {
		if (this.folderCreationCallback.call(this.pathsToCheck[index])) {
			this.createPathAtIndex(index);
		} else {
			CompletionResult error = new CompletionResult();
			error.errorType = ErrorType.INCOMPLETE_PATHS_CHECK;
			this.completionCallback.call(error);
		}
	}
	
	private void createPathAtIndex(int index) {
		dataTransfer.createDirectoryWithCompletion(this.pathsToCheck[index], new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result.success) {
					checkNextPathGivenCurrentIndex(index);
				} else {
					completionCallback.call(this.result);
				}
				return null;
			}
		});
	}
	
	private void checkNextPathGivenCurrentIndex(int currentIndex) {
		if (currentIndex + 1 < this.pathsToCheck.length) {
			this.checkPathAtIndex(currentIndex + 1);
		} else {
			completionCallback.call(null);
		}
	}

}
