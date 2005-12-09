package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.util.BufferSegment;
import org.easymock.AbstractMatcher;
import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkProperties;

public class ParticipantConnectionImplTest extends TestCase {

	private static final String DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ace><request><query type=\"doc\"/></request></ace>";
	
	public void testSend() throws Exception {
		MockControl channelCtrl = MockControl.createControl(Channel.class);
		Channel channel = (Channel)channelCtrl.getMock();
		
		MainConnection connection = new MainConnection(channel);
		byte[] data = DATA.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		
		QueryInfo queryInfo = new QueryInfo("test-id", 0);
		channel.setAppData(queryInfo);
		channelCtrl.setMatcher(new QueryInfoMatcher());
		
		ResponseListener listener = ResponseListener.getInstance();
		listener.init(DeserializerImpl.getInstance(), new FailureFilter(null));
		
		channel.sendMSG(prepare(data), listener);
		channelCtrl.setDefaultReturnValue(null);
		channelCtrl.setMatcher(new SendMSGMatcher());
		
		channelCtrl.replay();
		
		connection.send(data, queryInfo, listener);
		
		channelCtrl.verify();
	}
	
	private OutputDataStream prepare(byte[] data) {
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		return output;
	}
	
	
}

class QueryInfoMatcher implements ArgumentsMatcher {

	public boolean matches(Object[] arg0, Object[] arg1) {
		QueryInfo info1 = (QueryInfo)arg0[0];
		QueryInfo info2 = (QueryInfo)arg1[0];
		return info1.getId().equals(info2.getId()) && info1.getQueryType() == info2.getQueryType();
	}

	public String toString(Object[] arg0) {
		QueryInfo info1 = (QueryInfo)arg0[0];
		String str = "QueryInfo("+info1.getId()+", "+info1.getQueryType()+")";
		return str;
	}
}

class SendMSGMatcher extends AbstractMatcher {
	
	public boolean argumentMatches(Object expected, Object actual) {
		if (actual instanceof OutputDataStream) {
			OutputDataStream out1 = (OutputDataStream)expected;
			OutputDataStream out2 = (OutputDataStream)actual;
			return out1.equals(out2);
		} else {  //QueryListener
			ResponseListener l1 = (ResponseListener)expected;
			ResponseListener l2 = (ResponseListener)actual;
			return l1.equals(l2);
		}
	}
}