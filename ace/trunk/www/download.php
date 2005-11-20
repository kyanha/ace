<?php

if (!isset($_GET['id'])) {
  header("Location: index.php\n\n");
}

/*$downloads[0] = "ace-win-m1.exe";
$downloads[1] = "ace-osx-m1.dmg";
$donwloads[2] = "ace-m1.tar.gz";

$downloads[3] = "ace-win-m2.exe";
$downloads[4] = "ace-osx-m2.dmg";
$donwloads[5] = "ace-m2.tar.gz";

$downloads[6] = "ace-win-m3.exe";
$downloads[7] = "ace-osx-m3.dmg";
$donwloads[8] = "ace-m3.tar.gz";*/

$id = $_GET['id'];

if ($id < 0 || $id > 2) {
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
}

?>