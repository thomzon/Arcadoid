package controllers.editor;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import data.model.Game;
import data.model.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class GamesViewController implements Initializable {

	@FXML
	private ListView<Game> allGamesListView;
	@FXML
	private TextField gameNameField;	
	@FXML
	private Label thumbnailArtworkPathLabel;
	@FXML
	private Label backgroundArtworkPathLabel;
	@FXML
	private ListView<Tag> availableTagsListView;
	@FXML
	private ListView<Tag> assignedTagsListView;
	
	private Game editedGame;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTagsAssignmentLists();
	}
	
	private void setupTagsAssignmentLists() {
		this.availableTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.assignedTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	private void initializeGamesList() {
//		this.allTagsListView.setItems(this.tagsAccessor.getAllTags());
		this.allGamesListView.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> showSelectedGame(newValue));
		if (this.allGamesListView.getItems().size() > 0) {
			this.allGamesListView.getSelectionModel().select(0);
		} else {
			this.newAction();
		}
	}
	
	private void showSelectedGame(Game selectedGame) {
		if (selectedGame != null) {
			this.editedGame = selectedGame;
			this.gameNameField.setText(this.editedGame.getName());
			this.thumbnailArtworkPathLabel.setText(this.editedGame.getThumbnailArtworkPath());
			this.backgroundArtworkPathLabel.setText(this.editedGame.getBackgroundArtworkPath());
//			this.availableTagsListView.setItems(this.tagsAccessor.getAllTagsExcept(this.editedTag.getAssignedTags()));
//			ObservableList<Tag> assignedTags = FXCollections.observableArrayList();
//			assignedTags.addAll(this.editedTag.getAssignedTags());
//			this.assignedTagsListView.setItems(assignedTags);
		}
	}
	
	@FXML
	private void saveAction() {
//		this.editedTag.setName(this.tagNameField.getText());
//		this.editedTag.setThumbnailArtworkPath(this.thumbnailArtworkPathLabel.getText());
//		this.editedTag.setBackgroundArtworkPath(this.backgroundArtworkPathLabel.getText());
//		List<Tag> assignedTags = this.assignedTagsListView.getItems();
////		this.editedTag.getAssignedTags().setAll(assignedTags);
//		this.allTagsListView.fireEvent(new ListView.EditEvent<>(this.allTagsListView, ListView.editCommitEvent(), this.editedTag, this.allTagsListView.getSelectionModel().getSelectedIndex()));
	}
	
	@FXML
	private void newAction() {
//		this.tagsAccessor.createNewTag();
//		this.allTagsListView.getSelectionModel().selectLast();
	}
	
	@FXML
	private void deleteAction() {
//		Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
//		confirmationAlert.setTitle("Delete " + this.editedTag.getName());
//		confirmationAlert.setHeaderText(null);
//		confirmationAlert.setContentText("Are you sure you want to delete this tag ?");
//		Optional<ButtonType> result = confirmationAlert.showAndWait();
//		if (result.get() == ButtonType.OK){
//		   this.doDeleteCurrentTag();
//		}
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
