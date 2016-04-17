package controllers.frontend;

import java.io.File;
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
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.frontend.GameLaunchService;
import views.frontend.FrontendPane;
import views.frontend.FrontendPopup;
import views.frontend.InfoPopup;

public class GameNavigationPane extends FrontendPane implements PlayerInputObserver {

	private GameNavigationLayout layout;
	private ImageView backgroundImageView = new ImageView();
	private boolean firstAppearance = true;
	private FrontendPopup gameRunningMessagePopup;
	private List<BaseItem> displayedItems;
	private BaseItem currentItem;
	private Stack<NavigationItem> parentStack = new Stack<NavigationItem>();
	
	@Override
	public void prepareForAppearance() {
		super.prepareForAppearance();
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.DATA_LOADED_NOTIFICATION, this, "dataLoaded");
		PlayerInputService.sharedInstance().addInputObserver(this);
	}
	
	@Override
	public void setupPane() {
		super.setupPane();
		if (this.layout == null) {
			this.getChildren().add(this.backgroundImageView);
			this.backgroundImageView.fitWidthProperty().bind(this.widthProperty()); 
			this.backgroundImageView.fitHeightProperty().bind(this.heightProperty());
			this.backgroundImageView.setPreserveRatio(true);
			this.backgroundImageView.setLayoutX(0);
			this.backgroundImageView.setLayoutY(0);
			this.layout = new GameNavigationLayoutFactory().createLayoutForTypeInParentPane(GameNavigationLayoutType.COVERFLOW, this);
			this.makeChildrenVisible(false);
		}
	}
	
	@Override
	public void doLayout() {
		this.layout.setupSettingsAccess();
		this.backgroundImageView.setOpacity(1);
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
	
	private void updateBackgroundImage() {
		if (this.currentItem.getBackgroundArtworkPath() == null || this.currentItem.getBackgroundArtworkPath().length() == 0) return;
		File backgroundImageFile = new File(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH), this.currentItem.getBackgroundArtworkPath());
		if (backgroundImageFile.exists()) {
			Image image = new Image(backgroundImageFile.toURI().toString(), false);
			this.backgroundImageView.setImage(image);
			double imageHeight = this.backgroundImageView.getBoundsInParent().getHeight();
			double imageWidth = this.backgroundImageView.getBoundsInParent().getWidth();
			if (imageHeight < this.getHeight()) {
				System.out.println("Image height is " + imageHeight);
				this.backgroundImageView.setLayoutY(10);
//				this.backgroundImageView.setLayoutY((this.getHeight() - imageHeight) / 2);
			}
		}
	}
	
	@Override
	public void navigateLeft() {
		if (this.currentItem == null || this.gameRunningMessagePopup != null) return;
		int currentIndex = this.displayedItems.indexOf(this.currentItem);
		if (currentIndex >= 1) {
			this.currentItem = this.displayedItems.get(currentIndex - 1);
			this.layout.navigateToSiblingInItems(this.currentItem, this.displayedItems);
			this.updateBackgroundImage();
		}
	}
	
	@Override
	public void navigateRight() {
		if (this.currentItem == null || this.gameRunningMessagePopup != null) return;
		int currentIndex = this.displayedItems.indexOf(this.currentItem);
		if (currentIndex >= 0 && currentIndex+1 < this.displayedItems.size()) {
			this.currentItem = this.displayedItems.get(currentIndex + 1);
			this.layout.navigateToSiblingInItems(this.currentItem, this.displayedItems);
			this.updateBackgroundImage();
		}
	}
	
	@Override
	public void navigateDown() {
		if (this.currentItem == null || this.gameRunningMessagePopup != null) return;
		if (this.currentItem instanceof NavigationItem) {
			NavigationItem navigationItem = (NavigationItem)this.currentItem;
			List<BaseItem> children = navigationItem.getAllChildItems();
			if (!children.isEmpty()) {
				this.parentStack.push(navigationItem);
				this.displayedItems = children;
				this.currentItem = children.get(0);
				this.layout.navigateToChildren(children);
				this.updateBackgroundImage();
			}
		}
	}
	
	@Override
	public void navigateUp() {
		if (this.parentStack.isEmpty() || this.gameRunningMessagePopup != null) return;
		NavigationItem parentItem = this.parentStack.pop();
		List<BaseItem> siblings = ArcadoidData.sharedInstance().getSiblingsForNavigationItem(parentItem);
		this.displayedItems = siblings;
		this.currentItem = parentItem;
		this.layout.navigateToParentWithSiblings(parentItem, siblings);
		this.updateBackgroundImage();
	}
	
	@Override
	public void quitGame() {
		if (this.gameRunningMessagePopup != null) {
			UIService.getInstance().discardPopup(this.gameRunningMessagePopup);
			this.gameRunningMessagePopup = null;
		}
	}
	
	@Override
	public void confirm() {
		if (this.gameRunningMessagePopup != null) return;
		if (this.currentItem instanceof NavigationItem) {
			this.navigateDown();
		} else if (this.currentItem instanceof Game) {
			GameLaunchService.sharedInstance().runGame((Game)this.currentItem);
			this.displayGameRunningMessage();
		}
	}
	
	private void displayGameRunningMessage() {
		this.gameRunningMessagePopup = new InfoPopup(600, 200, Messages.get("frontend.msg.quitCombToDismiss"), false);
		UIService.getInstance().displayPopup(gameRunningMessagePopup);
	}
	
}