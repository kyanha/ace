package ch.iserver.ace.net.beep.profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import junit.framework.TestCase;

import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.Message;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.lib.Reply;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.net.impl.NetworkConstants;

public class TestACEProfileClient extends TestCase {

	private TestACEProfileServer server;
	private QueryCreator creator;
	
	public void setUp() {
		server = new TestACEProfileServer();
		server.start();
		creator = new TLVQueryCreator();
	}
	
	public void tearDown() {
		server.terminate();
		server = null;
	}
	
	public void testACEProfile() throws Exception {
		/** create client-side **/
		
		TCPSession session = TCPSessionCreator.initiate(InetAddress.getLocalHost(), TestACEProfileServer.LISTENING_PORT);
		Channel channel = session.startChannel(ACEProfile.ACE_URI);
		
		Reply reply = new Reply();
		
		//create data structure containing the published documents 
		byte[] data = createQuery();
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		
		channel.sendMSG(output, reply);
		Message response = reply.getNextReply();
		int msgType = response.getMessageType();
		assertEquals(msgType, Message.MESSAGE_TYPE_RPY);
		
		InputDataStream input = response.getDataStream();
		String str = process(input);
		System.out.println("C: answer: "+str);
		assertEquals(str, DefaultParser.DEFAULT_ANSWER);
		
		channel.close();
		session.close();
	}
	
	private byte[] createQuery() throws Exception {
		return creator.createQuery("queryDocs");
	}
	
	private String process(InputDataStream input) {
		if (creator instanceof TLVQueryCreator) {
			TLVQueryProcessor processor = new TLVQueryProcessor();
			try {
				InputStream in = getInputStream(input);
				processor.dump(in, null);
				return processor.getPayload();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			do {
				try {
					System.out.println("C: waitForNextSegment");
					BufferSegment b = input.waitForNextSegment();
					System.out.println("C: ok "+b.getLength());
					if (b == null) {
						out.flush();
						break;
					}
					out.write(b.getData());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} while (!input.isComplete());
			
			byte[] result = out.toByteArray();
			String str = null;
			try {
				str = new String(result, NetworkConstants.DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException uee) {}
			return str;
		}
		return null;
	}
	
	private InputStream getInputStream(InputDataStream xmlInput) {
		//parse xml
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
            try {
            		BufferSegment b = xmlInput.waitForNextSegment();
                if (b == null) {
                		out.flush();
                    break;
                }
                out.write(b.getData());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } while (!xmlInput.isComplete());
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}
	
}
