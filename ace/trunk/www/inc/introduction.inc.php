<h2>Introduction</h2>

<p><b>Working on a document as a group can be challenging. Version control systems like Subversion and CVS help to keep a consistent copy of a document, but they do not allow real-time collaboration. Would it not be easier and more efficient to edit a document together in real-time, seeing each other's changes immediately and reducing the amount of time to complete work substantially? The error prone step of merging several asynchronous changes to a document can be avoided altogether with a real-time synchronous collaborative editor, namely ACE - a collaborative editor.</b>
<p>
ACE allows multiple geographically dispersed users to view and edit a shared text document at the same time, thus facilitating efficient teamwork. According to available knowledge, there is only one such application; it works well and has reached widespread use. Unfortunately, it is limited to the Mac platform. ACE is the first collaborative text editor that runs across all major platforms, including Windows, Linux, and Mac.
</p>
<p>
First of all, ACE is a simple text editor with standard features such as copy/paste and load/save. Multiple documents can be edited at the same time. Furthermore, ACE can share documents with other users on different computers, connected by communication networks. ACE also discovers users and their shared documents automatically in a local area network. Users can opt to join any discovered shared document. For all this, no configuration is necessary because it is based on zero-conf networking (also known as Bonjour or Rendezvous).
</p>
<p>
Once a user has joined a shared document, he can freely edit the document at the same time with all participants as a virtual team. So-called awareness information helps to avoid unnecessary conflicts (i.e. two users editing at the same time and text location). Awareness information includes the cursor and the currently selected text of the other users marked with the color of the respective user.
</p>
<p>
The heart of the application is a concurrency control algorithm based on the innovative concept of Operational Transformation, which allows for lock-free editing of a document by multiple users. It imposes no editing constraints and resolves all conflicts automatically. The algorithm overcomes one of the most significant challenges in designing and implementing real-time collaborative editing systems, namely consistency preservation. That is to ensure that at quiescence (i.e. when no messages are in transit) the shared document is identical for all participants. This algorithm was implemented as part of the semester project and forms the foundation of ACE.
</p>
<p>
ACE builds upon open technologies such as BEEP (RFC 3080) and zero-conf networking; all this leaves the possibility open to communicate even with different applications that understand the public protocol of ACE.
</p>
