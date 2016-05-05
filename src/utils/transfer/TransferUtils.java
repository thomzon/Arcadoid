package utils.transfer;

import java.io.FileNotFoundException;
import java.io.IOException;

import data.access.ArcadoidData;
import data.settings.Messages;
import data.transfer.CompletionResult;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Static utilities for file transfer errors handling.
 * @author Thomas Debouverie
 *
 */
public class TransferUtils {

	public static void resetFromFileWithUnknownFileAlert(boolean showUnknownFileAlert) {
		try {
			ArcadoidData.sharedInstance().loadData();
		} catch (FileNotFoundException e) {
			if (showUnknownFileAlert) {
				showFileLoadErrorForMessage(Messages.get("error.body.cannotAccessFile"));
			}
		} catch (IOException e) {
			showFileLoadErrorForMessage(Messages.get("error.body.errorDuringFileIO"));
		} catch (Exception e) {
			showFileLoadErrorForMessage(Messages.get("error.body.unexpectedFileError"));
		}
	}
	
	public static void showFileLoadErrorForMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.resetFromFile"));
		alert.setContentText(message);
		alert.show();
	}
	
	public static void showRepositoryOperationError(CompletionResult result) {
		String message = null;
		switch (result.errorType) {
		case CANNOT_READ_REMOTE_FILE:
			message = Messages.get("error.body.ftpReadError");
			break;
		case CANNOT_WRITE_REMOTE_FILE:
			message = Messages.get("error.body.ftpWriteError");
			break;
		default:
			message = Messages.get("error.body.invalidFtpSettings");
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.ftpOperationError"));
		alert.setContentText(message);
		alert.show();
	}
	
	public static void handleErrorForFtpResult(CompletionResult result, String checkedAddress) {
		String message = null;
		String header = Messages.get("error.header.ftpCheckError");
		switch (result.errorType) {
		case OTHER_ERROR:
			message = Messages.get("error.body.unexpectedFtpError");
			break;
		case UNKNOWN_HOST:
			message = Messages.get("error.body.unknownFtpHost", checkedAddress);
			break;
		case WRONG_LOGIN:
			message = Messages.get("error.body.invalidFtpLogin");
			break;
		case INCOMPLETE_PATHS_CHECK:
			header = Messages.get("error.header.ftpCheckIncomplete");
			message = Messages.get("error.body.dataPathsNotAllValidated");
			break;
		default:
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.show();
	}
	
}
