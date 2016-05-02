package data.transfer.updater;

public enum ApplicationExecutable {
	EDITOR("ArcadoidEditor.jar"),
	FRONTEND("Arcadoid.jar"),
	UPDATER("ArcadoidUpdater.jar");
	
	private final String executableName;
	
	private ApplicationExecutable(final String executableName) {
		this.executableName = executableName;
	}
	
	public String getExecutableName() {
		return executableName;
	}
	
	public static ApplicationExecutable executableForExecutableName(String executableName) {
		if (executableName.equals(EDITOR.getExecutableName())) {
			return EDITOR;
		} else if (executableName.equals(FRONTEND.getExecutableName())) {
			return FRONTEND;
		} else if (executableName.equals(UPDATER.getExecutableName())) {
			return UPDATER;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
}
