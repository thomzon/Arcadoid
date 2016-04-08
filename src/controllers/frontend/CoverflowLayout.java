package controllers.frontend;

import java.util.ArrayList;
import java.util.List;

import data.model.BaseItem;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;
import utils.frontend.MouseAutohideBehaviour;
import utils.frontend.UIUtils;
import views.frontend.CoverflowItem;
import views.frontend.FrontendPane;

public class CoverflowLayout implements GameNavigationLayout {

	/**
	 * Constants for animation
	 */
	private static final Duration DURATION = Duration.millis(400);
    private static final Interpolator INTERPOLATOR = Interpolator.EASE_OUT;
    private static final double SPACING = 50;
    private static final double LEFT_OFFSET = -110;
    private static final double RIGHT_OFFSET = 110;
    private static final double SCALE_SMALL = 0.7;
    
    /**
     * UI elements
     */
	private FrontendPane parentPane;
	private Button settingsButton, syncButton;
	private MouseAutohideBehaviour mouseBehaviour = new MouseAutohideBehaviour();
	private Group centeredGroup, leftItemsGroup, centerItemGroup, rightItemsGroup;
    private StackPane selectedTextContainer;
    private Text selectedText;
    
	/**
	 * Items model
	 */
	private Timeline itemsAnimationTimeline;
	private List<BaseItem> displayedItems;
	private List<CoverflowItem> coverflowItems = new ArrayList<CoverflowItem>();
	
	public CoverflowLayout(FrontendPane parentPane) {
		this.parentPane = parentPane;
		this.createAllNodes();
	}

	@Override
	public void prepareForParentPaneDisappearance() {
		this.mouseBehaviour.stopBehaviour();
	}
	
	@Override
	public void setupSettingsAccess() {
		this.layoutAllNode();
		this.mouseBehaviour.setupInPane(this.parentPane, new Node[]{this.settingsButton, this.syncButton});
		this.mouseBehaviour.startBehaviour();
		this.centeredGroup.setOpacity(1);
		this.selectedTextContainer.setOpacity(1);
	}
	
	@Override
	public void reloadWithDisplayedItems(List<BaseItem> items) {
		this.displayedItems = items;
		this.coverflowItems.clear();
		this.coverflowItems.addAll(this.createCoverflowItemsForDisplayedItems());
		this.updateForDisplayedItem(items.get(0));
	}
	
	@Override
	public void navigateToSiblingInItems(BaseItem item, List<BaseItem> items) {
		this.updateForDisplayedItem(item);
	}
	
	@Override
	public void navigateToChildren(List<BaseItem> childrenItems) {
		this.reloadWithDisplayedItems(childrenItems);
	}
	
	@Override
	public void navigateToParentWithSiblings(BaseItem parent, List<BaseItem> siblings) {
		this.displayedItems = siblings;
		this.coverflowItems.clear();
		this.coverflowItems.addAll(this.createCoverflowItemsForDisplayedItems());
		this.updateForDisplayedItem(parent);
	}
	
	private void createAllNodes() {
		this.createButtons();
		this.createItemDisplayNodes();
	}
	
	private void createButtons() {
		this.settingsButton = UIUtils.createButton("", false);
		this.settingsButton.setOnAction((event) -> {
			UIService.getInstance().displaySettings(true);
		});
		this.settingsButton.setId("settings-button");
		
		this.syncButton = UIUtils.createButton("", false);
		this.syncButton.setOnAction((event) -> {
			UIService.getInstance().startCatalogSync();
		});
		this.syncButton.setId("sync-button");
		
		this.parentPane.getChildren().addAll(this.settingsButton, this.syncButton);
	}
	
	private void createItemDisplayNodes() {
		this.centeredGroup = new Group();
		this.centerItemGroup = new Group();
		this.leftItemsGroup = new Group();
		this.rightItemsGroup = new Group();
		this.centeredGroup.getChildren().addAll(this.leftItemsGroup, this.rightItemsGroup, this.centerItemGroup);
		
		this.selectedText = new Text();
		this.selectedText.setId("coverflow-selected-item-text");
		this.selectedTextContainer = new StackPane();
		this.selectedTextContainer.setPrefWidth(Screen.getPrimary().getBounds().getWidth());
		this.selectedTextContainer.getChildren().add(this.selectedText);
		
		this.parentPane.getChildren().addAll(this.centeredGroup, this.selectedTextContainer);
	}
	
