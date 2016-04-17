package controllers.frontend;

import java.util.List;

import data.access.ArcadoidData;
import data.model.BaseItem;
import data.model.NavigationItem;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import utils.frontend.MouseAutohideBehaviour;
import utils.frontend.UIUtils;
import views.frontend.CoverflowItem;
import views.frontend.CoverflowItemPool;
import views.frontend.CoverflowList;
import views.frontend.CoverflowListDataSource;
import views.frontend.FrontendPane;

public class CoverflowLayout implements GameNavigationLayout, CoverflowListDataSource {
   
    /**
     * UI elements
     */
	private FrontendPane parentPane;
	private Button settingsButton, syncButton;
	private MouseAutohideBehaviour mouseBehaviour = new MouseAutohideBehaviour();
    private StackPane selectedTextContainer;
    private Text selectedText;
    private CoverflowList focusedCoverflowList, previousCoverflowList, nextCoverflowList, unusedCoverflowList;
    
	/**
	 * Items model
	 */
	private List<BaseItem> displayedItems;
	private List<BaseItem> previousItems;
	private NavigationItem parentItem;
    private BaseItem focusedItem;
	
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
		this.mouseBehaviour.setupInPane(this.parentPane, new Node[]{this.settingsButton, this.syncButton});
		this.mouseBehaviour.startBehaviour();
		this.focusedCoverflowList.setOpacity(1);
		this.previousCoverflowList.setOpacity(1);
		this.nextCoverflowList.setOpacity(1);
		this.unusedCoverflowList.setOpacity(1);
		this.selectedTextContainer.setOpacity(1);
		this.layoutAllNode();
	}
	
	@Override
	public void reloadWithDisplayedItems(List<BaseItem> items) {
		this.displayedItems = items;
		this.setFocusedItem(items.get(0), true);
		this.focusedCoverflowList.reloadData();
		this.previousCoverflowList.reloadData();
	}
	
	@Override
	public void navigateToSiblingInItems(BaseItem item, List<BaseItem> items) {
		int itemIndex = items.indexOf(item);
		if (itemIndex >= 0) {
			this.focusedCoverflowList.scrollToItemAtIndexAnimated(itemIndex, true);
			this.setFocusedItem(item, false);
		}
	}
	
	@Override
	public void navigateToChildren(List<BaseItem> childrenItems) {
		this.previousItems = this.displayedItems;
		this.parentItem = (NavigationItem)this.focusedItem;
		
		this.previousCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForLeavingUnusedList(), false, true);
		this.focusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForPreviousList(), false, true);
		this.nextCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForCurrentList(), true, true);
		this.unusedCoverflowList.setLayoutY(this.verticalPositionForEnteringUnusedList());
		this.unusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForNextList(), false, true);
		
		CoverflowList unusedList = this.unusedCoverflowList;
		this.unusedCoverflowList = this.previousCoverflowList;
		this.previousCoverflowList = this.focusedCoverflowList;
		this.focusedCoverflowList = this.nextCoverflowList;
		this.nextCoverflowList = unusedList;
		
		this.displayedItems = childrenItems;
		this.focusedItem = childrenItems.get(0);
		
		this.nextCoverflowList.reloadData();
	}
	
	@Override
	public void navigateToParentWithSiblings(BaseItem parent, List<BaseItem> siblings) {
		if (this.parentItem != null && this.parentItem.getParentItem() != null) {
			this.previousItems = ArcadoidData.sharedInstance().getSiblingsForNavigationItem(this.parentItem.getParentItem());
			this.parentItem = this.parentItem.getParentItem();
		} else {
			this.previousItems = null;
			this.parentItem = null;
		}
		
		this.displayedItems = siblings;
		
		this.unusedCoverflowList.setLayoutY(this.verticalPositionForLeavingUnusedList());
		this.unusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForPreviousList(), false, true);
		this.previousCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForCurrentList(), true, true);
		this.focusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForNextList(), false, true);
		this.nextCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForEnteringUnusedList(), false, true);
		
		CoverflowList unusedList = this.unusedCoverflowList;
		this.unusedCoverflowList = this.nextCoverflowList;
		this.nextCoverflowList = this.focusedCoverflowList;
		this.focusedCoverflowList = this.previousCoverflowList;
		this.previousCoverflowList = unusedList;
		
		this.previousCoverflowList.reloadData();
		this.focusedItem = parent;
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
		this.selectedText = new Text();
		this.selectedText.setId("coverflow-selected-item-text");
		this.selectedTextContainer = new StackPane();
		this.selectedTextContainer.setPrefWidth(Screen.getPrimary().getBounds().getWidth());
		this.selectedTextContainer.getChildren().add(this.selectedText);
		
		this.focusedCoverflowList = new CoverflowList(this);
		this.previousCoverflowList = new CoverflowList(this);
		this.nextCoverflowList = new CoverflowList(this);
		this.unusedCoverflowList = new CoverflowList(this);
		this.parentPane.getChildren().addAll(this.focusedCoverflowList, this.previousCoverflowList, this.nextCoverflowList, this.unusedCoverflowList);//, this.selectedTextContainer);
	}
	
	private void layoutAllNode() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		
		this.focusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForCurrentList(), true, false);
		this.focusedCoverflowList.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
		this.previousCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForPreviousList(), false, false);
		this.previousCoverflowList.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
		this.nextCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForNextList(), false, false);
		this.nextCoverflowList.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
		this.unusedCoverflowList.moveToVerticalPositionAndFocusedMode(this.verticalPositionForEnteringUnusedList(), false, false);
		this.unusedCoverflowList.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
		
		this.syncButton.setLayoutX(screenBounds.getWidth() - this.syncButton.getWidth());
        this.selectedTextContainer.setLayoutY(screenBounds.getHeight()/2 - CoverflowItem.HEIGHT);
	}
	
	private void setFocusedItem(BaseItem item, boolean showChildrenImmediately) {
		this.focusedItem = item;
		this.selectedText.setText(this.focusedItem.getName());
		if (showChildrenImmediately) {
			this.nextCoverflowList.reloadData();
		} else {
			this.nextCoverflowList.reloadData();
		}
	}

	private double verticalPositionForCurrentList() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return screenBounds.getHeight()/2 - CoverflowItem.HEIGHT/2;
	}
	
	private double verticalPositionForPreviousList() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return screenBounds.getHeight()/5 - CoverflowItem.HEIGHT/2;
	}
	
	private double verticalPositionForNextList() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return screenBounds.getHeight()/5 * 4 - CoverflowItem.HEIGHT/2;
	}
	
	private double verticalPositionForLeavingUnusedList() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return -screenBounds.getHeight() / 5 * 2;
	}
	
	private double verticalPositionForEnteringUnusedList() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		return screenBounds.getHeight() / 5 * 7;
	}
	
	@Override
	public int numberOfItemsInCoverflowList(CoverflowList coverflowList) {
		List<BaseItem> itemsList = this.relevantListForCoverflowList(coverflowList);
		return itemsList != null ? itemsList.size() : 0;
	}

	@Override
	public CoverflowItem nodeForItemAtIndex(int index, CoverflowList coverflowList) {
		CoverflowItem coverflowItem = CoverflowItemPool.dequeueItem();
		coverflowItem.setBaseItem(this.relevantListForCoverflowList(coverflowList).get(index));
		return coverflowItem;
	}
	
	private List<BaseItem> relevantListForCoverflowList(CoverflowList coverflowList) {
		if (coverflowList == this.focusedCoverflowList) {
			return this.displayedItems;
		} else if (coverflowList == this.previousCoverflowList) {
			return this.previousItems;
		} else if (coverflowList == this.nextCoverflowList) {
			if (this.focusedItem instanceof NavigationItem) {
				return ((NavigationItem)this.focusedItem).getAllChildItems();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
