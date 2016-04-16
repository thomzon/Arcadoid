package controllers.editor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.model.Game;
import data.model.Game.Platform;
import data.model.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * View controller in charge of editing Games.
 * @author Thomas Debouverie
 *
 */
public class GamesViewController implements Initializable {

	@FXML private ListView<Game> allGamesListView;
	@FXML private ComboBox<Platform> gameTypeDropdown;
	@FXML private TextField gameNameField;	
	@FXML private Label thumbnailArtworkPathLabel, backgroundArtworkPathLabel;
	@FXML private ListView<Tag> availableTagsListView, assignedTagsListView;
	@FXML private GridPane gameFieldsGridPane;
	
	private Game editedGame;
	private PlatformSpecificGameFieldsHandler platformSpecificFieldsHandler;
	private boolean ignoreSave = false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initializeValueChangeListening();
		this.setupTagsAssignmentLists();
		this.setupGameTypeDropdown();
		this.initializeGamesList();
	}
	
	private void initializeValueChangeListening() {
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.TAG_MODIFIED_NOTIFICATION, this, "tagModifiedNotification");
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.DATA_LOADED_NOTIFICATION, this, "dataLoadedNotification");
		this.gameNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			this.saveAction();
		});
	}
	
	private void setupTagsAssignmentLists() {
		ArcadoidData.sharedInstance().getAllTags().addListener(new ListChangeListener<Tag>() {
			@Override public void onChanged(Change<? extends Tag> c) {
				showSelectedGame(editedGame);
			}
		});
		this.availableTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.assignedTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	private void setupGameTypeDropdown() {
		ObservableList<Platform> platformList = FXCollections.observableArrayList();
		platformList.addAll(Platform.values());
		this.gameTypeDropdown.setItems(platformList);
		this.gameTypeDropdown.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> changeGamePlatform(newValue));
	}
	
	private void initializeGamesList() {
		this.allGamesListView.setItems(ArcadoidData.sharedInstance().getAllGames());
		this.allGamesListView.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> showSelectedGame(newValue));
		if (this.allGamesListView.getItems().size() > 0) {
			this.allGamesListView.getSelectionModel().select(0);
		} else {
			this.newAction();
		}
	}
	
	public void dataLoadedNotification() {
		this.initializeGamesList();
	}
	
	public void tagModifiedNotification(Tag tag) {
		this.showSelectedGame(this.editedGame);
	}
	
	private void changeGamePlatform(Platform platform) {
		int selectedIndex = this.allGamesListView.getSelectionModel().getSelectedIndex();
		if (this.editedGame.getPlatform() != platform) {
			this.editedGame = ArcadoidData.sharedInstance().changeGamePlatform(this.editedGame, platform);
		}
		if (this.platformSpecificFieldsHandler != null) {
			this.platformSpecificFieldsHandler.teardownForGridPane(this.gameFieldsGridPane);
		}
		this.platformSpecificFieldsHandler = PlatformSpecificGameFieldsHandler.handlerForPlatform(platform);
		this.platformSpecificFieldsHandler.setupInGridPane(this.gameFieldsGridPane);
		this.platformSpecificFieldsHandler.setEditedGame(this.editedGame);
		this.allGamesListView.getSelectionModel().select(selectedIndex);
	}
	
	private void showSelectedGame(Game selectedGame) {
		if (selectedGame == null) return;
		this.ignoreSave = true;
		this.editedGame = selectedGame;
		this.availableTagsListView.setItems(null);
		this.availableTagsListView.setItems(ArcadoidData.sharedInstance().getAllTagsExcept(this.editedGame.getAssignedTags()));
		ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
		assignedTags.addAll(this.editedGame.getAssignedTags());
		this.assignedTagsListView.setItems(null);
		this.assignedTagsListView.setItems(assignedTags);
		this.gameTypeDropdown.getSelectionModel().select(this.editedGame.getPlatform());
		this.gameNameField.setText(this.editedGame.getName());
		this.thumbnailArtworkPathLabel.setText(this.editedGame.getThumbnailArtworkPath());
		this.backgroundArtworkPathLabel.setText(this.editedGame.getBackgroundArtworkPath());
		this.platformSpecificFieldsHandler.setEditedGame(this.editedGame);
		this.ignoreSave = false;
	}
	
	private void doDeleteCurrentGame() {
		ArcadoidData.sharedInstance().deleteGame(this.editedGame);
		if (ArcadoidData.sharedInstance().getAllGames().size() == 0) {
			this.newAction();
		}
	}
	
	@FXML private void saveAction() {
		if (this.ignoreSave) return;
		this.editedGame.setName(this.gameNameField.getText());
		this.editedGame.setThumbnailArtworkPath(this.thumbnailArtworkPathLabel.getText());
		this.editedGame.setBackgroundArtworkPath(this.backgroundArtworkPathLabel.getText());
		List<Tag> assignedTags = this.assignedTagsListView.getItems();
		this.editedGame.getAssignedTags().setAll(assignedTags);
		int selectedIndex = this.allGamesListView.getSelectionModel().getSelectedIndex();
		this.allGamesListView.fireEvent(new ListView.EditEvent<>(this.allGamesListView, ListView.editCommitEvent(), this.editedGame, selectedIndex));
		this.allGamesListView.getSelectionModel().select(selectedIndex);
	}
	
	@FXML private void newAction() {
		ArcadoidData.sharedInstance().createNewGame();
		this.allGamesListView.getSelectionModel().selectLast();
	}
	
	@FXML private void deleteAction() {
		this.doDeleteCurrentGame();
	}
	
	@FXML private void pickThumbnailPathAction() {
		File file = ArtworkPathSelection.selectArtworkFile(this.thumbnailArtworkPathLabel.getScene().getWindow());
		if (file != null) {
			this.thumbnailArtworkPathLabel.setText(file.getName());
			this.saveAction();
		}
	}
	
	@FXML private void pickBackgroundPathAction() {
		File file = ArtworkPathSelection.selectArtworkFile(this.backgroundArtworkPathLabel.getScene().getWindow());
		if (file != null) {
			this.backgroundArtworkPathLabel.setText(file.getName());
			this.saveAction();
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
