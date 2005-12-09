package ch.iserver.ace.net.impl.discovery;

import junit.framework.TestCase;
import ch.iserver.ace.net.core.NetworkProperties;

import com.apple.dnssd.TXTRecord;

public class TXTRecordProxyTest extends TestCase {

	private TXTRecord txt;
	private String username, userid;
	
	
	public void setUp() {
		username = "testuser";
		userid = "testuser-id";
		txt = TXTRecordProxy.create(username, userid);
	}
	
	public void testCreate() {
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_VERSION, txt), NetworkProperties.get(NetworkProperties.KEY_TXT_VERSION));
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USER, txt), username);
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USERID, txt), userid);
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_PROTOCOL_VERSION, txt), NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_VERSION));
	}
	
	public void testSet() {
		TXTRecordProxy.set(TXTRecordProxy.TXT_USER, "new-testuser",txt);
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USER, txt), "new-testuser");
	}
}
