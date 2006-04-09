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
package ch.iserver.ace.net.bonjour.txt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSRecord;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.QueryListener;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

public class SimpleTest implements RegisterListener, ResolveListener, BrowseListener, QueryListener {
	
	static final String RegType = "_test._tcp";

	static final String kWireCharSet = "ISO-8859-1";
	
	DNSSDRegistration registration;
	DNSSDService browser;
	DNSSDService resolver;
	DNSSDService query;
//	TXTRecord txtRec;
	
	Map d2T = new HashMap();
	String regType;
	String userName;
	List myTxtRecs = new ArrayList();
	int ctr = 0, ctr2;
	
	public SimpleTest() throws Exception {
		userName = System.getProperty("user.name");
		DatagramSocket ds = new DatagramSocket();
		
		TXTRecord txt = new TXTRecord();
//		txt.set("doc", "test.txt");
		registration = DNSSD.register(0, 0, userName, RegType, "",
				"", ds.getLocalPort(), null, this);
		
	}

	public void serviceRegistered(DNSSDRegistration registration, int flags,
			String serviceName, String regType, String domain) {
//		System.out.println("serviceRegistered: "+serviceName+" "+regType+" "+domain);
		userName = serviceName;
		this.regType = regType;
		
		try {
			resolver = DNSSD.resolve(0, 0, serviceName,
					regType, domain, this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void operationFailed(DNSSDService arg0, int arg1) {
		System.err.println("opFailed: "+arg0+" "+arg1);
		
	}
	
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
//		System.out.println("serviceResolved: "+flags+" "+ifIndex+" "+fullName+" "+hostName+" "+port+" "+txtRecord);
//		txtRec = txtRecord;
		
		try {
			DNSRecord r = registration.getTXTRecord();
			System.out.println(r+" "+txtRecord);
			myTxtRecs.add(r);
			d2T.put(r, txtRecord);
			browser = DNSSD.browse(0, 0, regType, "", this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		resolver.stop();
	}

	public void serviceFound(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		if (!userName.equals(serviceName)) {
			try {
//				System.out.println("query TXT record for "+serviceName+"."+regType+domain);
				query = DNSSD.queryRecord(flags, ifIndex, serviceName+"."+regType+domain, 16 /* 16=txt record, 1=ns_t_a */,
					1 /* ns_c_in */, this);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		System.out.println("serviceLost: "+serviceName);
	}

	public void queryAnswered(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
//		System.out.println("queryAnswered: "+flags+" "+ifIndex+" "+fullName+" "+rrtype+" "+rrclass+" "+rdata.length+" "+ttl);
		TXTRecord txt = new TXTRecord(rdata);
		System.out.println("TXT: "+txt+" from "+fullName+" "+flags+" "+ifIndex+" "+ttl);
	}
	
	public void addTxtRecord(String entry) throws Exception {
		TXTRecord txt = new TXTRecord();
		++ctr;
		txt.set("doc"+ctr, entry);
		DNSRecord r = registration.addRecord(0, 16, txt.getRawBytes(), 0);
		System.out.println("new txtr: "+r);
		d2T.put(r, txt);
		myTxtRecs.add(r); //TODO: how to set TTL?
		System.out.println("total "+myTxtRecs.size()+" txts");
	}
	
	public void removeTxtRecord(int id) throws Exception {
		DNSRecord r = (DNSRecord)myTxtRecs.remove(id);
		if (r != null) {
			System.out.println("txtr to del: "+r);
			r.remove();
			System.out.println("removed txt, "+myTxtRecs.size()+" txts remain.");
		}
	}
	
	public void addEntryToRecord(int id, String entry) throws Exception  {
		DNSRecord r = (DNSRecord)myTxtRecs.get(id);
		if (r != null) {
			TXTRecord txt = (TXTRecord)d2T.get(r);
			if (txt != null) {
				++ctr2;
				txt.set(userName+ctr2, entry);
				System.out.println("updated TXT: "+txt);
				r.update(0, txt.getRawBytes(), 0);
			} else
				System.err.println("TXT record not found.");
		}
	}
	
	public void removeEntryFromRecord(int id, String key) throws Exception {
		DNSRecord r = (DNSRecord)myTxtRecs.get(id);
		if (r != null) {
			TXTRecord txt = (TXTRecord)d2T.get(r);
			if (txt != null) {
				txt.remove(key);
				r.update(0, txt.getRawBytes(), 0);
				System.out.println("TXT entry removed.");
			} else
				System.err.println("TXT record not found.");
		}
	}
	
	public void unregister() {
		registration.stop();
		resolver.stop();
		browser.stop();
		query.stop();
	}



	
	public static void main(String args[]) throws Exception {
		SimpleTest st = new SimpleTest();
		
		boolean flag = true;
		while (flag) {
			
			String file = getUserInput("[ add | remove | tadd | trem | stop ] > ");
			if (file.indexOf("add") == 0) {
				file = getUserInput("entry > ");
				st.addTxtRecord(file);
			} else if (file.indexOf("stop") != -1)
				flag = false;
			else if (file.indexOf("remove") == 0) {
				file = getUserInput("id > ");
				int id = Integer.valueOf(file).intValue();
				st.removeTxtRecord(id);
			} else if (file.indexOf("tadd") != -1) { //tadd 1 test.txt
				String s = getUserInput("id > ");
				String entry = getUserInput("entry > ");
				int id = Integer.valueOf(s).intValue();
				st.addEntryToRecord(id, entry);
			} else if (file.indexOf("trem") != -1) {
				String s = getUserInput("id > ");
				String key = getUserInput("key > ");
				int id = Integer.valueOf(s).intValue();
				st.removeEntryFromRecord(id, key);
			}
				
				
		}
		
		System.out.println("unregister.");
		st.unregister();
		System.exit(0);
	}
	
	public static String getUserInput( String prompt )
	  {
	    String input = "";
	    try {
	      BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
	      System.out.print( prompt );
	      input = br.readLine();
	    }
	    catch ( Exception e )
	    {
	      System.err.println(e);
	    }
	    return input;
	  }
	
	
	
}
