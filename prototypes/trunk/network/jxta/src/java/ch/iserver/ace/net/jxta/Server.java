package ch.iserver.ace.net.jxta;

import java.io.FileInputStream;
import java.io.StringWriter;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

public class Server {
	static PeerGroup group = null;

	static PeerGroupAdvertisement groupAdvertisement = null;

	private DiscoveryService discovery;

	private PipeService pipes;

	private InputPipe myPipe; // input pipe for the service

	private Message msg; // pipe message received

	private ID gid; // group id

	public static void main(String args[]) {
		Server myapp = new Server();
		System.out.println("Starting Service Peer ....");
		myapp.startJxta();
		System.out.println("Good Bye ....");
		System.exit(0);
	}

	private void startJxta() {
		try {
			// create, and Start the default jxta NetPeerGroup
			group = PeerGroupFactory.newNetPeerGroup();
		} catch (PeerGroupException e) {
			// could not instantiate the group, print the stack and exit
			System.out.println("fatal error : group creation failure");
			e.printStackTrace();
			System.exit(1);
		}
		// this is how to obtain the group advertisement
		groupAdvertisement = group.getPeerGroupAdvertisement();
		// get the discovery, and pipe service
		System.out.println("Getting DiscoveryService");
		discovery = group.getDiscoveryService();
		System.out.println("Getting PipeService");
		pipes = group.getPipeService();
		startServer();
	}

	private void startServer() {
		System.out.println("Start the Server daemon");
		// get the peergroup service we need
		gid = group.getPeerGroupID();
		try {
			// First create the Service Module class advertisement
			// build the module class advertisement using the
			// AdvertisementFactory by passing the Advertisement type
			// we want to construct. The Module class advertisement
			// is to be used to simply advertise the existence of
			// the service. This is a very small advertisement
			// that only advertise the existence of service
			// In order to access the service, a peer must
			// discover the associated module spec advertisement.
			ModuleClassAdvertisement mcadv = (ModuleClassAdvertisement) AdvertisementFactory
					.newAdvertisement(ModuleClassAdvertisement
							.getAdvertisementType());
			mcadv.setName("JXTAMOD:JXTA-EX1");
			mcadv.setDescription("Tutorial example to use JXTA module advertisement Framework");
			ModuleClassID mcID = IDFactory.newModuleClassID();
			mcadv.setModuleClassID(mcID);
			// Once the Module Class advertisement is created, publish
			// it in the local cache and within the peergroup.
			discovery.publish(mcadv);
			discovery.remotePublish(mcadv);
			// Create the Service Module Spec advertisement
			// build the module Spec Advertisement using the
			// AdvertisementFactory class by passing in the
			// advertisement type we want to construct.
			// The Module Spec advertisement contains
			// all the information necessary for a client to reach
			// the service
			// i.e. it contains a pipe advertisement in order
			// to reach the service
			ModuleSpecAdvertisement mdadv = (ModuleSpecAdvertisement) AdvertisementFactory
					.newAdvertisement(ModuleSpecAdvertisement
							.getAdvertisementType());
			// Setup some information about the servive. In this
			// example, we just set the name, provider and version
			// and a pipe advertisement. The module creates an input
			// pipes to listen on this pipe endpoint
			mdadv.setName("JXTASPEC:JXTA-EX1");
			mdadv.setVersion("Version 1.0");
			mdadv.setCreator("sun.com");
			mdadv.setModuleSpecID(IDFactory.newModuleSpecID(mcID));
			mdadv.setSpecURI("http://www.jxta.org/Ex1");
			// Create the service pipe advertisement.
			// The client MUST use the same pipe advertisement to
			// communicate with the server. When the client
			// discovers the module advertisement it extracts
			// the pipe advertisement to create its pipe.
			// So, we are reading the advertisement from a default
			// config file to ensure that the
			// service will always advertise the same pipe
			// 
			System.out.println("Reading in pipeserver.adv");
			PipeAdvertisement pipeadv = null;
			try {
				FileInputStream is = new FileInputStream("pipeserver.adv");
				pipeadv = (PipeAdvertisement) AdvertisementFactory
						.newAdvertisement(MimeMediaType.XMLUTF8, is);
				is.close();
			} catch (Exception e) {
				System.out.println("failed to read/parse pipe advertisement");
				e.printStackTrace();
				System.exit(-1);
			}
			// Store the pipe advertisement in the spec adv.
			// This information will be retrieved by the client when it
			// connects to the service
			mdadv.setPipeAdvertisement(pipeadv);
			// display the advertisement as a plain text document.
			StructuredTextDocument doc = (StructuredTextDocument) mdadv
					.getDocument(MimeMediaType.XMLUTF8);
			StringWriter out = new StringWriter();
			doc.sendToWriter(out);
			System.out.println(out.toString());
			out.close();
			// Ok the Module advertisement was created, just publish
			// it in my local cache and into the NetPeerGroup.
			discovery.publish(mdadv);
			discovery.remotePublish(mdadv);
			// we are now ready to start the service
			// create the input pipe endpoint clients will
			// use to connect to the service
			myPipe = pipes.createInputPipe(pipeadv);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Server: Error publishing the module");
		}
		// Ok no way to stop this daemon, but that's beyond the point
		// of the example!
		while (true) { // loop over every input received from clients
			System.out.println("Waiting for client messages to arrive");
			try {
				// Listen on the pipe for a client message
				msg = myPipe.waitForMessage();
			} catch (Exception e) {
				myPipe.close();
				System.out.println("Server: Error listening for message");
				return;
			}
			// Read the message as a String
			String ip = null;
			try {
				// NOTE: The Client and Service must agree on the tag
				// names. This is part of the Service protocol defined
				// to access the service.
				// get all the message elements
				Message.ElementIterator en = msg.getMessageElements();
				if (!en.hasNext()) {
					return;
				}
				// get the message element named SenderMessage
				MessageElement msgElement = msg.getMessageElement(null,
						"DataTag");
				// Get message
				if (msgElement.toString() != null) {
					ip = msgElement.toString();
				}
				if (ip != null) {
					// read the data
					System.out.println("Server: receive message: " + ip);
				} else {
					System.out.println("Server: error could not find the tag");
				}
			} catch (Exception e) {
				System.out.println("Server: error receiving message");
			}
		}
	}
}
