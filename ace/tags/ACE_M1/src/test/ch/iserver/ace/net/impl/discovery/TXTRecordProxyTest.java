package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import junit.framework.TestCase;

import com.apple.dnssd.TXTRecord;

public class TXTRecordProxyTest extends TestCase {

	private TXTRecord txt;
	private Properties props;
	
	
	
	public void setUp() {
		props = new Properties();
		props.put(Bonjour.KEY_TXT_VERSION, "1");
		props.put(Bonjour.KEY_USER, "testuser");
		props.put(Bonjour.KEY_USERID, "testuser-id");
		props.put(Bonjour.KEY_PROTOCOL_VERSION, "0.1");
		txt = TXTRecordProxy.create(props);
	}
	
	public void testCreate() {
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_VERSION, txt), props.get(Bonjour.KEY_TXT_VERSION));
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USER, txt), props.get(Bonjour.KEY_USER));
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USERID, txt), props.get(Bonjour.KEY_USERID));
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_PROTOCOL_VERSION, txt), props.get(Bonjour.KEY_PROTOCOL_VERSION));
	}
	
	public void testSet() {
		TXTRecordProxy.set(TXTRecordProxy.TXT_USER, "new-testuser",txt);
		assertEquals(TXTRecordProxy.get(TXTRecordProxy.TXT_USER, txt), "new-testuser");
	}
}
