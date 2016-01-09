package data.transfer;

import java.util.concurrent.Callable;

/**
 * Represents code that must be executed during a file transfer to notify of the amount of bytes that were transferred.
 * @author Thomas Debouverie
 *
 */
public class TransferProgressCallable implements Callable<Void> {

	long bytesTransferred;
	
	@Override
	public Void call() throws Exception {
		return null;
	}
	
	public long getBytesTransferred() {
		return this.bytesTransferred;
	}

}
