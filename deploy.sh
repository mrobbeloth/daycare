#!/bin/bash

# Create daycare database
echo "Creating Daycare Database"
createdb daycare

# Process the DDL script for the daycare project
# on the daycare database
psql -f mock/createTables.sql daycare

# Process the DML scripts for the daycare project
# on the daycare database
psql -f mock/insert_daycare_log_data.sql daycare

# Status prompt
read -p "Daycare tables created, mock data inserted, press any key to continue"

# Remove daycare database 
echo "Would you like to remove the database (yes | no)"
read delChoice
if [ $delChoice == 'yes' ] 
then
   echo "Removing Daycare Database"
   dropdb daycare
else
   echo "Not dropping Daycare Database"
fi
