package data.transfer;

import data.transfer.CompletionCallable.ErrorType;

public class CompletionResult {

	public boolean success = false;
	public ErrorType errorType = ErrorType.NONE;
	
	public CompletionResult() {
	}

}
