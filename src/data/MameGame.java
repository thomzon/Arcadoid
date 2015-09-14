package data;

public class MameGame extends Game {

	public MameGame(long identifier) {
		super(identifier);
	}

	@Override
	public void execute() {
		if (this.process != null) return;
//		String mamePath   = AppSettings.getSetting(AppSettings.PropertyId.MAME_PATH);
//		String executable = AppSettings.getSetting(AppSettings.PropertyId.MAME_PATH) + " " + _gameName;
//		try {
//			_process = Runtime.getRuntime().exec(executable, null, new File(mamePath).getParentFile());
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(4);
//		}
	}

	@Override
	public void terminate() {
		if (this.process != null) {
			this.process.destroy();
		}
		this.process = null;
	}

}
