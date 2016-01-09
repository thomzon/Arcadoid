package data.transfer;

import data.transfer.CompletionCallable.ErrorType;

/**
 * Simple wrapper about the results of an asynchronous call.
 * @author Thomas Debouverie
 *
 */
public class CompletionResult {

	public boolean success = false;
	public ErrorType errorType = ErrorType.NONE;
	
	public CompletionResult() {
	}

}
