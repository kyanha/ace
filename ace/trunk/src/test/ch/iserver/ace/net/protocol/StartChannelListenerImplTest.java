package ch.iserver.ace.net.protocol;

import junit.framework.TestCase;

import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.StartChannelListener;
import org.easymock.AbstractMatcher;
import org.easymock.MockControl;

public class StartChannelListenerImplTest extends TestCase {
	
	public void testAdvertiseProfile() throws Exception {
		StartChannelListener instance = new StartChannelListenerImpl(getFactory());
		
		assertTrue(instance.advertiseProfile(null));
	}
	
	public void testStartChannel() throws Exception {
		StartChannelListener instance = new StartChannelListenerImpl(getFactory());
		
		MockControl channelCtrl = MockControl.createControl(Channel.class);
		Channel channel = (Channel)channelCtrl.getMock();
		
		channel.setRequestHandler(null);
		channelCtrl.setDefaultReturnValue(null);
		channelCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		channelCtrl.replay();
		
		instance.startChannel(channel, "", RemoteUserSession.CHANNEL_MAIN);
		
		channelCtrl.verify();
	}
	
	public DefaultRequestHandlerFactory getFactory() {
		MockControl handlerCtrl = MockControl.createControl(RequestHandler.class);
		RequestHandler handler = (RequestHandler)handlerCtrl.getMock();
		handlerCtrl.replay();
		MockControl deserializerCtrl = MockControl.createControl(Deserializer.class);
		Deserializer deserializer = (Deserializer)deserializerCtrl.getMock();
		deserializerCtrl.replay();
		//only for testing
		ResponseParserHandler parserHandler = new ResponseParserHandler();
		DefaultRequestHandlerFactory.init(handler, deserializer, parserHandler);
		return DefaultRequestHandlerFactory.getInstance();
	}
	
	
}

class RequestHandlerMatcher extends AbstractMatcher {
	
	protected boolean argumentMatches(Object arg0, Object arg1) {
		return arg0.equals(arg1);
	}
	
}
