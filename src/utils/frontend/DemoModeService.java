package utils.frontend;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import data.model.Game;
import javafx.animation.Timeline;

/**
 * Handles launching of random games when system is idle for a certain period of time.
 * @author Thomas Debouverie
 *
 */
public class DemoModeService implements PlayerInputObserver {
	
	private static DemoModeService sharedInstance = null;
	
	private Timeline countdownTimeline = null;
	private Timeline nextDemoTimeline = null;
	private boolean gameRunning = false;
	private boolean externalGameRunning = false;
	
	private DemoModeService() {
	}
	
	public static DemoModeService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new DemoModeService();
		}
		return sharedInstance;
	}
	
	/**
	 * Registers as player input observer and start listening for game launch events started from outside the DemoModeService.
	 * Also starts the demo launch countdown.
	 */
	public void startService() {
		PlayerInputService.sharedInstance().addInputObserver(this);
		NotificationCenter.sharedInstance().addObserver(GameLaunchService.GAME_WILL_LAUNCH_NOTIFICATION, this, "externalGameLaunch");
		this.startDemoLaunchCountdown();
	}
	
	/**
	 * Called when a game has been launched, cancels demo mode count down if game was not started from service itself.
	 */
	public void externalGameLaunch() {
		if (!this.gameRunning) {
			this.externalGameRunning = true;
			this.cancelDemoLaunchCountdown();
		}
	}
	
	private void startDemoLaunchCountdown() {
		this.cancelDemoLaunchCountdown();
		this.countdownTimeline = UIUtils.callMethodAfterTime(this, "startDemoMode", UIUtils.TIME_BEFORE_DEMO_MODE_START);
	}
	
	private void cancelDemoLaunchCountdown() {
		if (this.countdownTimeline != null) {
			this.countdownTimeline.stop();
		}
		if (this.nextDemoTimeline != null) {
			this.nextDemoTimeline.stop();
			this.nextDemoTimeline = null;
		}
	}
	
	public void startDemoMode() {
		this.gameRunning = true;
		this.runRandomGameAndPrepareForNextDemo();
	}
	
	private void runRandomGameAndPrepareForNextDemo() {
		List<Game> allGames = ArcadoidData.sharedInstance().getAllGames();
		int randomIndex = ThreadLocalRandom.current().nextInt(0, allGames.size());
		Game toStart = allGames.get(randomIndex);
		GameLaunchService.sharedInstance().runGame(toStart);
		int timeBeforeNextDemo = ThreadLocalRandom.current().nextInt(UIUtils.MINIMUM_TIME_BETWEEN_DEMOS, UIUtils.MAXIMUM_TIME_BETWEEN_DEMOS + 1);
		this.nextDemoTimeline = UIUtils.callMethodAfterTime(this, "startNextDemo", timeBeforeNextDemo);
	}
	
	public void startNextDemo() {
		GameLaunchService.sharedInstance().quitCurrentGame();
		this.runRandomGameAndPrepareForNextDemo();
	}
	
	@Override
	public void anyInputEntered() {
		if (this.gameRunning) {
			GameLaunchService.sharedInstance().quitCurrentGame();
			this.quitGame();
		} else if (!this.externalGameRunning) {
			this.startDemoLaunchCountdown();
		} 
	}
	
	@Override
	public void quitGame() {
		this.gameRunning = false;
		this.externalGameRunning = false;
		this.startDemoLaunchCountdown();
	}
	
}
