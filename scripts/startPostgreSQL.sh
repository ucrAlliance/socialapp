#!/bin/bash
rm -rf ../temp
mkdir  ../temp
echo "export PGDATA=$PWD/../temp" >> ~/.bash_profile
source ~/.bash_profile
initdb
echo "export PGPORT=3128" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl -o "-p $PGPORT" -D $PGDATA -l logfile start
pg_ctl status
echo "export DB_NAME=amaccDB" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl status
echo PGPORT: $PGPORT
echo PGDATA: $PGDATA
echo DB_NAME: $DB_NAME
echo USER: $USER
pg_ctl status
