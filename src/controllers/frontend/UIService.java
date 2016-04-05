package controllers.frontend;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
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
	
	private UIService() {
		this.createDimLayer();
	}
	
	public static UIService getInstance() {
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
		this.rootPane = new Pane();
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Scene scene = new Scene(this.rootPane, screenBounds.getWidth(), screenBounds.getHeight());
		scene.getStylesheets().add("Frontend.css");
		primaryStage.setScene(scene);
//		primaryStage.setFullScreen(true);
		primaryStage.show();
		this.displayGameNavigation(false);
	}
	
	public void displayGameNavigation(boolean animated) {
		FrontendPane newPane = new GameNavigationPane();
		this.replacePane(newPane, animated);
	}
	
	public void displaySettings(boolean animated) {
		FrontendPane newPane = new SettingsPane();
		this.replacePane(newPane, animated);
	}
	
	/**
	 * Display a popup on middle of the screen.
	 * @param popup FrontendPopup to display
	 */
	public void displayPopup(FrontendPopup popup) {
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
	 * Replaces current displayed pane with fade out/fade in effect
	 * @param newPane
	 */
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
		newPane.animateAppearanceWithDuration(animationDurations);
		UIUtils.callMethodAfterTime(this, "removeObsoletePanes", animationDurations * 2);
	}
	
}
