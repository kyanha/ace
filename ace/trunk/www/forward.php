<?php
include("functions.inc.php");

header("Location: http://sourceforge.net/project/showfiles.php?group_id=161008\n\n");

$os = $_GET['os'];
$address = $_SERVER['REMOTE_ADDR'];
$url = "http://api.hostip.info/get_html.php?ip=$address";
$target = "/home/ace/ace.iserver.ch/www/logs/clients-" . date("d.m.Y") . ".txt";
$title = "Type: $os\nRecipient: $address";
download($url, $target, $title);

?>
