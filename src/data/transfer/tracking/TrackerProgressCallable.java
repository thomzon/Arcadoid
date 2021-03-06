package data.transfer.tracking;

import java.util.concurrent.Callable;

/**
 * Represents code to be executed during a full Arcadoid data download or upload, with global and current file progress indicators.
 * @author Thomas Debouverie
 *
 */
public class TrackerProgressCallable implements Callable<Void> {

	protected long percentageDone;
	protected long currentTransferPercentageDone;
	private String currentMessage;
	
	public TrackerProgressCallable() {
	}

	public long getPercentageDone() {
		return this.percentageDone;
	}
	
	public long getCurrentTransferPercentageDone() {
		return currentTransferPercentageDone;
	}
	
	@Override
	public Void call() throws Exception {
		return null;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

}
