package data.transfer;

import com.enterprisedt.net.ftp.FTPFile;

/**
 * Extends the base CompletionResult to add a list of files found on a remote FTP server.
 * @author Thomas Debouverie
 *
 */
public class FileListingResult extends CompletionResult {

	public FTPFile[] foundFiles;
	
	public FileListingResult() {
	}

}
