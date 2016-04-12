package controllers.frontend;

import java.util.List;
import java.util.Stack;

import controllers.frontend.GameNavigationLayoutFactory.GameNavigationLayoutType;
import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import data.model.BaseItem;
import data.model.Game;
import data.model.NavigationItem;
import javafx.application.Platform;
import utils.frontend.GameLaunchService;
import views.frontend.FrontendPane;

public class GameNavigationPane extends FrontendPane implements PlayerInputObserver {

	private GameNavigationLayout layout;
	private boolean firstAppearance = true;
	private List<BaseItem> displayedItems;
	private BaseItem currentItem;
	private Stack<NavigationItem> parentStack = new Stack<NavigationItem>();
	
	@Override
	public void prepareForAppearance() {
		super.prepareForAppearance();
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.DATA_LOADED_NOTIFICATION, this, "dataLoaded");
		NotificationCenter.sharedInstance().addObserver(GameLaunchService.GAME_WILL_LAUNCH_NOTIFICATION, this, "gameWillLaunch");
		NotificationCenter.sharedInstance().addObserver(GameLaunchService.GAME_WILL_QUIT_NOTIFICATION, this, "gameWillQuit");
		PlayerInputService.sharedInstance().addInputObserver(this);
	}
	
	@Override
	public void setupPane() {
		super.setupPane();
		if (this.layout == null) {
			this.layout = new GameNavigationLayoutFactory().createLayoutForTypeInParentPane(GameNavigationLayoutType.COVERFLOW, this);
			this.makeChildrenVisible(false);
		}
	}
	
	@Override
	public void doLayout() {
		this.layout.setupSettingsAccess();
		if (this.firstAppearance) {
			this.firstAppearance = false;
			this.initialAppearance();
		}
	}
	
	private void initialAppearance() {
		try {
			ArcadoidData.sharedInstance().loadData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void prepareForDisappearance() {
		super.prepareForDisappearance();
		this.layout.prepareForParentPaneDisappearance();
		NotificationCenter.sharedInstance().removeObserver(this);
		PlayerInputService.sharedInstance().removeInputObserver(this);
	}
	
	public void dataLoaded() {
		ArcadoidData.sharedInstance().buildCompleteCatalog();
		this.displayedItems = ArcadoidData.sharedInstance().getRootItems();
		this.layout.reloadWithDisplayedItems(this.displayedItems);
		if (!this.displayedItems.isEmpty()) {
			this.currentItem = this.displayedItems.get(0);
		}
	}
	
	@Override
	public void navigateLeft() {
		if (this.currentItem == null) return;
		int currentIndex = this.displayedItems.indexOf(this.currentItem);
		if (currentIndex >= 1) {
			this.currentItem = this.displayedItems.get(currentIndex - 1);
			this.layout.navigateToSiblingInItems(this.currentItem, this.displayedItems);
		}
	}
	
	@Override
	public void navigateRight() {
		if (this.currentItem == null) return;
		int currentIndex = this.displayedItems.indexOf(this.currentItem);
		if (currentIndex >= 0 && currentIndex+1 < this.displayedItems.size()) {
			this.currentItem = this.displayedItems.get(currentIndex + 1);
			this.layout.navigateToSiblingInItems(this.currentItem, this.displayedItems);
		}
	}
	
	@Override
	public void navigateDown() {
		if (this.currentItem == null) return;
		if (this.currentItem instanceof NavigationItem) {
			NavigationItem navigationItem = (NavigationItem)this.currentItem;
			List<BaseItem> children = navigationItem.getAllChildItems();
			if (!children.isEmpty()) {
				this.parentStack.push(navigationItem);
				this.displayedItems = children;
				this.currentItem = children.get(0);
				this.layout.navigateToChildren(children);
			}
		}
	}
	
	@Override
	public void navigateUp() {
		if (this.parentStack.isEmpty()) return;
		NavigationItem parentItem = this.parentStack.pop();
		List<BaseItem> siblings = ArcadoidData.sharedInstance().getSiblingsForNavigationItem(parentItem);
		this.displayedItems = siblings;
		this.currentItem = parentItem;
		this.layout.navigateToParentWithSiblings(parentItem, siblings);
	}
	
	@Override
	public void confirm() {
		if (this.currentItem instanceof NavigationItem) {
			this.navigateDown();
		} else if (this.currentItem instanceof Game) {
			GameLaunchService.sharedInstance().runGame((Game)this.currentItem);
		}
	}
	
	public void gameWillLaunch() {
		Platform.runLater(() -> {
			PlayerInputService.sharedInstance().removeInputObserver(this);
		});
	}
	
	public void gameWillQuit() {
		Platform.runLater(() -> {
			PlayerInputService.sharedInstance().addInputObserver(this);
			this.getScene().getWindow().requestFocus();
		});
	}
	
}