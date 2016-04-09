package views.frontend;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;

public class CoverflowList extends Group {

	/**
	 * Constants for animation
	 */
	private static final Duration DURATION = Duration.millis(300);
    private static final Interpolator INTERPOLATOR = Interpolator.EASE_OUT;
    private static final double SPACING = 50;
    private static final double LEFT_OFFSET = -110;
    private static final double RIGHT_OFFSET = 110;
    private static final double SCALE_SMALL = 0.7;
	
	private CoverflowListDataSource dataSource;
	private List<CoverflowItem> visibleItems = new ArrayList<CoverflowItem>();
	private int centeredIndex = 0;
	private List<Timeline> itemsAnimations = new ArrayList<Timeline>();
	
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
				this.moveItemToPositionIndexAnimated(newItem, index, false);
			}
		}
	}
	
	public void scrollToItemAtIndexAnimated(int index, boolean animated) {
		this.stopAnimations();
		this.centeredIndex = index;
		for (int visibleItemIndex = 0; visibleItemIndex < this.visibleItems.size(); ++visibleItemIndex) {
			CoverflowItem item = this.visibleItems.get(visibleItemIndex);
			int newItemIndex = visibleItemIndex - index;
			this.moveItemToPositionIndexAnimated(item, newItemIndex, animated);
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
	}
	
	private void stopAnimations() {
		for (Timeline timeline : this.itemsAnimations) {
			timeline.stop();
		}
		this.itemsAnimations.clear();
	}
	
	private void moveItemToPositionIndexAnimated(CoverflowItem item, int positionIndex, boolean animated) {
		if (animated) {
			Timeline timeline = new Timeline();
			KeyFrame keyFrame = new KeyFrame(
					DURATION,
		            new KeyValue(item.translateXProperty(), this.horizontalPositionForItemAtIndex(positionIndex), INTERPOLATOR),
		            new KeyValue(item.scaleXProperty(), this.scaleForItemAtIndex(positionIndex), INTERPOLATOR),
		            new KeyValue(item.scaleYProperty(), this.scaleForItemAtIndex(positionIndex), INTERPOLATOR),
		            new KeyValue(item.angleModel(), this.angleForItemAtIndex(positionIndex), INTERPOLATOR));
			timeline.getKeyFrames().add(keyFrame);
			timeline.play();
		} else {
			item.setLayoutX(this.horizontalPositionForItemAtIndex(positionIndex));
			item.setScaleX(this.scaleForItemAtIndex(positionIndex));
			item.setScaleY(this.scaleForItemAtIndex(positionIndex));
			item.setAngle(this.angleForItemAtIndex(positionIndex));
		}
	}
	
	private double horizontalPositionForItemAtIndex(int index) {
		double position = 0;
		if (index < 0) {
			int numberOfItemsOnIndexSide = this.centeredIndex - 1;
			int indexOfItemOnIndexSide = numberOfItemsOnIndexSide - Math.abs(index);
			position = -numberOfItemsOnIndexSide*SPACING + SPACING*indexOfItemOnIndexSide + LEFT_OFFSET;
		} else if (index > 0) {
			int numberOfItemsOnIndexSide = this.numberOfItems() - (this.centeredIndex + 1);
			int indexOfItemOnIndexSide = index - 1;
			position = numberOfItemsOnIndexSide*SPACING - SPACING*indexOfItemOnIndexSide + RIGHT_OFFSET;
		}
		System.out.println("Position is (" + position + ") for item at index " + index);
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
