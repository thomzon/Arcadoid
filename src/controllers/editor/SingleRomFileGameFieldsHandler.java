package controllers.editor;

import java.io.File;

import data.model.Game;
import data.settings.Messages;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public abstract class SingleRomFileGameFieldsHandler extends PlatformSpecificGameFieldsHandler {

	private Label titleLabel = new Label();
	private Label fileNameLabel = new Label();
	private Button selectFileButton = new Button();
	private Button clearButton = new Button();
	
	@Override
	void teardownForGridPane(GridPane gridPane) {
		super.teardownForGridPane(gridPane);
		gridPane.getChildren().removeAll(this.titleLabel, this.fileNameLabel, this.selectFileButton, this.clearButton);
	}

	@Override
	void setupInGridPane(GridPane gridPane) {
		gridPane.add(this.titleLabel, 0, 4);
		gridPane.add(this.fileNameLabel, 1, 4, 1, 1);
		gridPane.add(this.selectFileButton, 2, 4, 1, 1);
		gridPane.add(this.clearButton, 3, 4, 1, 1);
		GridPane.setHalignment(this.fileNameLabel, HPos.RIGHT);
		GridPane.setHalignment(this.selectFileButton, HPos.CENTER);
	
		this.titleLabel.setText(Messages.get("field.romFileName"));
		this.selectFileButton.setText("...");
		this.selectFileButton.setOnAction((event) -> {
			selectRomFile();
		});
		this.clearButton.setText(Messages.get("common.clear"));
		this.clearButton.setPrefWidth(60);
		this.clearButton.setOnAction((event) -> {
			clearRomFile();
		});
	}
	
	@Override
	void setEditedGame(Game editedGame) {
		super.setEditedGame(editedGame);
		if (this.getRomFileName() != null) {
			this.fileNameLabel.setText(this.getRomFileName());
		}
	}
	
	private void selectRomFile() {
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(Messages.get("field.romFileName"));
    	fileChooser.setInitialDirectory(new File(this.getRomsFolderPath()));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(this.getRomFileDescription(), this.getExtensionFilter());
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this.titleLabel.getScene().getWindow());
        if (file != null) {
        	this.fileNameLabel.setText(file.getName());
        	this.setNewRomFileName(this.fileNameLabel.getText());
        	this.fieldModified();
        }
	}
	
	private void clearRomFile() {
		this.fileNameLabel.setText("");
		this.clearRomFileName();
		this.fieldModified();
	}
	
	abstract protected String getRomsFolderPath();
	abstract protected String getRomFileDescription();
	abstract protected String getExtensionFilter();
	abstract protected String getRomFileName();
	abstract protected void setNewRomFileName(String romFileName);
	abstract protected void clearRomFileName();

}
