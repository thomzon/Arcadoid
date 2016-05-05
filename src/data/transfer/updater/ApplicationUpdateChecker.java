package data.transfer.updater;

import java.io.File;
import java.io.IOException;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;

/**
 * Has the responsibility to check the applications version files on the FTP repository,
 * and check if it is more recent than the local ones.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateChecker {

	private static final String TEMPORARY_VERSION_FILE_NAME = "tmp_version";
	
	private CompletionCallable completion;
	private DataTransfer dataTransfer;
	
	public boolean updateAvailableForEditor, updateAvailableForFrontend, updateAvailableForUpdater;
	public ApplicationUpdateData updateData;
	
	public boolean anyUpdateAvailable() {
		return this.updateAvailableForEditor || this.updateAvailableForFrontend || this.updateAvailableForUpdater;
	}
	
	public void checkForUpdate(CompletionCallable completion) {
		this.updateAvailableForEditor = false;
		this.updateAvailableForFrontend = false;
		this.updateAvailableForUpdater = false;
		this.completion = completion;
		this.dataTransfer = new DataTransfer();
		this.connect();
	}
	
	private void connect() {
		this.dataTransfer.connectWithCompletion(new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					goToRemoteApplicationDirectory();
				} else {
					completion(this.result);
				}
				return null;
			}
		});
	}
	
	private void goToRemoteApplicationDirectory() {
		this.dataTransfer.goToDirectoryWithCompletion(this.dataTransfer.getFtpSettings().applicationPath, new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					getTemporaryDataFile();
				} else {
					completion(this.result);
				}
				return null;
			}
		});
	}
	
	private void getTemporaryDataFile() {
		this.dataTransfer.getFileWithCompletion(ApplicationUpdater.REMOTE_VERSION_FILE, TEMPORARY_VERSION_FILE_NAME, new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					doCheckForUpdate();
				} else {
					completion(this.result);
				}
				return null;
			}
		});
	}
	
	private void doCheckForUpdate() {
		this.updateData = this.getUpdateData();
		if (updateData != null) {
			this.updateAvailableForEditor = this.checkForUpdateForProperty(PropertyId.EDITOR_VERSION_NUMBER, this.updateData.editorVersionNumber);
			this.updateAvailableForFrontend = this.checkForUpdateForProperty(PropertyId.FRONTEND_VERSION_NUMBER, this.updateData.frontEndVersionNumber);
			this.updateAvailableForUpdater = this.checkForUpdateForProperty(PropertyId.UPDATER_VERSION_NUMBER, this.updateData.updaterVersionNumber);
		}
		this.cleanup();
		this.completion(null);
	}
	
	private ApplicationUpdateData getUpdateData() {
		try {
			ApplicationUpdateData updateData = ApplicationUpdateData.dataFromFile(TEMPORARY_VERSION_FILE_NAME);
			return updateData;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean checkForUpdateForProperty(PropertyId property, int remoteVersionNumber) {
		try {
			String localValue = Settings.getSetting(property);
			int localVersionNumber = localValue != null && localValue.length() > 0 ? Integer.parseInt(localValue) : 0;
			System.out.println("Remote version number of " + property.getKey() + " is " + remoteVersionNumber + ", local one is " + localValue);
			return remoteVersionNumber > localVersionNumber;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void completion(CompletionResult result) {
		if (this.completion != null) {
			this.completion.call(result);
		}
	}
	
	private void cleanup() {
		new File(TEMPORARY_VERSION_FILE_NAME).delete();
	}

}
