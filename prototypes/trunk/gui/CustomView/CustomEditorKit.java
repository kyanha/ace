import javax.swing.text.*;


public class CustomEditorKit extends StyledEditorKit implements ViewFactory {

	public ViewFactory getViewFactory() {
		return this;
	}
	
	public View create(Element elem) {
		return new CustomView(elem);
	}

}
