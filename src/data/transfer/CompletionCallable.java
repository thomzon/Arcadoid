package data.transfer;

import java.util.concurrent.Callable;

public class CompletionCallable implements Callable<Void> {

	public enum ErrorType {
		NONE,
		UNKNOWN_HOST,
		WRONG_LOGIN,
		OTHER_ERROR;
	}
	
	protected CompletionResult result;
	
	public CompletionCallable() {
	}
	
	public void call(CompletionResult result) throws Exception {
		this.result = result;
		this.call();
	}

	@Override
	public Void call() throws Exception {
		System.out.println("Yep");
		return null;
	}

}
