<?

/*  downloads a file similar to 'wget $file_source' and	 
 *  writes it into $file_target.	
 */	
function download($file_source, $file_target, $comment) {
       $rh = fopen($file_source, 'r');
       $wh = fopen($file_target, 'a');

       if ($rh===false || $wh===false) {
 	   // error reading or opening file
           return true;
       }
       $meta = "\n\n--- " . date("d.m.Y, H:i") . " ---\n";
       fwrite($wh, $meta);
       $comment = "$comment\n";	
       fwrite($wh, $comment);
 
       while (!feof($rh)) {
           if (fwrite($wh, fread($rh, 1024)) === FALSE) {
                   // 'Download error: Cannot write to file ('.$file_target.')';
                   return true;
               }
       }
       fclose($rh);
       fclose($wh);
       // No error
       return false;
   }

?>
