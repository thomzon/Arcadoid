package data.transfer;

import com.enterprisedt.net.ftp.EventListener;

/**
 * Simple EDTFTPJ EventListener that only monitors file transfer progress.
 * It forwards the information using a TransferProgressCallable object.
 * @author Thomas Debouverie
 *
 */
public class TransferProgressListener implements EventListener {

	private TransferProgressCallable progressCallable;
	
	public TransferProgressListener(DataTransfer dataTransfer, TransferProgressCallable progressCallable) {
		dataTransfer.setListener(this);
		this.progressCallable = progressCallable;
	}
	
	@Override
	public void bytesTransferred(String connId, String remoteFilename, long count) {
		this.progressCallable.bytesTransferred = count;
		try {
			this.progressCallable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commandSent(String arg0, String arg1) {
	}

	@Override
	public void downloadCompleted(String arg0, String arg1) {
	}

	@Override
	public void downloadStarted(String arg0, String arg1) {
	}

	@Override
	public void replyReceived(String arg0, String arg1) {
	}

	@Override
	public void uploadCompleted(String arg0, String arg1) {
	}

	@Override
	public void uploadStarted(String arg0, String arg1) {
	}

}
