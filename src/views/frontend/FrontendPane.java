package views.frontend;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import utils.frontend.UIUtils;

public abstract class FrontendPane extends Pane {

	public void prepareForDisappearance() {
		
	}
	
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
	
	public void prepareForAppearance() {
		this.setOpacity(0);
		for (Node child : getChildren()) {
			child.setOpacity(0);
		}
	}
	
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
	
	abstract public void doLayout();
	
}
