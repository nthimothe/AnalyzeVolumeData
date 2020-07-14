#!/bin/bash

#remember previous location
location=`pwd`
# go to the location that has the java program -- CHANGE THIS LINE
cd /YOURPATHHERE/
#run the java program, taking in data as input from the scanner
java VolumeSettingsStatistics < /tmp/vsdata.txt
#return to previous location
cd $location
