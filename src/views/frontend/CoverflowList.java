package views.frontend;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;
import utils.frontend.EaseOutInterpolator;
import utils.frontend.EaseOutInterpolator.EaseOutFunction;

/**
 * Horizontal list that displays CoverflowItem objects.
 * @author Thomas Debouverie
 *
 */
public class CoverflowList extends Group {

	/**
	 * Constants for animation
	 */
	private static final Duration DURATION = Duration.millis(500);
    private static final Interpolator INTERPOLATOR = new EaseOutInterpolator(EaseOutFunction.EXPONENTIAL);
    private static final double SPACING = 150;//90;
    private static final double LEFT_OFFSET = -110;
    private static final double RIGHT_OFFSET = 110;
    private static final double SCALE_SMALL = 0.7;
    private static final double LEFT_SIDE_ANGLE = 60.0;//45.0;
    private static final double RIGHT_SIDE_ANGLE = 120.0;//135.0;
	
	private CoverflowListDataSource dataSource;
	private List<CoverflowItem> visibleItems = new ArrayList<CoverflowItem>();
	private int centeredIndex = 0;
	private Timeline itemScrollAnimationsTimeline, listAnimationsTimeline;
	private boolean focusedMode = true;
	
	public CoverflowList(CoverflowListDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Stops all animations and reloads all nodes.
	 */
	public void reloadData() {
		this.stopItemScrollAnimations();
		this.clearList();
		int numberOfItems = this.numberOfItems();
		this.centeredIndex = 0;
		for (int index = 0; index < numberOfItems; ++index) {
			CoverflowItem newItem = this.nodeForIndex(index);
			if (newItem != null) {
				newItem.setReflection(this.focusedMode ? 0.5 : 0);
				this.visibleItems.add(newItem);
				this.getChildren().add(newItem);
				newItem.toBack();
				this.moveItemToPositionIndexAnimated(newItem, index, false);
			}
		}
	}
	
	/**
	 * Centers item at given index.
	 * @param index Index of item to center
	 * @param animated If true, the move to centering the item will be animated
	 */
	public void scrollToItemAtIndexAnimated(int index, boolean animated) {
		this.stopItemScrollAnimations();
		this.centeredIndex = index;
		if (animated) {
			this.itemScrollAnimationsTimeline = new Timeline();
		}
		for (int visibleItemIndex = 0; visibleItemIndex < this.visibleItems.size(); ++visibleItemIndex) {
			CoverflowItem item = this.visibleItems.get(visibleItemIndex);
			int newItemIndex = visibleItemIndex - index;
			this.moveItemToPositionIndexAnimated(item, newItemIndex, animated);
			if (visibleItemIndex < this.centeredIndex) {
				item.toFront();
			} else {
				item.toBack();
			}
		}
		if (animated) {
			this.itemScrollAnimationsTimeline.play();
		}
	}
	
	/**
	 * Moves the list vertically and changes its "focused" mode.
	 * @param verticalPosition Vertical position in its parent where the list must move
	 * @param focusedMode If true, all items will be full opacity and with reflection. If false, opacity will be redued and reflection removed.
	 * @param animated If true, the move will be animated
	 */
	public void moveToVerticalPositionAndFocusedMode(double verticalPosition, boolean focusedMode, boolean animated) {
		this.focusedMode = focusedMode;
		this.stopListAnimations();
		double newOpacity = focusedMode ? 1.0 : 0.5;
		if (animated) {
			this.listAnimationsTimeline = new Timeline();
			KeyFrame keyFrame = new KeyFrame(
					DURATION,
		            new KeyValue(this.layoutYProperty(), verticalPosition, INTERPOLATOR),
		            new KeyValue(this.opacityProperty(), newOpacity, INTERPOLATOR)
		            );
			this.listAnimationsTimeline.getKeyFrames().add(keyFrame);
			double reflection = focusedMode ? 0.5 : 0;
			for (CoverflowItem item : this.visibleItems) {
				KeyFrame reflectionKeyFrame = new KeyFrame(
						DURATION,
			            new KeyValue(item.reflectionModel(), reflection, INTERPOLATOR)
			            );
				this.listAnimationsTimeline.getKeyFrames().add(reflectionKeyFrame);
			}
			this.listAnimationsTimeline.play();
		} else {
			this.setLayoutY(verticalPosition);
			this.setOpacity(newOpacity);
			double reflection = focusedMode ? 0.5 : 0;
			for (CoverflowItem item : this.visibleItems) {
				item.setReflection(reflection);
			}
		}
	}
	
	private int numberOfItems() {
		if (this.dataSource != null) {
			return this.dataSource.numberOfItemsInCoverflowList(this);
		} else {
			return 0;
		}
	}
	
	private CoverflowItem nodeForIndex(int index) {
		if (this.dataSource != null) {
			return this.dataSource.nodeForItemAtIndex(index, this);
		} else {
			return null;
		}
	}
	
	private void clearList() {
		this.getChildren().clear();
		for (CoverflowItem item : this.visibleItems) {
			CoverflowItemPool.recycleItem(item);
		}
		this.visibleItems.clear();
	}
	
	private void stopItemScrollAnimations() {
		if (this.itemScrollAnimationsTimeline != null) {
			this.itemScrollAnimationsTimeline.stop();
		}
	}
	
	private void stopListAnimations() {
		if (this.listAnimationsTimeline != null) {
			this.listAnimationsTimeline.stop();
		}
	}
	
	private void moveItemToPositionIndexAnimated(CoverflowItem item, int positionIndex, boolean animated) {
		double horizontalPosition = this.horizontalPositionForItemAtIndex(positionIndex);
		double scale = this.scaleForItemAtIndex(positionIndex);
		double angle = this.angleForItemAtIndex(positionIndex);
		if (animated) {
			KeyFrame keyFrame = new KeyFrame(
					DURATION,
		            new KeyValue(item.layoutXProperty(), horizontalPosition, INTERPOLATOR),
		            new KeyValue(item.scaleXProperty(), scale, INTERPOLATOR),
		            new KeyValue(item.scaleYProperty(), scale, INTERPOLATOR),
		            new KeyValue(item.angleModel(), angle, INTERPOLATOR)
		            );
			this.itemScrollAnimationsTimeline.getKeyFrames().add(keyFrame);
		} else {
			item.setLayoutX(horizontalPosition);
			item.setScaleX(scale);
			item.setScaleY(scale);
			item.setAngle(angle);
		}
	}
	
	private double horizontalPositionForItemAtIndex(int index) {
		double position = 0;
		if (index < 0) {
			int numberOfItemsOnIndexSide = this.centeredIndex - 1;
			int indexOfItemOnIndexSide = numberOfItemsOnIndexSide - Math.abs(index);
			position = -numberOfItemsOnIndexSide*SPACING + SPACING*indexOfItemOnIndexSide + LEFT_OFFSET;
		} else if (index > 0) {
			position = SPACING*index + RIGHT_OFFSET;
		}
		return position;
	}
	
	private double scaleForItemAtIndex(int index) {
		if (index == 0) {
			return 1.0;
		} else {
			return SCALE_SMALL;
		}
	}
	
	private double angleForItemAtIndex(int index) {
		if (index == 0) {
			return 90.0;
		} else if (index < 0) {
			return LEFT_SIDE_ANGLE;
		} else {
			return RIGHT_SIDE_ANGLE;
		}
	}
	
}
