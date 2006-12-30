# script used so that reading from standard input is possible
#
# parameters to pass:
#	$1	[ discover | register ]
#
# 20.09.2006 / zbindenl

cd build/classes/peer

java -Djava.security.policy=reggie.policy -Djava.rmi.server.codebase=http://192.168.1.100:5001/ -Dport=5001 -cp jini-core.jar:jini-ext.jar:sun-util.jar:start.jar:. ch.iserver.ace.net.jdiscovery.PeerApp start-transient-reggie.config DiscoveryListener.config $1
