package controllers.editor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.model.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TagsViewController implements Initializable {
	
	@FXML private ListView<Tag> allTagsListView;
	@FXML private TextField tagNameField;	
	@FXML private Label thumbnailArtworkPathLabel, backgroundArtworkPathLabel;
	
	private final ArcadoidData dataAccessor = ArcadoidData.sharedInstance();
	private Tag editedTag;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initializeValueChangesListening();
		this.initializeTagsList();
	}
	
	private void initializeValueChangesListening() {
		NotificationCenter.sharedInstance().addObserver(ArcadoidData.DATA_LOADED_NOTIFICATION, this, "dataLoadedNotification");
		this.tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
		    this.saveAction();
		});
	}
	
	public void dataLoadedNotification() {
		this.initializeTagsList();
	}
	
	private void initializeTagsList() {
		this.allTagsListView.setItems(this.dataAccessor.getAllTags());
		this.allTagsListView.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> showSelectedTag(newValue));
		if (this.allTagsListView.getItems().size() > 0) {
			this.allTagsListView.getSelectionModel().select(0);
		} else {
			this.newAction();
		}
	}
	
	private void showSelectedTag(Tag selectedTag) {
		if (selectedTag != null) {
			this.editedTag = selectedTag;
			this.tagNameField.setText(this.editedTag.getName());
			this.thumbnailArtworkPathLabel.setText(this.editedTag.getThumbnailArtworkPath());
			this.backgroundArtworkPathLabel.setText(this.editedTag.getBackgroundArtworkPath());
		}
	}
	
	private void doDeleteCurrentTag() {
		this.dataAccessor.deleteTag(this.editedTag);
		if (this.dataAccessor.getAllTags().size() == 0) {
			this.newAction();
		}
	}
	
	@FXML private void saveAction() {
		this.editedTag.setName(this.tagNameField.getText());
		this.editedTag.setThumbnailArtworkPath(this.thumbnailArtworkPathLabel.getText());
		this.editedTag.setBackgroundArtworkPath(this.backgroundArtworkPathLabel.getText());
		this.allTagsListView.fireEvent(new ListView.EditEvent<>(this.allTagsListView, ListView.editCommitEvent(), this.editedTag, this.allTagsListView.getSelectionModel().getSelectedIndex()));
		NotificationCenter.sharedInstance().postNotification(ArcadoidData.TAG_MODIFIED_NOTIFICATION, this.editedTag);
	}
	
	@FXML private void newAction() {
		this.dataAccessor.createNewTag();
		this.allTagsListView.getSelectionModel().selectLast();
	}
	
	@FXML private void deleteAction() {
		this.doDeleteCurrentTag();
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
	
}
