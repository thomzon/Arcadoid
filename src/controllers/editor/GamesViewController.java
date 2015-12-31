package controllers.editor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import data.access.ArcadoidData;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

public class GamesViewController implements Initializable {

	@FXML
	private ListView<Game> allGamesListView;
	@FXML
	private ComboBox<Platform> gameTypeDropdown;
	@FXML
	private TextField gameNameField;	
	@FXML
	private Label thumbnailArtworkPathLabel, backgroundArtworkPathLabel;
	@FXML
	private BorderPane platformSpecificFieldsContainer;
	@FXML
	private ListView<Tag> availableTagsListView, assignedTagsListView;
	
	private Game editedGame;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTagsAssignmentLists();
		this.setupGameTypeDropdown();
		this.initializeGamesList();
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
	
	private void changeGamePlatform(Platform platform) {
		this.editedGame = ArcadoidData.sharedInstance().changeGamePlatform(this.editedGame, platform);
	}
	
	private void showSelectedGame(Game selectedGame) {
		if (selectedGame == null) return;
		this.editedGame = selectedGame;
		this.gameTypeDropdown.getSelectionModel().select(this.editedGame.getPlatform());
		this.gameNameField.setText(this.editedGame.getName());
		this.thumbnailArtworkPathLabel.setText(this.editedGame.getThumbnailArtworkPath());
		this.backgroundArtworkPathLabel.setText(this.editedGame.getBackgroundArtworkPath());
		this.availableTagsListView.setItems(ArcadoidData.sharedInstance().getAllTagsExcept(this.editedGame.getAssignedTags()));
		ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
		assignedTags.addAll(this.editedGame.getAssignedTags());
		this.assignedTagsListView.setItems(assignedTags);
		System.out.println("Test");
	}
	
	private void doDeleteCurrentGame() {
		ArcadoidData.sharedInstance().deleteGame(this.editedGame);
		if (ArcadoidData.sharedInstance().getAllGames().size() == 0) {
			this.newAction();
		}
	}
	
	@FXML
	private void saveAction() {
		this.editedGame.setName(this.gameNameField.getText());
		this.editedGame.setThumbnailArtworkPath(this.thumbnailArtworkPathLabel.getText());
		this.editedGame.setBackgroundArtworkPath(this.backgroundArtworkPathLabel.getText());
		List<Tag> assignedTags = this.assignedTagsListView.getItems();
		this.editedGame.getAssignedTags().setAll(assignedTags);
		this.allGamesListView.fireEvent(new ListView.EditEvent<>(this.allGamesListView, ListView.editCommitEvent(), this.editedGame, this.allGamesListView.getSelectionModel().getSelectedIndex()));
	}
	
	@FXML
	private void newAction() {
		ArcadoidData.sharedInstance().createNewGame();
		this.allGamesListView.getSelectionModel().selectLast();
	}
	
	@FXML
	private void deleteAction() {
		this.doDeleteCurrentGame();
	}
	
	@FXML
	private void thumbnailPathAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.showOpenDialog(null);
	}
	
	@FXML
	private void backgroundPathAction() {
		System.out.println("Back");
	}
	
	@FXML
	private void assignTagsAction() {
		List<Tag> selectedTags = this.availableTagsListView.getSelectionModel().getSelectedItems();
		this.assignedTagsListView.getItems().addAll(selectedTags);
		this.availableTagsListView.getItems().removeAll(selectedTags);
	}
	
	@FXML
	private void unassignTagsAction() {
		List<Tag> selectedTags = this.assignedTagsListView.getSelectionModel().getSelectedItems();
		this.availableTagsListView.getItems().addAll(selectedTags);
		this.assignedTagsListView.getItems().removeAll(selectedTags);
	}

}
