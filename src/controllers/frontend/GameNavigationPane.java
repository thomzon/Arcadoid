package controllers.frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Stack;

import controllers.frontend.GameNavigationLayoutFactory.GameNavigationLayoutType;
import data.access.ArcadoidData;
import data.access.FrontendData;
import data.access.NotificationCenter;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import data.model.BaseItem;
import data.model.Game;
import data.model.NavigationItem;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.util.Duration;
import utils.frontend.GameLaunchService;
import utils.frontend.VolumeControlService;
import utils.global.GlobalUtils;
import views.frontend.FrontendPane;
import views.frontend.FrontendPopup;
import views.frontend.InfoPopup;

/**
 * Main screen of the front-end UI. Handles user input and presents the game catalog by relying on
 * an object conforming to the GameNavigationLayout interface.
 * @author Thomas Debouverie
 *
 */
public class GameNavigationPane extends FrontendPane implements PlayerInputObserver {

	private GameNavigationLayout layout;
	private ImageView unusedBackgroundImageView = new ImageView();
	private ImageView usedBackgroundImageView = new ImageView();
	private boolean firstAppearance = true;
	private FrontendPopup gameRunningMessagePopup;
	private List<BaseItem> displayedItems;
	private BaseItem currentItem;
	private Stack<NavigationItem> parentStack = new Stack<NavigationItem>();
	private String lastDisplayedBackgroundImagePath;
	private Timeline backgroundFadeTimeline;
	
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
			this.setupBackgroundImageView(this.usedBackgroundImageView);
			this.setupBackgroundImageView(this.unusedBackgroundImageView);
			this.layout = new GameNavigationLayoutFactory().createLayoutForTypeInParentPane(GameNavigationLayoutType.COVERFLOW, this);
			this.makeChildrenVisible(false);
		}
	}
	
	private void setupBackgroundImageView(ImageView imageView) {
		this.getChildren().add(imageView);
		imageView.fitWidthProperty().bind(this.widthProperty()); 
		imageView.fitHeightProperty().bind(this.heightProperty());
		imageView.setPreserveRatio(true);
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
			FrontendData.sharedInstance().loadData();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.catalogLoad", "error.body.catalogLoad");
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
		if (this.backgroundFadeTimeline != null) return;
		String backgroundArtworkPath = this.getMostRelevantBackgroundArtworkPath();
		if (!this.backgroundImageIsDifferentThanCurrent(backgroundArtworkPath)) return;
		this.lastDisplayedBackgroundImagePath = backgroundArtworkPath;
		Image backgroundImage = null;
		if (backgroundArtworkPath != null && backgroundArtworkPath.length() > 0) {
			File backgroundImageFile = new File(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH), backgroundArtworkPath);
			if (backgroundImageFile.exists()) {
				Rectangle2D screenBounds = Screen.getPrimary().getBounds();
				backgroundImage = new Image(backgroundImageFile.toURI().toString(), screenBounds.getWidth(), screenBounds.getHeight(), true, true);
			}
		}
		this.setCurrentBackgroundImage(backgroundImage);
	}
	
	private void setCurrentBackgroundImage(Image backgroundImage) {
		this.unusedBackgroundImageView.setImage(backgroundImage);
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.unusedBackgroundImageView.setLayoutX(0);
		this.unusedBackgroundImageView.setLayoutY(0);
		if (backgroundImage != null) {
			double imageHeight = this.unusedBackgroundImageView.getBoundsInParent().getHeight();
			double imageWidth = this.unusedBackgroundImageView.getBoundsInParent().getWidth();
			if (imageHeight < screenBounds.getHeight()) {
				this.unusedBackgroundImageView.setLayoutY((screenBounds.getHeight() - imageHeight) / 2);
			}
			if (imageWidth < screenBounds.getWidth()) {
				this.unusedBackgroundImageView.setLayoutX((screenBounds.getWidth() - imageWidth) / 2);
			}
		}
		ImageView unusedImageView = this.unusedBackgroundImageView;
		this.unusedBackgroundImageView = this.usedBackgroundImageView;
		this.usedBackgroundImageView = unusedImageView;
		this.animateBackgroundImageChange();
	}
	
	private void animateBackgroundImageChange() {
		this.backgroundFadeTimeline = new Timeline();
		KeyFrame keyFrame = new KeyFrame(
				Duration.millis(300),
	            new KeyValue(this.unusedBackgroundImageView.opacityProperty(), 0, Interpolator.LINEAR),
	            new KeyValue(this.usedBackgroundImageView.opacityProperty(), 1, Interpolator.LINEAR)
	            );
		this.backgroundFadeTimeline.getKeyFrames().add(keyFrame);
		this.backgroundFadeTimeline.setOnFinished((event) -> {
			this.backgroundFadeTimeline = null;
			this.updateBackgroundImage();
		});
		this.backgroundFadeTimeline.play();
	}
	
	private boolean backgroundImageIsDifferentThanCurrent(String backgroundArtworkPath) {
		if ((backgroundArtworkPath == null || backgroundArtworkPath.length() == 0) && (this.lastDisplayedBackgroundImagePath == null || this.lastDisplayedBackgroundImagePath.length() == 0)) {
			return false;
		}
		if (backgroundArtworkPath == null || backgroundArtworkPath.length() == 0) return true;
		if (this.lastDisplayedBackgroundImagePath == null || this.lastDisplayedBackgroundImagePath.length() == 0) return true;
		return !backgroundArtworkPath.equals(this.lastDisplayedBackgroundImagePath);
	}
	
	private String getMostRelevantBackgroundArtworkPath() {
		String backgroundArtworkPath = this.currentItem.getBackgroundArtworkPath();
		if ((backgroundArtworkPath == null || backgroundArtworkPath.length() == 0) && !this.parentStack.isEmpty()) {
			NavigationItem navigationItem = this.parentStack.peek();
			while ((backgroundArtworkPath == null || backgroundArtworkPath.length() == 0) && navigationItem != null) {
				backgroundArtworkPath = navigationItem.getBackgroundArtworkPath();
				navigationItem = navigationItem.getParentItem();
			}
		}
		return backgroundArtworkPath;
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
		this.layout.stoppingGame();
		if (this.gameRunningMessagePopup != null) {
			UIService.sharedInstance().discardPopup(this.gameRunningMessagePopup);
			this.gameRunningMessagePopup = null;
		}
	}
	
	@Override
	public void confirm() {
		if (this.gameRunningMessagePopup != null) return;
		this.layout.startingGame();
		if (this.currentItem instanceof NavigationItem) {
			this.navigateDown();
		} else if (this.currentItem instanceof Game) {
			Game game = (Game)this.currentItem;
			VolumeControlService.sharedInstance().stopAudioFeedback();
			FrontendData.sharedInstance().markGameAsSeen(game);
			GameLaunchService.sharedInstance().runGame(game);
			this.displayGameRunningMessage();
		}
	}
	
	@Override
	public void addFavorite() {
		if (this.gameRunningMessagePopup != null) return;
		System.out.println("Add favorite !");
	}
	
	@Override
	public void lowerVolume() {
		if (this.gameRunningMessagePopup != null) return;
		VolumeControlService.sharedInstance().lowerVolume();
	}
	
	@Override
	public void raiseVolume() {
		if (this.gameRunningMessagePopup != null) return;
		VolumeControlService.sharedInstance().raiseVolume();
	}
	
	private void displayGameRunningMessage() {
		this.gameRunningMessagePopup = new InfoPopup(600, 200, Messages.get("frontend.msg.quitCombToDismiss"), false);
		UIService.sharedInstance().displayPopup(gameRunningMessagePopup);
	}
	
}