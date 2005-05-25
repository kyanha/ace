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
package ch.iserver.ace.net.jgroups;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.TimeoutException;
import org.jgroups.View;

/**
 * 
 */
public class JGroupsTest {

	public JGroupsTest(JChannel channel, String group) throws ChannelException {
		channel.connect(group);
		System.out.println("... connected to " + group);
		System.out.println("... local address is " + channel.getLocalAddress());
		System.out.println("... waiting for messages");

		try {
			Object obj = channel.receive(0);
			if (obj instanceof Message) {
				Message msg = (Message) obj;
				System.out.println("... got message: " + msg);
			} else if (obj instanceof View) {
				View view = (View) obj;
				System.out.println("... got view: " + view);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String props = "UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=32):"
				+ "PING(timeout=3000;num_initial_members=6):"
				+ "FD(timeout=5000):" 
				+ "VERIFY_SUSPECT(timeout=1500):"
				+ "pbcast.NAKACK(gc_lag=10;retransmit_timeout=3000):"
				+ "pbcast.STABLE(desired_avg_gossip=10000):"
				+ "UNICAST(timeout=5000):" 
				+ "FRAG:"
				+ "pbcast.GMS(join_timeout=5000;shun=false;print_local_addr=false)";
		JChannel channel;
		try {
			channel = new JChannel(props);
			// channel = new JChannel();
			System.out.println("... created channel");
			new JGroupsTest(channel, args[0]);
		} catch (ChannelException e) {
			e.printStackTrace();
		}
	}

}
