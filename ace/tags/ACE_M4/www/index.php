<html>
 <head>
  <title>ACE - a collaborative editor</title>
  <link rel="stylesheet" type="text/css" href="css/site.css" />
  <meta name="keywords" content="ace, collaborative, editor, text editor, opensource, java, beep, bonjour, operational, transformation, concurrency, control, groupware"/>
  <meta name="author" content="ace project team"/>
 </head>
 <body>
  <div align="center">
  
  <div id="header">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
     <tr>
      <td style="padding-left: 15px; padding-top: 13px">
       <a href="index.php"><img src="images/logo.png" border="0"/></a>
      </td>
      <td id="quicknav">
       <a href="http://ace.iserver.ch/jira">Jira</a> | 
       <a href="http://ace.iserver.ch/confluence">Confluence</a> | 
       <a href="http://ace.iserver.ch/cgi-bin/trac.cgi">Trac</a> |
       <a href="/api">API</a>
      </td>
     </tr>
    </table>
  </div>
  
  <div id="mainnav">
   <a href="index.php?page=1"><img src="images/navbar-intro.jpg" border="0"/></a>
   <a href="index.php?page=2"><img src="images/navbar-vision.jpg" border="0"/></a>
   <a href="index.php?page=3"><img src="images/navbar-background.jpg" border="0"/></a>
   <a href="mailto:feedback@ace.iserver.ch"><img src="images/navbar-contact.jpg" border="0"/></a>
  </div>
  
  <div id="main">
   <table border="0" cellspacing="0" cellpadding="0" 
          style="margin-left: 10px; width: 100%">
    <tr>
     <td valign="top" align="left" width="160">
      <div class="block">
       <ul>
        <li><a href="index.php">Home</a></li>
        <li><a href="index.php?page=8">Downloads</a></li>
        <li><a href="index.php?page=9">Screenshots</a></li>      
        <li><a href="index.php?page=7">Documents</a></li>
        <li><a href="index.php?page=10">Mailing Lists</a></li>
        <li><a href="index.php?page=5">Development</a></li>
       </ul>
      </div>
      <div align="center" style="margin-top: 25px; margin-bottom: 15px">
       <a href="index.php?page=8"><img src="images/download.png" border="0"/></a><br/><br/>
       <img src="images/opensource.png" border="0"/>
       <br/><br/>
       <a href="http://sourceforge.net"><img src="http://sflogo.sourceforge.net/sflogo.php?group_id=161008&amp;type=2" width="125" height="37" border="0" alt="SourceForge.net Logo" /></a>
      </div>
    </td>
    <td style="vertical-align: top; padding: 10px; padding-right: 30px">
     <div id="content">
       <?php
       switch ($_GET['page']) {
       case 1:
         require('inc/introduction.inc.php');
         break;
       case 2:
         require('inc/vision.inc.php');
         break;
       case 3:
         require('inc/links.inc.php');
         break;
       case 5:
         require('inc/development.inc.php');
         break;
       case 7:
         require('inc/documents.inc.php');
         break;
       case 8:
         require('inc/downloads.inc.php');
         break;
       case 9:
         require('inc/screenshots.inc.php');
         break;
       case 10:
         require('inc/mailinglists.inc.php');
         break; 
       default:
         require('inc/index.inc.php');
       }
       ?>
      </div>
     </td>
    </tr>
   </table>
  </div>
  
  <div id="footernav" align="center">
    <div style="width: 700px; border-top: 1px solid #eee; padding-top: 10px">
     <a href="index.php">Home</a> |
     <a href="index.php?page=8">Downloads</a> |
     <a href="mailto:feedback@ace.iserver.ch">Contact</a>
    </div>
  </div>
  
  <div id="footer">
   <div style="padding-top: 5px">Copyright &copy; 2005 - ace project team. All rights reserved.</div>
  </div>
  
  </div>
 </body>
</html>
