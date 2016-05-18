package controllers.frontend;

import java.util.ArrayList;
import java.util.List;

import applications.ApplicationVersionService;
import data.settings.Settings.PropertyId;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataUpdateChecker;
import data.transfer.updater.ApplicationExecutable;
import data.transfer.updater.ApplicationUpdateChecker;
import data.transfer.updater.ApplicationUpdater;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.frontend.UIUtils;
import utils.transfer.LoadFromRepositoryHandler;
import utils.transfer.TransferUtils;
import views.frontend.FrontendPane;
import views.frontend.FrontendPopup;

/**
 * Singleton class that coordinates all global UI operations for the front-end UI.
 * @author Thomas Debouverie
 *
 */
public class UIService {

	private static UIService sharedInstance = null;
	
	/**
	 * Reference to the app's primary stage
	 */
	private Stage primaryStage;
	
	/**
	 * Reference to root Pane of displayed Scene
	 */
	private Pane rootPane;
	/**
	 * Reference to displayed UbercadePane
	 */
	private FrontendPane displayedPane;
	/**
	 * Reference to game navigation pane, as it is created only once and reused.
	 */
	private GameNavigationPane gameNavigationPane = new GameNavigationPane();
	/**
	 * Layer to dim screen when popup is displayed
	 */
	private Rectangle dimLayer;
	
	private int numberOfFTPContactAttempt = 0;
	private ApplicationUpdateChecker updateChecker = new ApplicationUpdateChecker();
	private Runnable delayedUIRequest = null;
	
	private UIService() {
		this.createDimLayer();
	}
	
