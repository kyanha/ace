<h2>Downloads</h2>

<p>This is the download page of the ACE project. To run ACE, you need the following installed on your
local machine:</p>

<ul>
 <li>Java Runtime Environment (JRE) - 1.4.2 or higher</li>
 <li>Bonjour</li>
</ul>

<p><b>Note:</b> you have to open port 5353 (Bonjour) and 4123 (ACE) in your firewall configuration.</p>

<h3>Mac OS X</h3>
<p>Mac OS X users do not have to install any other software beside ACE itself. To download the current
version of ACE for Mac OS X click <a href="download.php?id=10">here</a>.</p>

<h3>Windows</h3>
<p>Windows users have to install a <a href="http://java.sun.com/j2se/1.5.0/download.jsp">Java Runtime Environment (JRE) - 1.4.2 or higher</a>. Further, 
<a href="http://www.apple.com/downloads/macosx/apple/bonjourforwindows.html">Bonjour for Windows</a> has to
be downloaded. If you have iTunes on your computer, Bonjour is already installed. The ACE installer
warns you, if Bonjour is not installed.</p>
<p>The current version of ACE for Windows can be downloaded <a href="download.php?id=9">here</a>.</p>

<h3>Other Operating Systems</h3>
<p>Chances are high that if there is a <a href="http://java.sun.com/j2se/1.5.0/download.jsp">Java Runtime Environment</a> for your operating system, ACE will work. Unfortunately, there is no installer for
Bonjour on Linux, Solaris, ... That means, you have to build Bonjour from source. In the following instructions, replace <code>os=linux</code>
with your operating system. Check the Makefile in <code>mDNSPosix/Makefile</code> for supported operating systems.</p>

<ol>
 <li>download the source code from <a href="http://developer.apple.com/networking/bonjour/download">here</a></li>
 <li>unpack the downloaded tar.gz to a location of your choice</li>
 <li>go to the subdirectory mDNSPosix</li>
 <li>in the Makefile, adjust the variable <em>JDK</em> to point to the correct JDK location</li>
 <li>type <code>make os=linux</code> to build the mDNSResponder</li>
 <li>as root user, type <code>make os=linux install</code> to install the mDNSResponder daemon</li>
 <li>now, the daemon needs to be started by running the startup script <code>/etc/init.d/mdns start</code> as root</li>
</ol>

<p>Further, you have to build a shared library in order that Bonjour for Java works:</p>

<ul>
 <li>in the directory mDNSPosix type <code>make os=linux Java</code></li>
 <li>as root, copy the file <code>libjdns_sd.so</code> from <code>build/prod</code> to somewhere into the Java library path (system property java.library.path)</li>
</ul>

<p>Next you can download the <a href="download.php?id=11">current version of ACE</a> for other platforms. To run ACE, type 
<code>ant run</code> in the top-level directory. Note: you need <a href="http://
ant.apache.org/">Apache Ant</a> installed in that case.</p>


<h3>Diploma Project Release</h3>
<p>The release notes can be found <a href="http://ace.iserver.ch:81/repos/ace/ace/tags/ACE_DIPLOMA/release-notes.txt">here</a>.</p>
<p>This is a stabilization release of the milestone 3 build.</p>

<ul>
 <li><a href="download.php?id=9">Windows Installer</a></li>
 <li><a href="download.php?id=10">OS X Disk Image</a></li>
 <li><a href="download.php?id=11">Other Platforms</a></li>
</ul>



<h3>Milestone 3</h3>
<p>The release notes can be found <a href="http://ace.iserver.ch:81/repos/ace/ace/tags/ACE_M3/release-notes.txt">here</a>.</p>
<p>Milestone 3 fixed some of the network related issues mentioned in
Milestone 2. However, you may still encounter some session failures. They
are related to the networking library we are using.</p>

<ul>
 <li><a href="download.php?id=6">Windows Installer</a></li>
 <li><a href="download.php?id=7">OS X Disk Image</a></li>
 <li><a href="download.php?id=8">Other Platforms</a></li>
</ul>



<h3>Milestone 2</h3>
<p>The release notes can be found <a href="http://ace.iserver.ch:81/repos/ace/ace/tags/ACE_M2/release-notes.txt">here</a>.</p>
<p>Milestone 2 has some known issues related to network communication. It
can happen that a session fails in some cases. We are trying to fix these
issues as soon as possible.</p>

<ul>
 <li><a href="download.php?id=3">Windows Installer</a></li>
 <li><a href="download.php?id=4">OS X Disk Image</a></li>
 <li><a href="download.php?id=5">Other Platforms</a></li>
</ul>


<h3>Milestone 1</h3>
<p>The release notes can be found <a href="http://ace.iserver.ch:81/repos/ace/ace/tags/ACE_M1/release-notes.txt">here</a>.</p>

<ul>
 <li><a href="download.php?id=0">Windows Installer</a></li>
 <li><a href="download.php?id=1">OS X Disk Image</a></li>
 <li><a href="download.php?id=2">Other Platforms</a></li>
</ul>
