<?php
include("functions.inc.php");

if (!isset($_GET['id'])) {
  header("Location: index.php\n\n");
}

$id = $_GET['id'];

if ($id < 0 || $id > 11) {
  header("Location: index.php\n\n");
} else {
  $link = mysql_connect('localhost', 'ace', 'ace13x');
  mysql_select_db('ace');
  $query = 'update downloads set count = count + 1 where id=' . $id;
  mysql_query($query);
  $query = 'select name from downloads where id=' . $id;
  $result = mysql_query($query);
  $row = mysql_fetch_row($result);
  $name = $row[0];
  mysql_free_result($result);
  mysql_close($link);
  header("Location: downloads/".$name."\n\n");

  $address = $_SERVER['REMOTE_ADDR']; 
  $url = "http://api.hostip.info/get_html.php?ip=$address";
  $target = "/home/ace/ace.iserver.ch/www/logs/clients-" . date("d.m.Y") . ".txt";
  $title = "Type: $name\nRecipient: $address";
  download($url, $target, $title);
}

?>
