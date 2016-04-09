package controllers.frontend;

import java.util.List;

import data.model.BaseItem;
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
    private CoverflowList coverflowList;
    
	/**
	 * Items model
	 */
	private List<BaseItem> displayedItems;
	
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
		this.coverflowList.setOpacity(1);
		this.selectedTextContainer.setOpacity(1);
	}
	
	@Override
	public void reloadWithDisplayedItems(List<BaseItem> items) {
		this.displayedItems = items;
		this.coverflowList.reloadData();
		this.selectedText.setText(items.get(0).getName());
	}
	
	@Override
	public void navigateToSiblingInItems(BaseItem item, List<BaseItem> items) {
		int itemIndex = items.indexOf(item);
		if (itemIndex >= 0) {
			this.coverflowList.scrollToItemAtIndexAnimated(itemIndex, true);
			this.selectedText.setText(item.getName());
		}
	}
	
	@Override
	public void navigateToChildren(List<BaseItem> childrenItems) {
		this.reloadWithDisplayedItems(childrenItems);
	}
	
	@Override
	public void navigateToParentWithSiblings(BaseItem parent, List<BaseItem> siblings) {
		this.displayedItems = siblings;
		this.coverflowList.reloadData();
		this.selectedText.setText(parent.getName());
		int itemIndex = siblings.indexOf(parent);
		if (itemIndex >= 0) {
			this.coverflowList.scrollToItemAtIndexAnimated(itemIndex, false);
		}
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
		
		this.coverflowList = new CoverflowList(this);
		this.parentPane.getChildren().addAll(this.coverflowList, this.selectedTextContainer);
	}
	
	private void layoutAllNode() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.syncButton.setLayoutX(screenBounds.getWidth() - this.syncButton.getWidth());
		this.coverflowList.setLayoutY(screenBounds.getHeight()/2 - CoverflowItem.HEIGHT/2);
		this.coverflowList.setLayoutX(screenBounds.getWidth()/2 - CoverflowItem.WIDTH/2);
        this.selectedTextContainer.setLayoutY(screenBounds.getHeight()/2 - CoverflowItem.HEIGHT);
	}

	@Override
	public int numberOfItems() {
		return this.displayedItems.size();
	}

	@Override
	public CoverflowItem nodeForItemAtIndex(int index) {
		return CoverflowItemPool.dequeueItem();
	}

}
