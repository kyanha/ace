package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.ReplyListener;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.util.ParameterValidator;

public class AbstractConnection {

	protected Logger LOG = Logger.getLogger(AbstractConnection.class);
	
	private Channel channel;
	private ReplyListener listener;
	
	public AbstractConnection(Channel channel) {
		this.channel = channel;
	}
	
	public synchronized void send(byte[] message, Object data, ReplyListener listener) throws ProtocolException {
		ParameterValidator.notNull("channel", channel);
		try {
			OutputDataStream output = prepare(message);
			//AppData is kept only in-process
			if (data != null)
				channel.setAppData(data);
			LOG.debug("--> sendMSG() with "+message.length+" bytes");
			channel.sendMSG(output, listener);
			LOG.debug("<-- sendMSG()");
		} catch (Exception e) {
			throw new ProtocolException(e.getMessage());
		}
	}
	
	private OutputDataStream prepare(byte[] data) {
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		return output;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public void setReplyListener(ReplyListener listener) {
		this.listener = listener;
	}
	
	public ReplyListener getReplyListener() {
		return listener;
	}
}
