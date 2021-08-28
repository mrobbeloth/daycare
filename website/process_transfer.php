<!DOCTYPE html>
<html>
    <body>
        <?php
            $cname = $_POST['child_name_id'];
            $pname = $_POST['parent_name_id'];
            $cat_ch = $_POST['category_choice'];
            $mood_ch = $_POST['mood_choice'];
            echo "Sign-in Verified for " . $cname;        
            
            // connect to database to perform logging activity
            $configSettings = parse_ini_file("daycaredb.ini");
            $daycareDBConnection = pg_connect("host=" . $configSettings['host'] . " " . 
                                              "dbname=" . $configSettings['dbname'] . " " .
                                              "user=" . $configSettings['user'] . " " .
                                              "password=" . $configSettings['password']);

            // if child name is provided instead of id, Look up child id
            $cid = -1;
            if (!is_numeric($cname)) {
                $name = explode(" ", $cname, 2);
                $first_name = ucfirst(strtolower($name[0]));
                $last_name =  strtoupper($name[1]);

                $nameSearchQuery = "SELECT cid FROM CHILD WHERE fname = '" . $first_name . "' AND lname = '" . $last_name . "';";
                printf($nameSearchQuery);
                if($ResultChildQuery = pg_query($daycareDBConnection, $nameSearchQuery)) {
                    $CursorChildQuery = pg_fetch_all($ResultChildQuery);
                    foreach($CursorChildQuery as $Row) {
                        foreach($Row as $Column) {
                            print("<p>" . $Column . "</p><br/>");
                        }
                    }
                }
                else {
                    printf("<p>" . "No result" . "</p></br>");
                }

            }
            else {
                $cid = $cname;
            }

              // if parent name is provided instead of id, Look up parent id
            $paID = -1;
            if (!is_numeric($pname)) {
                $name = explode(" ", $pname, 2);
                $first_name = ucfirst(strtolower($name[0]));
                $last_name =  strtoupper($name[1]);

                $nameSearchQuery = "SELECT pid FROM PARENT WHERE fname = '" . $first_name . "' AND lname = '" . $last_name . "';";
                printf($nameSearchQuery);
                if($ResultParentQuery = pg_query($daycareDBConnection, $nameSearchQuery)) {
                    $CursorParentQuery = pg_fetch_all($ResultParentQuery);
                }                
            }
            else {
                $paID = $pname;
            }

            // TODO:
            // join child and parent results on family, should hopefully only be one
            // select family.cid, family.pid from family
            // inner join child on child.cid 
            // inner join parent on parent.pid
            // print("<p> " . $cid . " " . $paID . "</p></br>");

            // Log the transfer entry and grab tid for use in other tables
            $DMLStmtTransfer = "INSERT INTO TRANSFER VALUES (DEFAULT, '" . date("Y-m-d") . "', '" . date("H:i:s") . "') RETURNING tid;";
            
            $tid = 0;
            if(!($Result = pg_query($daycareDBConnection, $DMLStmtTransfer))) {
                print("Failed: " . pg_last_error($daycareDBConnection));
            }
            else {
                // if the insert went fine, you should have a single cell tuple result, the tid
                $row = pg_fetch_row($Result);
                $tid = $row[0];
                print("<p>" . $tid . "</p>");
            }


            // Use tid to connect given parent and child transfer
            //$DMLStmtAtt = "INSERT INTO ATTENDANCE VALUES ("
            //$DMLStmtParXChge = "INSERT INTO PARENT_XCHGE VALUES ("
            //$DMLStmtCatTrsf = "INSERT INTO CATEGORY_TRANSFER VALUES ("
            //$DMLStmtDisTrsf = "NSERT INTO DISPOSITION_TRANSFER VALUES ("
        ?>
    </body>
</html>