#!/bin/bash
rm -rf ~/Desktop/socialapp/CS166_Project/sql/scripts/data
mkdir  ~/Desktop/socialapp/CS166_Project/sql/scripts/data
echo " export PGDATA=~/Desktop/socialapp/CS166_Project/sql/scripts/data " >> ~/.bash_profile
source ~/.bash_profile
initdb
echo " export PGPORT=4213" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl -o "-p $PGPORT" -D $PGDATA -l logfile start
pg_ctl status
echo " export DB_NAME=amaccDB" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl status
echo PGPORT: $PGPORT
echo PGDATA: $PGDATA
echo DB_NAME: $DB_NAME
echo USER: $USER
pg_ctl status
