package views.frontend;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import utils.frontend.UIUtils;

/**
 * Simple message popup, with or without close button
 * @author Thomas
 *
 */
public class InfoPopup extends FrontendPopup
{

	/**
	 * Information text
	 */
	private Text text;
	/**
	 * Close button
	 */
	private Button closeButton;
	
	/**
	 * Creates a message popup
	 * @param width Popup width
	 * @param height Popup height
	 * @param message Message to display
	 * @param withCloseButton If true, an "Ok" button allows user to close the popup
	 */
	public InfoPopup(double width, double height, String message, boolean withCloseButton)
	{
		super(width, height);
		this.text = new Text(message);
		text.setWrappingWidth(width - UIUtils.POPUP_TEXT_MARGIN);
		text.setTextAlignment(TextAlignment.CENTER);
		UIUtils.assignStyleClassToNodes("popup-text", this.text);
//		if (withCloseButton) {
//			this.closeButton = UiUtils.createButton("infoPopup.ok", true);
//			_closeButton.setOnAction(new EventHandler<ActionEvent>() {
//			    @Override public void handle(ActionEvent e) {
//			    	discard();
//			    }
//			});
//			getChildren().add(_closeButton);
//		}
//		getChildren().add(_text);
	}
	
//	@Override
//	public void doAppear()
//	{
//		super.doAppear();
//		_text.setLayoutX(getWidth()/2 - _text.getLayoutBounds().getWidth()/2);
//		_text.setLayoutY(_text.getLayoutBounds().getHeight() + UiUtils.POPUP_TEXT_MARGIN);
//		if (_closeButton == null) return;
//		_closeButton.setLayoutY(getHeight() - _closeButton.getHeight() - UiUtils.POPUP_TEXT_MARGIN);
//		_closeButton.setLayoutX(getWidth()/2 - _closeButton.getWidth()/2);
//	}
//	
//	/**
//	 * @return Y coordinate at which children can start layout out components, so it is below popup title
//	 */
//	protected double getStartYForChildren()
//	{
//		return _text.getLayoutBounds().getHeight() + UiUtils.POPUP_TEXT_MARGIN*2;
//	}

}
