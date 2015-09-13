package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ArcadoidEditor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class TabsController implements Initializable {

	@FXML
	private Tab tagsTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTagsTab();
	}
	
	private void setupTagsTab() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/TagsViewController.fxml"));
            AnchorPane tagsPane = (AnchorPane) loader.load();
            this.tagsTab.setContent(tagsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
}