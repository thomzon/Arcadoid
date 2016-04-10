package data.transfer.updater;

import java.io.File;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
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
				}
				return null;
			}
		});
	}
	
	private void doCheckForUpdate() {
		this.updateAvailableForEditor = this.checkForUpdateForProperty(PropertyId.EDITOR_VERSION_NUMBER);
		this.updateAvailableForFrontend = this.checkForUpdateForProperty(PropertyId.FRONTEND_VERSION_NUMBER);
		this.updateAvailableForUpdater = this.checkForUpdateForProperty(PropertyId.UPDATER_VERSION_NUMBER);
		this.cleanup();
		this.completion();
	}
	
	private boolean checkForUpdateForProperty(PropertyId property) {
		try {
			String remoteValue = Settings.getSettingsValueForPropertyFromFile(property, TEMPORARY_VERSION_FILE_NAME);
			int remoteVersionNumber = remoteValue != null ? Integer.parseInt(remoteValue) : 0;
			String localValue = Settings.getSetting(property);
			int localVersionNumber = localValue != null ? Integer.parseInt(localValue) : 0;
			System.out.println("Remote version number is " + remoteVersionNumber + ", local one is " + localValue);
			return localVersionNumber != 0 && remoteVersionNumber > localVersionNumber;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void completion() {
		if (this.completion != null) {
			try {
				this.completion.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void cleanup() {
		new File(TEMPORARY_VERSION_FILE_NAME).delete();
	}

}
