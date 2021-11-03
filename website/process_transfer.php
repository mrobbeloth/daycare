<!DOCTYPE html>
<html xml:lang='en'>
    <head>
        <title>Verification</title>
    </head>
    <body>
        <?php
            $cname = $_POST['child_name_id'];
            $pname = $_POST['parent_name_id'];
            $cat_ch = $_POST['category_choice'];
            $mood_ch = $_POST['mood_choice'];
            $array_child = array();
            $array_parent = array();              
            
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
                    $cnt = 0;
                    foreach($CursorChildQuery as $Row) {
                        foreach($Row as $Column) {
                            print("<p>" . $Column . "</p><br/>");
                            $array_child[$cnt++] = $Column;
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

            if((count($array_child) == 1) && ($cid == -1)) {
                $cid = $array_child[0];
                unset($array_child[0]);
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
                    $cnt = 0;
                    foreach($CursorChildQuery as $Row) {
                        foreach($Row as $Column) {
                            print("<p>" . $Column . "</p><br/>");
                            $array_parent[$cnt++] = $Column;
                        }
                    }
                }                
            }
            else {
                $paID = $pname;
            }

            if((count($array_parent) == 1) && ($paId == -1)) {
                $paID = $array_parent[0];
                unset($array_parent[0]);
            }            

            // TODO:
            // verify correct parent and child pairing from family relation before permitting insertion
            $arrChldCnt = count($array_child);
            $arrPrntCnt = count($array_parent);
            for($i = 0; $i < $arrChldCnt; $i++) {
                for($j = 0; $j < $array_parent; $j++) {
                    $nameSearchQuery = "SELECT count(*) from FAMILY WHERE cid=" . $array_child[i] . " and pid=" . $array_parent[j];
                    if($ResultChildQuery = pg_query($daycareDBConnection, $nameSearchQuery)) {
                        $CursorParentQuery = pg_fetch_all($ResultParentQuery);
                        foreach($CursorChildQuery as $Row) {
                            foreach($Row as $Column) {
                                $cntResult = $Column;
                                if ($cntResult == 1) {
                                    print("<p>" . "Verified pairing" . "</p><br/>");
                                    break;
                                }
                            }
                        }
                    }
                }
            }            

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
            $DMLStmtAtt = "INSERT INTO ATTENDANCE VALUES (" . $tid . "," . $cid . ");";
            if(!($Result = pg_query($daycareDBConnection, $DMLStmtAtt))) {
                print("Failed attendance: " . pg_last_error($daycareDBConnection));
            }

            $DMLStmtParXChge = "INSERT INTO PARENT_XCHGE VALUES (" . $tid . "," . $paID . ");";
            if(!($Result = pg_query($daycareDBConnection, $DMLStmtParXChge))) {
                print("Failed xchge: " . pg_last_error($daycareDBConnection));
            }

            $DMLStmtCatTrsf = "INSERT INTO CATEGORY_TRANSFER VALUES (" . $tid . ",'" . $cat_ch . "');";
            if(!($Result = pg_query($daycareDBConnection, $DMLStmtCatTrsf))) {
                print("Failed categeory: " . pg_last_error($daycareDBConnection));
            }

            $DMLStmtDisTrsf = "INSERT INTO DISPOSITION_TRANSFER VALUES (" . $tid . ",'" . $mood_ch . "');"; 
            if(!($Result = pg_query($daycareDBConnection, $DMLStmtDisTrsf))) {
                print("Failed disposition: " . pg_last_error($daycareDBConnection));
            }
        ?>
    </body>
</html>