<?php

$docs[1]  = "sun98operational.pdf";
$docs[2]  = "sun98achieving.pdf";
$docs[3]  = "sun98reversible.pdf";
$docs[4]  = "prakash94framework.pdf";
$docs[5]  = "netedit.pdf";
$docs[6]  = "iasted-99.pdf";
$docs[7]  = "henri.pdf";
$docs[8]  = "gclipse_report.pdf";
$docs[9]  = "ecscw03.pdf";
$docs[10] = "dopt.pdf";
$docs[11] = "cscw02cew.pdf";
$docs[12] = "concurrency%20control%20in%20groupware.pdf";
$docs[13] = "choudhary92multiuser.pdf";
$docs[14] = "Zhang.pdf";
$docs[15] = "Ignat.pdf";
$docs[16] = "IEEEIC2000.pdf";
$docs[17] = "DavisSunLu02.pdf";
$docs[18] = "CSCWJ03.pdf";
$docs[19] = "CS-95-06.pdf";
$docs[20] = "Aspect-oriented_composition_paulo.pdf";
$docs[21] = "ACMTOCHI1998.pdf";
$docs[22] = "2003g-in-ecscw.pdf";
$docs[23] = "2002g-in-cewcscw.pdf";
$docs[24] = "archForCollabApp.pdf";
$docs[25] = "usability-cscw.pdf";
$docs[26] = "RR-5188.pdf";
$docs[27] = "icdcs04.pdf";
$docs[28] = "cscw96.pdf";
$docs[29] = "sun97generic.pdf";
$docs[30] = "sun99optional.pdf";
$docs[31] = "group99-final.pdf";
$docs[32] = "main.pdf";
$docs[33] = "icpads04.pdf";
$docs[34] = "bouazza.pdf";
$docs[35] = "vidot.pdf";
$docs[36] = "MMGroupEditor.pdf";
$docs[37] = "cscw04sdt.pdf";
$docs[38] = "WCWA_17_02_2003.pdf";
$docs[39] = "LiLi.pdf";
$docs[40] = "undoAsConcurrentInverseInGroupEditors.pdf";
$docs[41] = "concurrencyControlInGroupwareSystems.pdf";
$docs[42] = "soct2_1.pdf";
$docs[43] = "jupiter.pdf";
$docs[44] = "nice.pdf";
$docs[45] = "proofResselAlgorithm.pdf";
$docs[46] = "time-clocks.pdf";
$docs[47] = "VirtTimeGlobStates.pdf";
$docs[48] = "82890036.pdf";
$docs[49] = "netedit_thesis.pdf";
$docs[50] = "cscwtrends.pdf";

if (isset($_GET["id"])) {
  $id = $_GET["id"];
  Header("Location: http://ace.iserver.ch:81/docs/".$docs["".$id]."\n");
}

?>
