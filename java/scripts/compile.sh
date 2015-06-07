#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Indicate the path of the java compiler to use
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# compile the java program
rm -rf ../classes
mkdir ../classes
javac -d $DIR/../classes $DIR/../src/ProfNetwork.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar ProfNetwork $DB_NAME $PGPORT $USER

