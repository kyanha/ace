package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.ReplyListener;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.util.ParameterValidator;

public abstract class AbstractConnection {

	protected Logger LOG = Logger.getLogger(AbstractConnection.class);
	
	public static final int STATE_INITIALIZED = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_DIRTY = 2;
	public static final int STATE_ABORTED = 3;
	public static final int STATE_CLOSED = 4;
	
	private Channel channel;
	private ReplyListener listener;
	private int state;
	
	public AbstractConnection(Channel channel) {
		this.channel = channel;
	}
	
	public synchronized void send(byte[] message, Object data, ReplyListener listener) throws ProtocolException {
		ParameterValidator.notNull("channel", channel);
		
		try {
			OutputDataStream output = prepare(message);
			//AppData is kept in-process only
			if (data != null)
				channel.setAppData(data);

			if (isEstablished()) { //TODO: replace isEstablished by (getState() != STATE_ACTIVE) 
				LOG.debug("--> sendMSG() with "+message.length+" bytes");
//				LOG.debug(message+" "+output+" "+channel+" "+listener);
				channel.sendMSG(output, listener);
				LOG.debug("<-- sendMSG()");
			} else {
				LOG.warn("channel not established, cannot send data.");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public synchronized void setChannel(Channel channel) {
		this.channel = channel;
		//do not use setState() method here since we are already in a synchronized method!
//		state = (channel != null) ? STATE_ACTIVE: getState();
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
	
	public synchronized boolean isEstablished() {
		return (channel != null);
	}
	
	public int getState() {
		return state;
	}
	
	public synchronized void setState(int newState) {
		this.state = newState;
	}
	
	/**
	 * Cleans up this connection and frees any resources.
	 *
	 */
	public abstract void cleanup();
}
