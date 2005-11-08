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

import java.nio.ByteBuffer;

import org.apache.asn1.ber.BEREncoder;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.StatefulEncoder;

import ch.iserver.ace.net.impl.NetworkConstants;

/**
 *
 */
public class TLVQueryCreator implements QueryCreator, EncoderCallback {

	private ByteBuffer buffer;
	
	public TLVQueryCreator() {
		buffer = ByteBuffer.wrap( new byte[64] );
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.protocol.QueryCreator#createQuery()
	 */
	public byte[] createQuery(String query) throws Exception {
		BEREncoder encoder = new BEREncoder();
		encoder.setCallback(this);
		//T
		Tuple tlv = new Tuple();
		tlv.setTag(UniversalTag.UTF8_STRING);
		encoder.tag(tlv);
		
		//L
		tlv.setLength(query.length());
		encoder.length(tlv);
		
		//V
        byte[] value = query.getBytes(NetworkConstants.DEFAULT_ENCODING);
        ByteBuffer chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
		
        buffer.flip();
        byte[] encodedBytes = new byte[buffer.remaining()];
        buffer.get( encodedBytes );
        
		System.out.println("encoded "+encodedBytes.length+" bytes.");
		return encodedBytes;
	}

	public void encodeOccurred(StatefulEncoder encoder, Object encoded) {
		ByteBuffer buf = (ByteBuffer)encoded;
		buffer.put(buf);
	}
	
	public static void main(String args[]) throws Exception {
		TLVQueryCreator c = new TLVQueryCreator();
		byte[] buf = c.createQuery("queryDocs");
		String s = new String(buf, NetworkConstants.DEFAULT_ENCODING);
		System.out.println("result: "+buf[0]+" "+buf[1]+" "+buf[2]+" "+buf[3]+" "+buf[4]+" ...");
	}

}
