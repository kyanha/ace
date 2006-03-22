<h2>Download for other OS (Linux, Solaris, FreeBSD)</h2>

<p><b>Step 3:</b> Download the following software (if not already installed):
<ul>
<li><a href="http://java.sun.com/j2se/1.5.0/download.jsp" target="_blank">Java Runtime Environment (JRE) - 1.4.2 or higher</a></li>
<li><a href="http://developer.apple.com/networking/bonjour/download" target="_blank">Bonjour Source Code</a></li>
<li><a href="http://ant.apache.org/" target="_blank">Apache Ant</a></li>
<li><a href="http://maven.apache.org/download.html" target="_blank">Maven Tasks for Ant</a></li>
</ul>
</p>

<p><b>Step 4:</b> Install the Java Runtime environment (JRE). Chances are high that if there is a JRE for your operating system, ACE will work too.
</p>

<p><b>Step 5:</b> Install Bonjour. Unfortunately, there is no installer on Linux, Solaris, FreeBSD. That means, Bonjour has to be built from source. In the following instructions, replace <code>os=linux</code>
with your operating system. Check the Makefile in <code>mDNSPosix/Makefile</code> for supported operating systems.</p>

<ol>
 <li>unpack the downloaded <code>mDNSResponder-<i>version</i>.tar.gz</code> to a location of your choice</li>
 <li>cd into subdirectory mDNSPosix</li>
 <li>in the Makefile, adjust the variable <em>JDK</em> to point to the correct JDK location on your machine</li>
 <li>type <code>$make os=[linux|solaris|...]</code> to build the mDNSResponder</li>
 <li>as user root, type <code>$make os=[linux|solaris|...] install</code> to install the mDNSResponder daemon</li>
 <li>now, the daemon needs to be started by running the startup script as root: <code>$/etc/init.d/mdns start</code> </li>
</ol>

<p>Further, you have to build a shared library in order that Bonjour for Java works:</p>

<ol>
 <li>in the directory <code>mDNSPosix</code> type <code>$make os=[linux|solaris|...] Java</code></li>
 <li>as user root, copy the file <code>libjdns_sd.so</code> from <code>build/prod</code> to somewhere in the Java library path (cf. system property <code>java.library.path</code>)</li>
</ol>

<p><b>Step 6:</b> Download the current version of ACE</a> for other platforms at the <a href="http://ace.iserver.ch/forward.php?os=ace-m4.tar.gz" target="_blank">SourceForge download page</a> (<code>ace-m4.tar.gz</code>).</p>

<p>For older releases of ACE, go to the <a href="index.php?page=16">Download Archive</a> page.</p>

<p><b>Step 7:</b> Make sure, you have <a href="http://ant.apache.org/" target="_blank">Apache Ant</a> and the <a href="http://maven.apache.org/download.html" target="_blank">Maven Tasks for Ant</a> installed.</p>

<p><b>Step 8:</b> To run ACE, type <code>$ant run</code> in the top-level directory.</p>
<br>
<p>Please report any problems <a href="http://ace.iserver.ch/forum/">here</a>.</p>

<p>Have fun!</p>
