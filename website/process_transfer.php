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

            // Log the transfer entry and grab tid for use in other tables
            $DMLStmtTransfer = "INSERT INTO TRANSFER VALUES (DEFAULT, '" . date("Y-m-d") . "', '" . date("H:i:s") . "') RETURNING tid;";
            
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