package utils.frontend;

import data.access.NotificationCenter;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import data.model.Game;
import utils.global.GlobalUtils;

/**
 * Handles launching of games from the frontend. Notifications are sent trough
 * NotificationCenter to inform when a game is launched or quitted.
 * @author Thomas Debouverie
 *
 */
public class GameLaunchService implements PlayerInputObserver {

	public static final String GAME_WILL_LAUNCH_NOTIFICATION = "GAME_WILL_LAUNCH_NOTIFICATION";
	public static final String GAME_WILL_QUIT_NOTIFICATION = "GAME_WILL_QUIT_NOTIFICATION";
	
	private static GameLaunchService sharedInstance = null;
	
	private Game runningGame;

	private GameLaunchService() {
		
	}
	
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
		try {
			this.runningGame.execute();
		} catch (Exception e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.gameLaunch", "error.body.gameLaunchError");
		}
	}
	
	public void quitCurrentGame() {
		if (this.runningGame == null) return;
		NotificationCenter.sharedInstance().postNotification(GAME_WILL_QUIT_NOTIFICATION, this.runningGame);
		try {
			this.runningGame.terminate();
		} catch (Exception e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.gameLaunch", "error.body.gameQuitError");
		}
	}
	
	@Override
	public void quitGame() {
		this.quitCurrentGame();
	}
	
}
