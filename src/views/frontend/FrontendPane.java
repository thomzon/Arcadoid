package views.frontend;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import utils.frontend.UIUtils;

/**
 * An extension of a simple Pane that adds functions specific to the Arcadoid front-end.
 * Intended to be subclassed.
 * @author Thomas Debouverie
 *
 */
public abstract class FrontendPane extends Pane {

	/**
	 * Called by the pane handler (UIService usually) when the pane should prepare to be removed off its parent.
	 */
	public void prepareForDisappearance() {
		
	}
	
	/**
	 * Do a simple disappearance animation by changing opacity from current to 0 over a duration.
	 * @param duration Duration of the animation in milliseconds
	 */
	public void animateDisappearanceWithDuration(int duration) {
		if (duration > 0) {
			FadeTransition transition = new FadeTransition(Duration.millis(duration), this);
			transition.setFromValue(this.getOpacity());
			transition.setToValue(0);
			transition.play();
		} else {
			this.setOpacity(0);
		}
	}
	
	/**
	 * Called by the pane handler (UIService usually) when the pane should prepare itself to be displayed.
	 */
	public void prepareForAppearance() {
		this.setOpacity(0);
		for (Node child : getChildren()) {
			child.setOpacity(0);
		}
	}
	
	/**
	 * Do a simple appearance animation by changing opacity from 0 to 1 over a duration.
	 * @param duration Duration of the animation in milliseconds
	 */
	public void animateAppearanceWithDuration(int duration) {
		if (duration > 0) {
			FadeTransition transition = new FadeTransition(Duration.millis(duration), this);
			transition.setFromValue(0);
			transition.setToValue(1);
			transition.play();
		} else {
			this.setOpacity(1);
		}
	}
	
	/**
	 * Called by the pane handler (usually UIService) when the pane should set itself up.
	 * This will simply call the "doLayout" method on itself after a set duration.
	 * It is necessary to wait before laying out children, otherwise geometry of the pane is not yet
	 * ready for position and sizes calculations.
	 */
	public void setupPane() {
		UIUtils.callMethodAfterTime(this, "doLayout", UIUtils.DELAY_BEFORE_LAYOUT);
	}
	
	/**
	 * Make all children visible or not
	 * @param visible True if children must be visible, otherwise false
	 */
	protected void makeChildrenVisible(boolean visible) {
		for (Node child : getChildren()) {
			child.setOpacity(visible ? 1 : 0);
		}
	}
	
	/**
	 * Must be implemented by concrete classes to actually set the positions and sizes of its children.
	 */
	abstract public void doLayout();
	
}
