package views.frontend;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;
import utils.frontend.SpringInterpolator;

public class CoverflowList extends Group {

	/**
	 * Constants for animation
	 */
	private static final Duration DURATION = Duration.millis(300);
    private static final Interpolator INTERPOLATOR = new SpringInterpolator();
    private static final double SPACING = 90;
    private static final double LEFT_OFFSET = -110;
    private static final double RIGHT_OFFSET = 110;
    private static final double SCALE_SMALL = 0.7;
	
	private CoverflowListDataSource dataSource;
	private List<CoverflowItem> visibleItems = new ArrayList<CoverflowItem>();
	private int centeredIndex = 0;
	private Timeline animationTimeline;
	
	public CoverflowList(CoverflowListDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void reloadData() {
		this.stopAnimations();
		this.clearList();
		int numberOfItems = this.numberOfItems();
		this.centeredIndex = 0;
		for (int index = 0; index < numberOfItems; ++index) {
			CoverflowItem newItem = this.nodeForIndex(index);
			if (newItem != null) {
				this.visibleItems.add(newItem);
				this.getChildren().add(newItem);
				newItem.toBack();
				this.moveItemToPositionIndexAnimated(newItem, index, false);
			}
		}
	}
	
	public void scrollToItemAtIndexAnimated(int index, boolean animated) {
		this.stopAnimations();
		this.centeredIndex = index;
		if (animated) {
			this.animationTimeline = new Timeline();
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
			this.animationTimeline.play();
		}
	}
	
	private int numberOfItems() {
		if (this.dataSource != null) {
			return this.dataSource.numberOfItems();
		} else {
			return 0;
		}
	}
	
	private CoverflowItem nodeForIndex(int index) {
		if (this.dataSource != null) {
			return this.dataSource.nodeForItemAtIndex(index);
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
	
	private void stopAnimations() {
		if (this.animationTimeline != null) {
			this.animationTimeline.stop();
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
			this.animationTimeline.getKeyFrames().add(keyFrame);
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
			return 45.0;
		} else {
			return 135.0;
		}
	}
	
}
