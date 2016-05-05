package utils.frontend;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.util.Duration;
import views.frontend.FrontendPane;

/**
 * Handles autohiding the mouse curser in a given pane when the mouse stops moving for
 * specific amount of time.
 * @author Thomas Debouverie
 *
 */
public class MouseAutohideBehaviour {

	private FrontendPane pane;
	private boolean behaviourActive;
	private Node[] nodesToAutohide;
	private Timeline hideCallTimeline;
	
	public void setupInPane(FrontendPane pane, Node[] nodesToAutohide) {
		this.pane = pane;
		this.nodesToAutohide = nodesToAutohide;
		for (Node node : nodesToAutohide) {
			node.setOpacity(0);
		}
	}
	
	public void startBehaviour() {
		this.behaviourActive = true;
		this.pane.getScene().setCursor(Cursor.NONE);
		this.pane.getScene().setOnMouseMoved((event) -> {
			if (this.behaviourActive) {
				this.mouseMoved();
			}
		});
	}
	
	public void stopBehaviour() {
		this.behaviourActive = false;
		this.cancelAutohideCall();
		this.pane.getScene().setCursor(Cursor.DEFAULT);
	}
	
	private void cancelAutohideCall() {
		if (this.hideCallTimeline != null) {
			this.hideCallTimeline.stop();
			this.hideCallTimeline = null;
		}
	}
	
	private void mouseMoved() {
		if (this.hideCallTimeline == null) {
			this.pane.getScene().setCursor(Cursor.DEFAULT);
			this.showNodes();
		}
		this.cancelAutohideCall();
		this.hideCallTimeline = UIUtils.callMethodAfterTime(this, "hideMouse", UIUtils.MOUSE_AUTOHIDE_DELAY);
	}
	
	public void hideMouse() {
		this.hideCallTimeline = null;
		this.pane.getScene().setCursor(Cursor.NONE);
		this.hideNodes();
	}
	
	private void showNodes() {
		for (Node node : this.nodesToAutohide) {
			FadeTransition transition = new FadeTransition(Duration.millis(UIUtils.AUTOHIDE_ANIMATIONS_TIME), node);
			transition.setFromValue(node.getOpacity());
			transition.setToValue(1);
			transition.play();
		}
	}
	
	private void hideNodes() {
		for (Node node : this.nodesToAutohide) {
			FadeTransition transition = new FadeTransition(Duration.millis(UIUtils.AUTOHIDE_ANIMATIONS_TIME), node);
			transition.setFromValue(node.getOpacity());
			transition.setToValue(0);
			transition.play();
		}
	}
	
}
