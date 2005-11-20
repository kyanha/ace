<html>
 <head>
  <title>ACE - a collaborative editor</title>
  <link rel="stylesheet" type="text/css" href="css/site.css" />
  <meta name="keywords" content="ace, collaborative, editor, operational, transformation, concurrency, control, groupware"/>
  <meta name="author" content="ace project team"/>
 </head>
 <body>
  <div align="center">
  
  <div id="header">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
     <tr>
      <td style="padding-left: 15px; padding-top: 13px">
       <img src="images/logo.png"/>
      </td>
      <td id="quicknav">
     <a href="http://ace.iserver.ch:8080/jira">Jira</a> | 
     <a href="http://ace.iserver.ch:8080/confluence">Confluence</a> | 
     <a href="http://ace.iserver.ch:81/cgi-bin/trac.cgi">Trac</a>
      </td>
     </tr>
    </table>
  </div>
  
  <div id="mainnav">
   <img src="images/navbar-intro.jpg"/>
   <img src="images/navbar-vision.jpg"/>
   <img src="images/navbar-background.jpg"/>
   <img src="images/navbar-contact.jpg"/>
  </div>
  
  <div id="main">
   <table border="0" cellspacing="0" cellpadding="0" 
          style="margin-left: 10px; width: 100%">
    <tr>
     <td valign="top">
      <div class="block">
       <ul>
        <li><a href="index.php">Home</a></li>
        <li><a href="index.php?page=2">Documents</a></li>
        <li><a href="index.php?page=3">Links</a></li>
        <li><a href="index.php?page=4">Team</a></li>
        <li><a href="index.php?page=5">Development</a></li>
        <li><a href="index.php?page=6">Testing</a></li>
       </ul>
      </div>
      <div align="center" style="margin-top: 15px; margin-bottom: 15px">
       <img src="images/download.png"/>
      </div>
    </td>
    <td style="padding: 10px; padding-right: 30px">
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
         require('inc/background.inc.php');
         break;
       case 4:
         require('inc/team.inc.php');
         break;
       case 5:
         require('inc/development.inc.php');
         break;
       case 6:
         require('inc/testing.inc.php');
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
  
  <div id="footer">
   <div style="padding-top: 5px">Copyright &copy; 2005 - ace project team. All rights reserved.</div>
  </div>
  
  </div>
 </body>
</html>