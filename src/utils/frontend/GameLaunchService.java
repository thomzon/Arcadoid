package utils.frontend;

import data.input.PlayerInputObserver;

public class GameLaunchService implements PlayerInputObserver {

	private static GameLaunchService sharedInstance = null;

	public static GameLaunchService getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new GameLaunchService();
		}
		return sharedInstance;
	}
	
	@Override
	public void quitGame() {
		
	}
	
}
