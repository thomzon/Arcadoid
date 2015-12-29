package data.settings;

import java.util.concurrent.Callable;

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
