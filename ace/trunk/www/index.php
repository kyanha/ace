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
   <a href="forum/"><img src="images/navbar-contact.jpg" border="0"/></a>
  </div>
  
  <div id="main">
   <table border="0" cellspacing="0" cellpadding="0" 
          style="margin-left: 10px; width: 100%">
    <tr>
     <td valign="top" align="left" width="160">
      <div class="block">
       <ul>
        <li><a href="index.php">Home</a></li>
        <li><a href="index.php?page=8">Download</a></li>
        <li><a href="index.php?page=9">Screenshots</a></li>      
        <li><a href="index.php?page=7">Documents</a></li>
        <li><a href="index.php?page=5">Development</a></li>
        <li><a href="index.php?page=10">Mailing Lists</a></li>
        <li><a href="forum/">Forum</a></li>
       </ul>
      </div>
      <div align="center" style="margin-top: 25px; margin-bottom: 15px">
       <a href="index.php?page=8"><img src="images/download.png" border="0"/></a><br/><br/>
       <img src="images/opensource.png" border="0"/>
       <br/><br/>
       <a href="http://sourceforge.net"><img src="http://sflogo.sourceforge.net/sflogo.php?group_id=161008&amp;type=2" width="125" height="37" border="0" alt="SourceForge.net Logo" /></a>
       <br/><br/>
       <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="image" src="https://www.paypal.com/de_DE/i/btn/x-click-but04.gif" border="0" name="submit" alt="Donate with PayPal">
<img alt="" border="0" src="https://www.paypal.com/de_DE/i/scr/pixel.gif" width="1" height="1">
<input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
</form>
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
       case 11:
         require('inc/news.archive.inc.php');
         break;
       case 12:
         require('inc/downloads2.inc.php');
         break;         
       case 13:
         require('inc/windows.download.inc.php');
         break;         
       case 14:
         require('inc/mac.download.inc.php');
         break;         
       case 15:
         require('inc/unix.download.inc.php');
         break;
       case 16:
         require('inc/archive.downloads.inc.php');
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
     <a href="forum/">Contact</a>
    </div>
  </div>
  
  <div id="footer">
   <div style="padding-top: 5px">Copyright &copy; 2006 - ace project team. All rights reserved.</div>
  </div>
  
  </div>
 </body>
</html>
