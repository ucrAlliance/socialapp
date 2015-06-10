#!/bin/bash
#DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#echo $DIR
psql -p $PGPORT $DB_NAME < $PWD/../sql/create_tables.sql
psql -p $PGPORT $DB_NAME < $PWD/../sql/create_indexes.sql
psql -p $PGPORT $DB_NAME < $PWD/../sql/load_data.sql
psql -p $PGPORT $DB_NAME < $PWD/../sql/triggers.sql
