package views.frontend;

import java.io.File;

import data.model.BaseItem;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CoverflowItem extends Parent {

	private static final double REFLECTION_SIZE = 0.25;
	public static final double WIDTH = 200;
	public static final double HEIGHT = WIDTH + (WIDTH*REFLECTION_SIZE);
	private static final double RADIUS_H = WIDTH / 2;
	private static final double BACK = WIDTH / 10;
	
	private PerspectiveTransform transform = new PerspectiveTransform();
	private Label itemNameLabel = new Label();
//	private BaseItem item;

	private final DoubleProperty angle = new SimpleDoubleProperty(45) {
        @Override protected void invalidated() {
            // When angle changes calculate new _transform
            update();
        }
    };
    
	public CoverflowItem() {
		try {
			File artworkPathFolder = new File(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH));
			File coverFile = new File(artworkPathFolder, "ubercade_cover.jpg");
			ImageView imageView = new ImageView(new Image(coverFile.toURI().toString(), false));
			Reflection reflection = new Reflection();
			reflection.setFraction(REFLECTION_SIZE);
			imageView.setEffect(reflection);
			setEffect(this.transform);
			getChildren().addAll(imageView);
			
			this.itemNameLabel.setTextFill(Color.WHITE);
			this.itemNameLabel.setTextAlignment(TextAlignment.CENTER);
			this.itemNameLabel.setAlignment(Pos.CENTER);
			this.itemNameLabel.setPrefWidth(200);
			this.itemNameLabel.setMinWidth(200);
			this.itemNameLabel.setMaxWidth(200);
			this.itemNameLabel.setLayoutY(150);
			getChildren().addAll(this.itemNameLabel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setBaseItem(BaseItem item) {
		this.itemNameLabel.setText(item.getName());
	}
	
	public final double getAngle() {
		return angle.getValue();
	}

	public final void setAngle(double value) {
		angle.setValue(value);
	}

	public final DoubleProperty angleModel() {
		return angle;
	}
	
	private void update() {
		 double lx = (RADIUS_H - Math.sin(Math.toRadians(angle.get())) * RADIUS_H - 1);
         double rx = (RADIUS_H + Math.sin(Math.toRadians(angle.get())) * RADIUS_H + 1);
         double uly = (-Math.cos(Math.toRadians(angle.get())) * BACK);
         double ury = -uly;
         transform.setUlx(lx);
         transform.setUly(uly);
         transform.setUrx(rx);
         transform.setUry(ury);
         transform.setLrx(rx);
         transform.setLry(HEIGHT + uly);
         transform.setLlx(lx);
         transform.setLly(HEIGHT + ury);
	}
	
}
