#!/bin/bash

# Create daycare database
echo "Creating Daycare Database"
createdb daycare

read -p "Press any key to resume ..."

# Remvoe daycare database
echo "Removing Daycare Database"
dropdb daycare
