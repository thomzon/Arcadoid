package utils.frontend;

import java.lang.reflect.Method;

import com.sun.javafx.geom.Point2D;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.stage.Screen;
import javafx.util.Duration;

/**
 * Static utilities class for front-end application.
 * @author Thomas Debouverie
 *
 */
public class UIUtils {

	/**
	 * Constants for all UI elements
	 */
	public static final double 	BORDER_NODE_MARGIN 				= 30;
	public static final double  BUTTON_LABEL_MARGIN				= 50;
	public static final int		SCREEN_REPLACE_FADE_TIME 		= 300;
	public static final int		SETTINGS_INIT_MSG_TIME			= 1000;
	public static final double  DIM_LAYER_OPACITY				= 0.4;
	public static final double	POPUP_TEXT_MARGIN				= 10;
	public static final double	DELAY_BEFORE_LAYOUT				= 200;
	
	/**
	 * Assign given CSS class name to one or more nodes
	 * @param className Name of CSS class to assign
	 * @param nodes Nodes to which CSS class must be assigned
	 */
	public static void assignStyleClassToNodes(String className, Node... nodes) {
		for (Node node : nodes)	{
			node.getStyleClass().add(className);
		}
	}
	
	/**
	 * Position given node at center of screen, assuming we are working in a Pane
	 * @param control Node to position
	 */
	public static void centerNode(Control control) {
		Point2D middle = getScreenCenter();
		control.setLayoutX(middle.x - control.getWidth()/2);
		control.setLayoutY(middle.y - control.getHeight()/2);
	}
		
	/**
	 * @return X-Y coordinate of middle of screen
	 */
	public static Point2D getScreenCenter() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return new Point2D((float)screenBounds.getWidth()/2, (float)screenBounds.getHeight()/2);
	}
	
	/**
	 * Uses a Timeline object to call a method on an object after a given duration.
	 * @param target Object on which method will be called
	 * @param methodName Method to call
	 * @param timeInMillis Duration to wait before calling the method
	 * @return True if the method was found on the given target, otherwise false
	 */
	public static boolean callMethodAfterTime(final Object target, String methodName, double timeInMillis) {
		Method foundMethod = null;
		try {
			foundMethod = target.getClass().getMethod(methodName);
		} catch (Exception e) {
			return false;
		}
		final Method method = foundMethod;
		Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(timeInMillis), (event) -> {
        	try {
        		method.invoke(target);
        	} catch (Exception e) {
        	}
	    }));
        timeline.playFromStart();
        return true;
	}
	
}