	private void layoutAllNode() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.syncButton.setLayoutX(screenBounds.getWidth() - this.syncButton.getWidth());
		this.centeredGroup.setLayoutY(screenBounds.getHeight()/2 - CoverflowItem.HEIGHT/2);
        this.centeredGroup.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
        this.selectedTextContainer.setLayoutY(screenBounds.getHeight()/2 - CoverflowItem.HEIGHT);
	}
	
	private List<CoverflowItem> createCoverflowItemsForDisplayedItems() {
		List<CoverflowItem> createdItems = new ArrayList<CoverflowItem>();
		for (BaseItem item : this.displayedItems) {
			CoverflowItem coverflowItem = new CoverflowItem(item);
			createdItems.add(coverflowItem);
		}
		return createdItems;
	}
	
	private void updateForDisplayedItem(BaseItem displayedItem) {
		int displayedItemIndex = this.displayedItems.indexOf(displayedItem);
		if (displayedItemIndex < 0) return;
		// Move items to new homes in groups
        this.leftItemsGroup.getChildren().clear();
        this.centerItemGroup.getChildren().clear();
        this.rightItemsGroup.getChildren().clear();
        for (int index = 0; index < displayedItemIndex; ++index) {
        	this.leftItemsGroup.getChildren().add(this.coverflowItems.get(index));
        }
        this.centerItemGroup.getChildren().add(this.coverflowItems.get(displayedItemIndex));
        for (int index = this.coverflowItems.size() - 1; index > displayedItemIndex; --index) {
            this.rightItemsGroup.getChildren().add(this.coverflowItems.get(index));
        }
        // Stop old _timeline if there is one running
        if (this.itemsAnimationTimeline != null) this.itemsAnimationTimeline.stop();
        // Create _timeline to animate to new positions
        this.itemsAnimationTimeline = new Timeline();
        // Add key frames for left items
        final ObservableList<KeyFrame> keyFrames = this.itemsAnimationTimeline.getKeyFrames();
        for (int index = 0; index < this.leftItemsGroup.getChildren().size(); ++index) {
            final CoverflowItem coverflowItem = this.coverflowItems.get(index);
            double newX = -this.leftItemsGroup.getChildren().size() * SPACING + SPACING*index + LEFT_OFFSET;
            keyFrames.add(
            		new KeyFrame(DURATION,
	                new KeyValue(coverflowItem.translateXProperty(), newX, INTERPOLATOR),
	                new KeyValue(coverflowItem.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
	                new KeyValue(coverflowItem.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
	                new KeyValue(coverflowItem.angleModel(), 45.0, INTERPOLATOR)));
        }
        // Add key frame for center item
        final CoverflowItem centerItem = this.coverflowItems.get(displayedItemIndex);
        keyFrames.add(
        		new KeyFrame(DURATION,
                new KeyValue(centerItem.translateXProperty(), 0, INTERPOLATOR),
                new KeyValue(centerItem.scaleXProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.scaleYProperty(), 1.0, INTERPOLATOR),
                new KeyValue(centerItem.angleModel(), 90.0, INTERPOLATOR)));
        // Change text for selected item
        this.selectedText.setText(displayedItem.getName());
        // Add key frames for right items
        for (int index = 0; index < this.rightItemsGroup.getChildren().size(); index++) {
            final CoverflowItem coverflowItem = this.coverflowItems.get(this.coverflowItems.size() - index - 1);
            final double newX = this.rightItemsGroup.getChildren().size() * SPACING - SPACING*index + RIGHT_OFFSET;
            keyFrames.add(
            		new KeyFrame(DURATION,
                    new KeyValue(coverflowItem.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(coverflowItem.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(coverflowItem.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(coverflowItem.angleModel(), 135.0, INTERPOLATOR)));
        }
        // Play animation
        this.itemsAnimationTimeline.play();
	}

}
