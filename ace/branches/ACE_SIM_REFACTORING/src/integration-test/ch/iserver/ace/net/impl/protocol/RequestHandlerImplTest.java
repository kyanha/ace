package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.beepcore.beep.core.RequestHandler;
import org.easymock.MockControl;

public class RequestHandlerImplTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.RequestHandlerImpl.receiveMSG(MessageMSG)'
	 */
	public void testReceiveMSG() {
		MockControl filterCtrl = MockControl.createControl(AbstractRequestFilter.class);
		AbstractRequestFilter filter = (AbstractRequestFilter)filterCtrl.getMock();
		
		RequestHandler handler = new MainRequestHandler(DeserializerImpl.getInstance(), filter);

		//fail("not yet implemented");
	}

}
