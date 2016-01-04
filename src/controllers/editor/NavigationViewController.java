package controllers.editor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.model.NavigationItem;
import data.model.Tag;
import data.settings.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import views.editor.NavigationItemTreeCell;

public class NavigationViewController implements Initializable {

	@FXML private TreeView<NavigationItem> navigationTreeView;
	@FXML private TextField navigationItemNameField;
	@FXML private CheckBox showEligibleGamesCheckbox, mustMatchAllTagsCheckbox;
	@FXML private Label thumbnailArtworkPathLabel, backgroundArtworkPathLabel;
	@FXML private ListView<Tag> availableTagsListView, assignedTagsListView;
	
	private TreeItem<NavigationItem> dummyRoot;
	private TreeItem<NavigationItem> editedItem;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initializeValueChangeListening();
		this.setupTagsAssignmentLists();
		this.setupTreeView();
	}
	
	private void setupTreeView() {
		NavigationItem dummyRootItem = new NavigationItem(0);
		this.dummyRoot = new TreeItem<NavigationItem>(dummyRootItem);
		this.navigationTreeView.setRoot(this.dummyRoot);
		this.addItemsToNode(ArcadoidData.sharedInstance().getRootNavigationItems(), this.dummyRoot);
		this.navigationTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.navigationTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSelectedItem(newValue));
		this.navigationTreeView.setShowRoot(false);
		this.navigationTreeView.setCellFactory(new Callback<TreeView<NavigationItem>, TreeCell<NavigationItem>>()
		{
			public TreeCell<NavigationItem> call(TreeView<NavigationItem> tree) {
				return new NavigationItemTreeCell();
			}
		});
		if (ArcadoidData.sharedInstance().getRootNavigationItems().isEmpty()) {
			this.newRootAction();
		} else {
			this.navigationTreeView.getSelectionModel().select(0);
		}
	}
	
	private void addItemsToNode(List<NavigationItem> items, TreeItem<NavigationItem> node) {
		for (NavigationItem navigationItem : items) {
			TreeItem<NavigationItem> treeItem = this.createTreeItem(navigationItem, node);			
			this.addItemsToNode(navigationItem.getSubItems(), treeItem);
		}
	}
	
	private TreeItem<NavigationItem> createTreeItem(NavigationItem navigationItem, TreeItem<NavigationItem> parentNode) {
		TreeItem<NavigationItem> treeItem = new TreeItem<NavigationItem>(navigationItem);
		parentNode.getChildren().add(treeItem);
		return treeItem;
	}
	
	private void initializeValueChangeListening() {
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.TAG_MODIFIED_NOTIFICATION, this, "tagModifiedNotification");
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.DATA_LOADED_NOTIFICATION, this, "dataLoadedNotification");
		this.navigationItemNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			this.saveAction();
		});
		this.showEligibleGamesCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			this.saveAction();
		});
		this.mustMatchAllTagsCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			this.saveAction();
		});
	}
	
	private void setupTagsAssignmentLists() {
		ArcadoidData.sharedInstance().getAllTags().addListener(new ListChangeListener<Tag>() {
			@Override public void onChanged(Change<? extends Tag> c) {
				showSelectedItem(editedItem);
			}
		});
		this.availableTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.assignedTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	public void dataLoadedNotification() {
		this.setupTreeView();
	}
	
	public void tagModifiedNotification(Tag tag) {
		this.showSelectedItem(this.editedItem);
	}
	
	private void showSelectedItem(TreeItem<NavigationItem> item) {
		if (item == null) return;
		this.editedItem = item;
		this.availableTagsListView.setItems(null);
		this.availableTagsListView.setItems(ArcadoidData.sharedInstance().getAllTagsExcept(this.editedItem.getValue().getAssignedTags()));
		ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
		assignedTags.addAll(this.editedItem.getValue().getAssignedTags());
		this.assignedTagsListView.setItems(null);
		this.assignedTagsListView.setItems(assignedTags);
		this.thumbnailArtworkPathLabel.setText(this.editedItem.getValue().getThumbnailArtworkPath());
		this.backgroundArtworkPathLabel.setText(this.editedItem.getValue().getBackgroundArtworkPath());
		// Get current values before setting fields to avoid side effects due to fields change listeners
		String currentName = this.editedItem.getValue().getName();
		boolean currentShowEligibleGames = this.editedItem.getValue().getShowEligibleGames();
		boolean currentMustMatchAllTags = this.editedItem.getValue().getGamesMustMatchAllTags();
		this.navigationItemNameField.setText(currentName);
		this.showEligibleGamesCheckbox.setSelected(currentShowEligibleGames);
		this.mustMatchAllTagsCheckbox.setSelected(currentMustMatchAllTags);
	}
	
	private void updateViewFromValues() {
		NavigationItem navigationItem = this.editedItem.getValue();
		if (navigationItem.getMainTag() != null && (navigationItem.getName().equals(Messages.get("default.navigationItemName")) || navigationItem.getName().equals(navigationItem.getMainTag().getName()))) {
			navigationItem.setName("");
		}
		this.editedItem.setValue(null);
		this.editedItem.setValue(navigationItem);
		if (navigationItem.hasOwnName()) {
			this.navigationItemNameField.setStyle("-fx-text-inner-color: black;");
		} else {
			this.navigationItemNameField.setStyle("-fx-text-inner-color: red;");
		}
		this.navigationItemNameField.setText(navigationItem.getName());
		if (navigationItem.getMainTag() != null && navigationItem.getThumbnailArtworkPath().equals(navigationItem.getMainTag().getThumbnailArtworkPath())) {
			navigationItem.setThumbnailArtworkPath("");
		}
		if (navigationItem.hasOwnThumbnailArtworkPath()) {
			this.thumbnailArtworkPathLabel.setTextFill(Color.BLACK);
		} else {
			this.thumbnailArtworkPathLabel.setTextFill(Color.RED);
		}
		if (navigationItem.getMainTag() != null && navigationItem.getBackgroundArtworkPath().equals(navigationItem.getMainTag().getBackgroundArtworkPath())) {
			navigationItem.setBackgroundArtworkPath("");
		}
		if (navigationItem.hasOwnBackgroundArtworkPath()) {
			this.backgroundArtworkPathLabel.setTextFill(Color.BLACK);
		} else {
			this.backgroundArtworkPathLabel.setTextFill(Color.RED);
		}
		this.mustMatchAllTagsCheckbox.setDisable(!navigationItem.getShowEligibleGames());
	}
	
	private void doDeleteCurrentItem() {
		ArcadoidData.sharedInstance().deleteNavigationItem(this.editedItem.getValue());
		this.editedItem.getParent().getChildren().remove(this.editedItem);
		if (ArcadoidData.sharedInstance().getRootNavigationItems().isEmpty()) {
			this.newRootAction();
		} else {
			this.navigationTreeView.getSelectionModel().selectFirst();
		}
	}
	
	@FXML private void saveAction() {
		this.editedItem.getValue().setName(this.navigationItemNameField.getText());
		this.editedItem.getValue().setThumbnailArtworkPath(this.thumbnailArtworkPathLabel.getText());
		this.editedItem.getValue().setBackgroundArtworkPath(this.backgroundArtworkPathLabel.getText());
		this.editedItem.getValue().setShowEligibleGames(this.showEligibleGamesCheckbox.isSelected());
		this.editedItem.getValue().setGamesMustMatchAllTags(this.mustMatchAllTagsCheckbox.isSelected());
		List<Tag> assignedTags = this.assignedTagsListView.getItems();
		this.editedItem.getValue().getAssignedTags().setAll(assignedTags);
		this.updateViewFromValues();
	}
	
	@FXML private void newChildAction() {
		NavigationItem item = ArcadoidData.sharedInstance().createNewNavigationItemWithParent(this.editedItem.getValue());
		TreeItem<NavigationItem> treeItem = this.createTreeItem(item, this.editedItem);
		this.navigationTreeView.getSelectionModel().select(treeItem);
	}
	
	@FXML private void newSiblingAction() {
		TreeItem<NavigationItem> commonParent = this.editedItem.getParent();
		NavigationItem navigationItemParent = null;
		if (commonParent != this.dummyRoot) {
			navigationItemParent = commonParent.getValue();
		}
		NavigationItem item = ArcadoidData.sharedInstance().createNewNavigationItemWithParent(navigationItemParent);
		TreeItem<NavigationItem> treeItem = this.createTreeItem(item, commonParent);
		this.navigationTreeView.getSelectionModel().select(treeItem);
	}
	
	@FXML private void newRootAction() {
		NavigationItem item = ArcadoidData.sharedInstance().createNewNavigationItemWithParent(null);
		TreeItem<NavigationItem> treeItem = this.createTreeItem(item, this.dummyRoot);
		this.navigationTreeView.getSelectionModel().select(treeItem);
	}
	
	@FXML private void deleteAction() {
		if (this.editedItem.getValue().getSubItems().isEmpty()) {
			this.doDeleteCurrentItem();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(Messages.get("alert.title"));
			alert.setHeaderText(Messages.get("confirmation.header.deleteNavigationItem"));
			alert.setContentText(Messages.get("confirmation.body.deleteNavigationItemWithSubitems"));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				this.doDeleteCurrentItem();
			}
		}
	}
	
	@FXML private void pickThumbnailPathAction() {
		File file = ArtworkPathSelection.selectArtworkFile(this.thumbnailArtworkPathLabel.getScene().getWindow());
		if (file != null) {
			this.thumbnailArtworkPathLabel.setText(file.getAbsolutePath());
		}
	}
	
	@FXML private void pickBackgroundPathAction() {
		File file = ArtworkPathSelection.selectArtworkFile(this.backgroundArtworkPathLabel.getScene().getWindow());
		if (file != null) {
			this.backgroundArtworkPathLabel.setText(file.getAbsolutePath());
		}
	}
	
	@FXML private void clearThumbnailPathAction() {
		this.thumbnailArtworkPathLabel.setText("");
		this.saveAction();
	}
	
	@FXML private void clearBackgroundPathAction() {
		this.backgroundArtworkPathLabel.setText("");
		this.saveAction();
	}
	
	@FXML private void assignTagsAction() {
		List<Tag> selectedTags = this.availableTagsListView.getSelectionModel().getSelectedItems();
		this.assignedTagsListView.getItems().addAll(selectedTags);
		this.availableTagsListView.getItems().removeAll(selectedTags);
		this.saveAction();
	}
	
	@FXML private void unassignTagsAction() {
		List<Tag> selectedTags = this.assignedTagsListView.getSelectionModel().getSelectedItems();
		this.availableTagsListView.getItems().addAll(selectedTags);
		this.assignedTagsListView.getItems().removeAll(selectedTags);
		this.saveAction();
	}

}
