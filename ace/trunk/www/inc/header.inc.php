  <?php 
  
  function mainnav_link($target, $page, $str) {
    echo "<a href='index.php?page=".$target."'";
    if ($target == $page) {
      echo " class='active'";
    }
    echo ">$str</a>";
  }
  
  ?>

  <div id="header">
   <a class="anon" href="http://www.edgewall.com/">
    <img id="header-logo" class="noborder" src="images/logo.gif" alt="ACE - a collaborative editor" />
   </a>
   <div id="mainnav" class="nav">
    <ul>
      <li><?php mainnav_link(0, $page, "Home"); ?></li>
      <li><?php mainnav_link(4, $page, "Team"); ?></li>
      <li><a href="mailto:ace@ace.iserver.ch">Contact</a></li>
    </ul>
   </div>
  </div>
