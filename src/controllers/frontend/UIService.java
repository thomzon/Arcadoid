package controllers.frontend;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.frontend.UIUtils;
import views.frontend.FrontendPane;
import views.frontend.FrontendPopup;

/**
 * Singleton class that coordinates all global UI operations.
 * @author Thomas Debouverie
 *
 */
public class UIService {

	private static UIService sharedInstance = null;
	
	/**
	 * Reference to primary Stage
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
	 * Layer to dim screen when popup is displayed
	 */
	private Rectangle dimLayer;
	
	private UIService() {}
	
	public static UIService getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new UIService();
		}
		return sharedInstance;
	}
	
	/**
	 * Sets the primary stage.
	 * @param primaryStage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	/**
	 * Display a popup on middle of the screen
	 * @param popup FrontendPopup to display
	 */
	public void displayPopup(FrontendPopup popup) {
		this.rootPane.getChildren().add(this.dimLayer);
		this.rootPane.getChildren().add(popup);
		this.displayedPane.setDisable(true);
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
		this.displayedPane.setDisable(false);
	}
	
}
