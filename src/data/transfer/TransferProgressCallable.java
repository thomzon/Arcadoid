package data.transfer;

import java.util.concurrent.Callable;

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
