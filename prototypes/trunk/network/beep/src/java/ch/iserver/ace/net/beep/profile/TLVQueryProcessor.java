/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package ch.iserver.ace.net.beep.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.asn1.ber.BERDecoder;
import org.apache.asn1.ber.BERDecoderCallback;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;
import org.apache.asn1.codec.stateful.StatefulDecoder;

/**
 *
 */
public class TLVQueryProcessor implements QueryProcessor, BERDecoderCallback, DecoderMonitor {

	private String payload;
	
	/**
	 * @see ch.iserver.ace.net.protocol.QueryProcessor#process(java.io.InputStream)
	 */
	public byte[] process(InputStream input) throws Exception {
		processImpl(input);
		
		return createResponse();
	}
	
	public void dump(InputStream input, OutputStream out) throws Exception {
		System.out.println("--> dump");
		processImpl(input);
		System.out.println("<-- dump");
	}
	
	public String getPayload() {
		return payload;
	}

	/**
	 * @param input
	 * @throws IOException
	 * @throws DecoderException
	 */
	private void processImpl(InputStream input) throws IOException, DecoderException {
		int size = input.available();
		System.out.println("C: read "+size+"  bytes.");
		byte[] encoded = new byte[size];  
		input.read(encoded);
		
		BERDecoder decoder = new BERDecoder();
		decoder.setCallback( this ) ;
		decoder.setDecoderMonitor( this ) ;
		decoder.decode(ByteBuffer.wrap(encoded));
	}
	


	public void tagDecoded(Tuple arg0) {
		
		System.out.println("Tag decoded: "+arg0.getId());
	}

	public void lengthDecoded(Tuple arg0) {
		System.out.println("Length decoded: "+arg0.getLength());
		
	}

	public void partialValueDecoded(Tuple arg0) {
		System.out.println("Value decoded: "+arg0.getLastValueChunk().array().length);
		
	}

	public void decodeOccurred(StatefulDecoder decoder, Object decoded) {
		Tuple tlv = (Tuple)decoded;
		int tag = tlv.getId();
		int length = tlv.getLength();
		byte[] value = tlv.getLastValueChunk().array();
		System.out.println("TLV decoded: "+tag+" "+length+" "+value[2]+" "+value[3]+" ...");
		byte[] data = new byte[value.length-2];
		System.arraycopy(value, 2, data, 0, value.length-2);
		try {
			String str = new String(data, NetworkConstants.DEFAULT_ENCODING);
			System.out.println("TLV payload: "+str);
			payload = str;
		} catch (UnsupportedEncodingException uee) {}
		
	}

	public void error(StatefulDecoder arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}

	public void fatalError(StatefulDecoder arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}

	public void warning(StatefulDecoder arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}

	public void callbackOccured(StatefulDecoder arg0, DecoderCallback arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	public void callbackSet(StatefulDecoder arg0, DecoderCallback arg1, DecoderCallback arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public byte[] createResponse() throws Exception {
		TLVQueryCreator creator = new TLVQueryCreator();
		byte[] result = creator.createQuery(DefaultParser.DEFAULT_ANSWER);
		return result;
	}

}
