/*
 * $Id:DataStreamHelper.java 2413 2005-12-09 13:20:12Z zbinl $
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

package ch.iserver.ace.net.protocol;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.util.BufferSegment;

/**
 * DataStreamHelper provides methods which prepare data for the transmission
 * with a BEEP Core <code>Channel</code> or read data from an
 * <code>InputDataStream</code> from the BEEP API.
 *
 * @see org.beepcore.beep.core.Channel
 * @see org.beepcore.beep.core.InputDataStream
 */
public class DataStreamHelper {

	private static Logger LOG = Logger.getLogger(DataStreamHelper.class);
	
	/**
	 * Creates an <code>OutputDataStream</code> from the given data.
	 * 
	 * @param data 	the data to fill in the OutputDataStream 
	 * @return the OutputDataStream containing the <code>data</code>
	 */
	public static OutputDataStream prepare(byte[] data) {
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		return output;
	}
	
	/**
	 * Reads data from an <code>InputDataStream</code>.
	 * 
	 * @param stream		the InputDataStream stream to read data from
	 * @return	the data read as a byte array
	 * @throws Exception	if an error occurs
	 */
	public static byte[] read(InputDataStream stream) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
            	BufferSegment b = stream.waitForNextSegment();
             if (b == null) {
            	 	if (stream.isComplete()) {
            	 		LOG.warn("BufferSegment null but stream is NOT complete, thus continue...");
            	 		break;
            	 	} else {
            	 		continue;
            	 	}
             }
             out.write(b.getData());
		} while (stream.availableSegment());
		out.flush();
		return out.toByteArray();
	}
	
}
