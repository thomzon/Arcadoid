package utils.frontend;

import data.access.NotificationCenter;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import data.model.Game;

public class GameLaunchService implements PlayerInputObserver {

	public static final String GAME_WILL_LAUNCH_NOTIFICATION = "GAME_WILL_LAUNCH_NOTIFICATION";
	public static final String GAME_WILL_QUIT_NOTIFICATION = "GAME_WILL_QUIT_NOTIFICATION";
	
	private static GameLaunchService sharedInstance = null;
	
	private Game runningGame;

	public static GameLaunchService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new GameLaunchService();
		}
		return sharedInstance;
	}
	
	public void startService() {
		PlayerInputService.sharedInstance().addInputObserver(this);
	}
	
	public void runGame(Game game) {
		if (game == null) return;
		this.runningGame = game;
		NotificationCenter.sharedInstance().postNotification(GAME_WILL_LAUNCH_NOTIFICATION, this.runningGame);
		this.runningGame.execute();
	}
	
	public void quitCurrentGame() {
		if (this.runningGame == null) return;
		NotificationCenter.sharedInstance().postNotification(GAME_WILL_QUIT_NOTIFICATION, this.runningGame);
		this.runningGame.terminate();
	}
	
	@Override
	public void quitGame() {
		this.quitCurrentGame();
	}
	
}