	public static UIService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new UIService();
		}
		return sharedInstance;
	}
	
	private void createDimLayer() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.dimLayer = new Rectangle(0, 0, screenBounds.getWidth(), screenBounds.getHeight());
		this.dimLayer.setFill(Color.BLACK);
		this.dimLayer.setOpacity(0);
	}
	
	/**
	 * Starts the application main UI.
	 */
	public void startServiceInPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		UIUtils.callMethodAfterTime(this, "doStartApplication", UIUtils.DELAY_BEFORE_APP_STARTS);
	}
	
	public void doStartApplication() {
		primaryStage.setTitle("Arcadoid");
		this.rootPane = new Pane();
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Scene scene = new Scene(this.rootPane, screenBounds.getWidth(), screenBounds.getHeight());
		scene.getStylesheets().add("css/frontend.css");
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.show();
		this.displayGameNavigation(false);
		this.checkForAppUpdate();
		if (this.delayedUIRequest != null) {
			this.delayedUIRequest.run();
			this.delayedUIRequest = null;
		}
	}
	
	public void checkForAppUpdate() {
		this.updateChecker.checkForUpdate(new CompletionCallable() {
			@Override
			public Void call() throws Exception {
				Platform.runLater(() -> {
					handleUpdateCheckerResult(this.result);
				});
				return null;
			}
		});
	}
	
	private void handleUpdateCheckerResult(CompletionResult result) {
		if (result != null && !result.success) {
			this.numberOfFTPContactAttempt += 1;
			if (this.numberOfFTPContactAttempt > UIUtils.NUMBER_OF_FTP_ATTEMPT_AT_STARTUP) {
				TransferUtils.showRepositoryOperationError(result);
			} else {
				UIUtils.callMethodAfterTime(this, "checkForAppUpdate", UIUtils.DELAY_BETWEEN_CONNEXION_ATTEMPTS);
			}
		} else if (this.updateChecker.updateAvailableForUpdater) {
			new ApplicationUpdater(ApplicationExecutable.UPDATER).startUpdate(this.rootPane.getScene().getWindow(), false, () -> {
				ApplicationVersionService.updateVersionNumberForProperty("" + updateChecker.updateData.editorVersionNumber, PropertyId.UPDATER_VERSION_NUMBER);
				checkForAppUpdate();
			});
		} else if (this.updateChecker.updateAvailableForFrontend) {
			ApplicationUpdater.launchUpdaterForExecutable(ApplicationExecutable.FRONTEND);
		} else {
			this.checkForDataUpdate();
		}
	}
	
	private void checkForDataUpdate() {
		DataUpdateChecker checker = new DataUpdateChecker();
		checker.checkForUpdate(new CompletionCallable() {
			@Override
			public Void call() throws Exception {
				startCatalogSync();
				return null;
			}
		});
	}
	
	/**
	 * Navigates to the game catalog screen.
	 * @param animated If true, navigation will be animated.
	 */
	public void displayGameNavigation(boolean animated) {
		this.replacePane(this.gameNavigationPane, animated);
	}
	
	/**
	 * Navigates to the settings screen.
	 * @param animated If true, navigation will be animated.
	 */
	public void displaySettings(boolean animated) {
		FrontendPane newPane = new SettingsPane();
		this.replacePane(newPane, animated);
	}
	
	/**
	 * Starts catalog synchronization with remote FTP.
	 */
	public void startCatalogSync() {
		LoadFromRepositoryHandler handler = new LoadFromRepositoryHandler();
		handler.startInWindow(this.rootPane.getScene().getWindow());
	}
	
	/**
	 * Display a popup on middle of the screen.
	 * @param popup FrontendPopup to display
	 */
	public void displayPopup(FrontendPopup popup) {
		if (this.rootPane == null) {
			this.delayedUIRequest = () -> {
				this.displayPopup(popup);
			};
			return;
		}
		this.rootPane.getChildren().add(this.dimLayer);
		this.rootPane.getChildren().add(popup);
		if (this.displayedPane != null) {
			this.displayedPane.setDisable(true);
		}
		popup.makeAppearAfterStandardDelay();
		FadeTransition dimTransition = new FadeTransition(Duration.millis(UIUtils.SCREEN_REPLACE_FADE_TIME), this.dimLayer);
		dimTransition.setFromValue(0);
		dimTransition.setToValue(UIUtils.DIM_LAYER_OPACITY);
		dimTransition.setCycleCount(1);
		dimTransition.play();
	}
	
	/**
	 * Discards displayed popup
	 * @param popup FrontendPopup to discard
	 */
	public void discardPopup(FrontendPopup popup) {
		popup.makeDisappear();
		FadeTransition dimTransition = new FadeTransition(Duration.millis(UIUtils.SCREEN_REPLACE_FADE_TIME), this.dimLayer);
		dimTransition.setFromValue(this.dimLayer.getOpacity());
		dimTransition.setToValue(0);
		dimTransition.setCycleCount(1);
		dimTransition.play();
	}
	
	/**
	 * Called by a popup when it has disappeared
	 * @param popup FrontendPopup that has disappeared
	 */
	public void popupHasDisappeared(FrontendPopup popup) {
		this.rootPane.getChildren().remove(popup);
		this.rootPane.getChildren().remove(this.dimLayer);
		if (this.displayedPane != null) {
			this.displayedPane.setDisable(false);
		}
	}
	
	/**
	 * Removes any UbercadePane still in hierarchy that are not displayed
	 */
	public void removeObsoletePanes() {
		List<Node> toRemove = new ArrayList<Node>();
		for (Node child : this.rootPane.getChildren()) {
			if (child != this.displayedPane) {
				toRemove.add(child);
			}
		}
		for (Node child : toRemove) {
			this.rootPane.getChildren().remove(child);
		}
	}
	
	/**
	 * Starts animating new displayed pane
	 */
	public void animateNewPaneAppearance() {
		this.displayedPane.animateAppearanceWithDuration(UIUtils.SCREEN_REPLACE_FADE_TIME);
		UIUtils.callMethodAfterTime(this, "removeObsoletePanes", UIUtils.SCREEN_REPLACE_FADE_TIME);
	}
	
	private void replacePane(FrontendPane newPane, boolean animated) {
		int animationDurations = animated ? UIUtils.SCREEN_REPLACE_FADE_TIME : 0;
		for (Node child : this.rootPane.getChildren()) {
			if (child instanceof FrontendPane) {
				((FrontendPane) child).prepareForDisappearance();
				((FrontendPane) child).animateDisappearanceWithDuration(animationDurations);
			}
		}		
		this.rootPane.getChildren().add(newPane);
		newPane.prepareForAppearance();
		newPane.setupPane();
		this.displayedPane = newPane;
		if (animated) {
			UIUtils.callMethodAfterTime(this, "animateNewPaneAppearance", animationDurations);
		} else {
			newPane.animateAppearanceWithDuration(0);
			this.removeObsoletePanes();
		}
	}
	
}
