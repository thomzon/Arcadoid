package data.transfer;

import java.io.File;
import java.io.IOException;

import data.access.ArcadoidData;
import data.json.DataPersistence;
import utils.global.GlobalUtils;

/**
 * Has the responsability to rapidly check the data file on the FTP repository,
 * and check if it is more recent than the local one.
 * @author Thomas Debouverie
 *
 */
public class DataUpdateChecker {

	private static final String TEMPORARY_DATA_FILE_NAME = "tmp_data.json";
	
	private CompletionCallable updateAvailableCompletion;
	private DataTransfer dataTransfer;
	
	public void checkForUpdate(CompletionCallable updateAvailableCompletion) {
		this.updateAvailableCompletion = updateAvailableCompletion;
		this.dataTransfer = new DataTransfer();
		this.connect();
	}
	
	private void connect() {
		this.dataTransfer.connectWithCompletion(new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					goToRemoteCatalogDirectory();
				}
				return null;
			}
		});
	}
	
	private void goToRemoteCatalogDirectory() {
		this.dataTransfer.goToDirectoryWithCompletion(this.dataTransfer.getFtpSettings().catalogDataPath, new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					getTemporaryDataFile();
				}
				return null;
			}
		});
	}
	
	private void getTemporaryDataFile() {
		this.dataTransfer.getFileWithCompletion(ArcadoidData.DATA_FILE_PATH, TEMPORARY_DATA_FILE_NAME, new CompletionCallable() {
			@Override public Void call() throws Exception {
				if (this.result == null || this.result.success) {
					doCheckForUpdate();
				}
				return null;
			}
		});
	}
	
	private void doCheckForUpdate() {
		try {
			int remoteVersionNumber = DataPersistence.getVersionNumberFromFile(TEMPORARY_DATA_FILE_NAME);
			this.compareWithRemoteVersionNumber(remoteVersionNumber);
		} catch (IOException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.remoteCatalog", "error.body.remoteCatalogVersionReadError");
		} finally {
			this.cleanup();
		}
	}
	
	private void compareWithRemoteVersionNumber(int remoteVersionNumber) {
		if (remoteVersionNumber > ArcadoidData.sharedInstance().getArcadoidDataVersionNumber() && this.updateAvailableCompletion != null) {
			try {
				this.updateAvailableCompletion.call();
			} catch (Exception e) {
				GlobalUtils.simpleErrorAlertForKeys("error.header.unknown", "error.body.unknownError", true);
			}
		}
	}
	
	private void cleanup() {
		new File(TEMPORARY_DATA_FILE_NAME).delete();
	}

}
