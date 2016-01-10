package data.settings;

import java.util.concurrent.Callable;

/**
 * Represents code that can be executed to request confirmation from the user.
 * Meant to be subclassed to implement the details of the actual confirmation dialog.
 * @author Thomas Debouverie
 *
 */
public class ConfirmationDialogCallable implements Callable<Boolean> {

	protected String dialogParameter;
	
	public ConfirmationDialogCallable() {
	}

	public Boolean call(String dialogParameter) {
		this.dialogParameter = dialogParameter;
		try {
			return this.call();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Boolean call() throws Exception {
		return true;
	}

}
