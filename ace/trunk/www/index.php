<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <title>ACE - a collaborative editor</title>
  <link rel="stylesheet" type="text/css" href="css/site.css" />
  <style type="text/css"></style>
 </head>
 <body>
 
   <?php
     $page = $_GET['page'];
     if ($page < 0 || $page > 5) {
       $page = 0;
     }
     $contact = "feedback@ace.iserver.ch";
   ?>
   
   <div id="wrapper">
     <?php require('inc/topbar.inc.php'); ?>
     <?php require('inc/header.inc.php'); ?>

     <div id="main" class="">
       <?php require('inc/left.inc.php'); ?>
       <?php require('inc/right.inc.php'); ?>

       <div id="content">

       <?php
       
       switch ($page) {
       case 1:
         require('inc/vision.inc.php');
         break;
       case 2:
         require('inc/documents.inc.php');
         break;
       case 3:
         require('inc/links.inc.php');
         break;
       case 4:
         require('inc/team.inc.php');
         break;
       case 5:
         require('inc/development.inc.php');
         break;
       default:
         require('inc/index.inc.php');
       }
       
       ?>

     </div> <!-- #content -->

     <?php require('inc/footer.inc.php'); ?>

   </div> <!-- main -->
  </div> <!-- wrapper -->
 </body>
</html>
