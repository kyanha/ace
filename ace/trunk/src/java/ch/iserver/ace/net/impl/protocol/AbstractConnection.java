package ch.iserver.ace.net.impl.protocol;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

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
			if (getState() == STATE_ACTIVE) {
				OutputDataStream output = prepare(message);
				//AppData is kept in-process only
				if (data != null)
					channel.setAppData(data);
				
				LOG.debug("--> sendMSG() with "+message.length+" bytes");
				channel.sendMSG(output, listener);
				LOG.debug("<-- sendMSG()");
			} else {
				LOG.error("cannot send data, channel not in STATE_ACTIVE but in ["+getStateString()+"]");
			}
		} catch (Exception e) {
			String trace = getStackTrace(e);
			LOG.debug("caught exception [" + e + ", " + trace + "]");
			throw new ProtocolException(e.getMessage());
		}
	}
	
	private String getStackTrace(Exception e) {
		ByteArrayOutputStream trace = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(trace);
		e.printStackTrace(pw);
		pw.close();
		return new String(trace.toByteArray());
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
	
	public int getState() {
		return state;
	}
	
	public synchronized void setState(int newState) {
		this.state = newState;
	}
	
	public String getStateString() {
		switch(state) {
			case STATE_ABORTED:
				return "aborted";
			case STATE_ACTIVE:
				return "active";
			case STATE_CLOSED:
				return "closed";
			case STATE_DIRTY:
				return "dirty";
			case STATE_INITIALIZED:
				return "initialized";
			default:
				return "unkown";
		}
	}
	
	/**
	 * Cleans up this connection and frees any resources.
	 *
	 */
	public abstract void cleanup();
}
