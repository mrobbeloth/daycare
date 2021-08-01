#!/bin/bash

# Create daycare database
echo "Creating Daycare Database"
createdb daycare

# Process the DDL script for the daycare project
# on the daycare database
psql -f createTables.sql daycare

# Debugging prompt
read -p "Press any key to resume ..."

# Remove daycare database 
# Should be placed into separate script at some point
echo "Removing Daycare Database"
dropdb daycare
