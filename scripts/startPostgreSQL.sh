#!/bin/bash
rm -rf ../temp
mkdir  ../temp
echo "export PGDATA=$PWD/../temp" >> ~/.bashrc
source ~/.bashrc
initdb
echo "export PGPORT=4213" >> ~/.bashrc
source ~/.bashrc
pg_ctl -o "-p $PGPORT" -D $PGDATA -l logfile start
pg_ctl status
echo "export DB_NAME=amaccDB" >> ~/.bashrc
source ~/.bashrc
pg_ctl status
echo PGPORT: $PGPORT
echo PGDATA: $PGDATA
echo DB_NAME: $DB_NAME
echo USER: $USER
pg_ctl status
