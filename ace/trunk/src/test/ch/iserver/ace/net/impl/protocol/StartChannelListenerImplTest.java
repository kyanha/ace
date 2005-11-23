package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.StartChannelListener;
import org.easymock.AbstractMatcher;
import org.easymock.MockControl;

public class StartChannelListenerImplTest extends TestCase {
	
	public void testAdvertiseProfile() throws Exception {
		MockControl handlerCtrl = MockControl.createControl(RequestHandler.class);
		RequestHandler handler = (RequestHandler)handlerCtrl.getMock();
		StartChannelListener instance = new StartChannelListenerImpl(handler, null);
		
		handlerCtrl.replay();
		
		assertTrue(instance.advertiseProfile(null));
		
		handlerCtrl.verify();
	}
	
	public void testStartChannel() throws Exception {
		MockControl handlerCtrl = MockControl.createControl(RequestHandler.class);
		RequestHandler handler = (RequestHandler)handlerCtrl.getMock();
		handlerCtrl.replay();
		StartChannelListener instance = new StartChannelListenerImpl(handler, null);
		
		MockControl channelCtrl = MockControl.createControl(Channel.class);
		Channel channel = (Channel)channelCtrl.getMock();
		
		channel.setRequestHandler(handler);
		channelCtrl.setDefaultReturnValue(null);
		channelCtrl.setDefaultMatcher(new RequestHandlerMatcher());
		channelCtrl.replay();
		
		instance.startChannel(channel, "", "");
		
		channelCtrl.verify();
		handlerCtrl.verify();
	}
	
}

class RequestHandlerMatcher extends AbstractMatcher {
	
	protected boolean argumentMatches(Object arg0, Object arg1) {
		return arg0.equals(arg1);
	}
	
}
