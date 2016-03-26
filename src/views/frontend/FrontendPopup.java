package views.frontend;

import com.sun.javafx.geom.Point2D;

import controllers.frontend.UIService;
import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import utils.frontend.UIUtils;

public class FrontendPopup extends Group {

	/**
	 * Size properties
	 */
	private double width, height;

	/**
	 * Creates a popup of given size.
	 * @param width Width of the popup
	 * @param height Height of the popup
	 */
	public FrontendPopup(double width, double height) {
		super();
		this.width  = width;
		this.height = height;
		Rectangle inside = new Rectangle(width, height);
		UIUtils.assignStyleClassToNodes("popup-rectangle", inside);
		this.getChildren().add(inside);
		this.setOpacity(0);
		this.center();
	}
	
	/**
	 * Center popup on the center of the screen.
	 */
	public void center() {
		Point2D middle = UIUtils.getScreenCenter();
		this.setLayoutX(middle.x - this.getWidth()/2);
		this.setLayoutY(middle.y - this.getHeight()/2);
	}
	
	/**
	 * Display popup by asking UIService to do it
	 */
	public void display() {
		UIService.getInstance().displayPopup(this);
	}
	
	/**
	 * Called by popup user when popup is about to appear on screen
	 */
	public void makeAppearAfterStandardDelay() {
		UIUtils.callMethodAfterTime(this, "makeAppear", UIUtils.DELAY_BEFORE_LAYOUT);
	}
	
	/**
	 * Starts appearance animation
	 */
	public void makeAppear()
	{
		FadeTransition transition = new FadeTransition(Duration.millis(UIUtils.SCREEN_REPLACE_FADE_TIME), this);
		transition.setFromValue(0);
		transition.setToValue(1);
		transition.setCycleCount(1);
		transition.play();
	}
	
	/**
	 * Discard popup by asking UIService to do it
	 */
	public void discard() {
		setDisable(true);
		UIService.getInstance().discardPopup(this);
	}
	
	/**
	 * Called by popup user to trigger disappearance animation - will call hasDisappeared method on itself at end of animation
	 */
	public void makeDisappear()
	{
		FadeTransition transition = new FadeTransition(Duration.millis(UIUtils.SCREEN_REPLACE_FADE_TIME), this);
		transition.setFromValue(1);
		transition.setToValue(0);
		transition.setCycleCount(1);
		transition.play();
		transition.setOnFinished((event) -> {
			this.hasDisappeared();
		});
	}
	
	/**
	 * Notifies UIService that popup has disappeared
	 */
	public void hasDisappeared() {
		UIService.getInstance().popupHasDisappeared(this);
	}

	/**
	 * @return FrontendPopup width
	 */
	public double getWidth()
	{
		return this.width;
	}
	
	/**
	 * @return FrontendPopup height
	 */
	public double getHeight()
	{
		return this.height;
	}
	
}
