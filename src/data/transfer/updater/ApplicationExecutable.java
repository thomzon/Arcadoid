package data.transfer.updater;

/**
 * Represents one of the executables of the Arcadoid suite.
 * Capable of given the actual file name of the executable.
 * @author Thomas Debouverie
 *
 */
public enum ApplicationExecutable {
	EDITOR("Editor.jar"),
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
