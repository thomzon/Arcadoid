package utils.transfer;

import java.io.FileNotFoundException;
import java.io.IOException;

import data.access.ArcadoidData;
import data.settings.Messages;
import data.transfer.CompletionResult;
import utils.global.GlobalUtils;

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
				GlobalUtils.simpleErrorAlertForKeys("error.header.resetFromFile", "error.body.cannotAccessFile");
			}
		} catch (IOException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.resetFromFile", "error.body.errorDuringFileIO");
		} catch (Exception e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.resetFromFile", "error.body.unexpectedFileError");
		}
	}
	
	public static void showRepositoryOperationError(CompletionResult result) {
		String messageKey = null;
		switch (result.errorType) {
		case CANNOT_READ_REMOTE_FILE:
			messageKey = "error.body.ftpReadError";
			break;
		case CANNOT_WRITE_REMOTE_FILE:
			messageKey = "error.body.ftpWriteError";
			break;
		default:
			messageKey = "error.body.invalidFtpSettings";
			break;
		}
		GlobalUtils.simpleErrorAlertForKeys("error.header.ftpOperationError", messageKey);
	}
	
	public static void handleErrorForFtpResult(CompletionResult result, String checkedAddress) {
		String messageKey = null;
		String headerKey = Messages.get("error.header.ftpCheckError");
		String[] variables = new String[0];
		switch (result.errorType) {
		case OTHER_ERROR:
			messageKey = "error.body.unexpectedFtpError";
			break;
		case UNKNOWN_HOST:
			messageKey = "error.body.unknownFtpHost";
			variables = new String[]{checkedAddress};
			break;
		case WRONG_LOGIN:
			messageKey = "error.body.invalidFtpLogin";
			break;
		case INCOMPLETE_PATHS_CHECK:
			headerKey = "error.header.ftpCheckIncomplete";
			messageKey = "error.body.dataPathsNotAllValidated";
			break;
		default:
			break;
		}
		GlobalUtils.simpleErrorAlertForKeys(headerKey, messageKey, variables);
	}
	
}
