package controllers.editor;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class TagsViewController implements Initializable {
	
	@FXML
	private TextField tagNameField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.tagNameField.setEditable(false);
	}

}
