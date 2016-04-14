package data.transfer.updater;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataTransfer;

/**
 * Has the responsibility to check the application version file on the FTP repository,
 * and check if it is more recent than the local one.
 * @author Thomas Debouverie
 *
 */
public class ApplicationUpdateChecker {

	private static final String TEMPORARY_VERSION_FILE_NAME = "tmp_version";
	
	private CompletionCallable completion;
	private DataTransfer dataTransfer;
	
	public boolean updateAvailableForEditor, updateAvailableForFrontend, updateAvailableForUpdater;
	public int remoteEditorVersionNumber, remoteFrontendVersionNumber, remoteUpdaterVersionNumber;
	
	public boolean anyUpdateAvailable() {
		return this.updateAvailableForEditor || this.updateAvailableForFrontend || this.updateAvailableForUpdater;
	}
	
	public void checkForEditorUpdate(CompletionCallable completion) {
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
					fetchRemoteVersionNumbers();
					doCheckForUpdate();
				} else {
					completion(this.result);
				}
				return null;
			}
		});
	}
	
	private void fetchRemoteVersionNumbers() {
		this.remoteEditorVersionNumber = this.retrieveRemoteVersionNumberForProperty(PropertyId.EDITOR_VERSION_NUMBER);
		this.remoteFrontendVersionNumber = this.retrieveRemoteVersionNumberForProperty(PropertyId.FRONTEND_VERSION_NUMBER);
		this.remoteUpdaterVersionNumber = this.retrieveRemoteVersionNumberForProperty(PropertyId.UPDATER_VERSION_NUMBER);
	}
	
	private void doCheckForUpdate() {
		this.updateAvailableForEditor = this.checkForUpdateForProperty(PropertyId.EDITOR_VERSION_NUMBER, this.remoteEditorVersionNumber);
		this.updateAvailableForFrontend = this.checkForUpdateForProperty(PropertyId.FRONTEND_VERSION_NUMBER, this.remoteFrontendVersionNumber);
		this.updateAvailableForUpdater = this.checkForUpdateForProperty(PropertyId.UPDATER_VERSION_NUMBER, this.remoteUpdaterVersionNumber);
		this.cleanup();
		this.completion(null);
	}
	
	private int retrieveRemoteVersionNumberForProperty(PropertyId property) {
		try {
			String remoteValue = Settings.getSettingsValueForPropertyFromFile(property, TEMPORARY_VERSION_FILE_NAME);
			int remoteVersionNumber = remoteValue != null ? Integer.parseInt(remoteValue) : 0;
			return remoteVersionNumber;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private boolean checkForUpdateForProperty(PropertyId property, int remoteVersionNumber) {
		try {
			String localValue = Settings.getSetting(property);
			int localVersionNumber = localValue != null ? Integer.parseInt(localValue) : 0;
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
