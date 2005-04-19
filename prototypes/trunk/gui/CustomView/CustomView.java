import java.awt.*;
import javax.swing.text.*;


public class CustomView extends WrappedPlainView {

	public CustomView(Element elem) {
		super(elem);
	}

	public void paint(Graphics g, Shape a) {
		super.paint(g, a);
		
		try {
			Shape shape = modelToView(2, a, Position.Bias.Forward);
			Rectangle r = shape.getBounds();
			
			g.setColor(Color.blue);
			g.drawRect(r.x, r.y, 5, 5);
			g.setColor(Color.red);
			g.fillRect(r.x+1, r.y+1, 4, 4);
			
			
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
		
		
	}

}
