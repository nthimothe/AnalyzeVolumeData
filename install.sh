#!/bin/bash

# write to crontab 

loc=`pwd`
# record volume stats data script
RVSD_SCRIPT="runRVSD.sh"
VSSTATS_SCRIPT="runVSStats.sh"
CRON_FILE="collect.cron"

function create_record_script {
    # if the script, doesn't exist, write it and set permissions
    if [ ! -f $RVSD_SCRIPT ]; then
	echo "cd ${loc}" >> $RVSD_SCRIPT
	# ensure the script compiles the program if it is not compiled
	echo "if [ ! -f \"RecordVolSettingsData.class\" ]; then" >> $RVSD_SCRIPT
	echo "    javac RecordVolSettingsData.java" >> $RVSD_SCRIPT
	echo "fi" >> $RVSD_SCRIPT
	echo "java RecordVolSettingsData" >> $RVSD_SCRIPT
	chmod 744 $RVSD_SCRIPT
	echo "Successfully wrote $RVSD_SCRIPT..."
    else 
	echo "$RVSD_SCRIPT already exists..."
    fi
}

# create script that will run every time a new bash profile is opened, to allow for clean summary of data
function create_run_script {
    # if the script, doesn't exist, write it and set permissions
    if [ ! -f $VSSTATS_SCRIPT ]; then
        # remember previous location
	echo "location=\`pwd\`" >> $VSSTATS_SCRIPT
        # go to the location that has the java program
	echo "cd ${loc}" >> $VSSTATS_SCRIPT

	# ensure the script compiles the program if it is not compiled
	echo "if [ ! -f \"VolumeSettingsStatistics.class\" ]; then" >> $VSSTATS_SCRIPT
	echo "    javac VolumeSettingsStatistics.java" >> $VSSTATS_SCRIPT
	echo "fi" >> $VSSTATS_SCRIPT
	
        # run the java program, taking in data as input from the scanner
	echo "java VolumeSettingsStatistics < /tmp/user_vsdata.txt" >> $VSSTATS_SCRIPT

        # return to previous location
	echo "cd \${location}" >> $VSSTATS_SCRIPT
	echo "Successfully wrote $VSSTATS_SCRIPT..."
    else 
	echo "$VSSTATS_SCRIPT already exists..."
    fi
}

function preserve_cron {
    cron=`sudo crontab -l | grep ""`
    echo "$cron" >> $CRON_FILE
}

function append_to_cron {
    line="* * * * * . ${loc}/runRVSD.sh"
	echo "Potentially prompting for password to add cronjob..."
    if [ ! -f "collect.cron" ]; then
	preserve_cron
	echo "$line" >> $CRON_FILE
	sudo crontab $CRON_FILE
	echo "Successfully wrote collect.cron and added it as cron job..."
    else 
	echo "collect.cron already exists..."
    fi 

}

function append_to_profile {
    if ! bash_job_exists; then 
	echo ". ${loc}/$VSSTATS_SCRIPT" >> ${HOME}/.bash_profile
    else 
	echo "Bash job already exists..."
    fi
}

function bash_job_exists {
    job=`grep ". ${loc}/${VSSTATS_SCRIPT}" ${HOME}/.bash_profile`
    # job is not present, return false                                                                                                       
    [[ -z "$job" ]] && false
    # job is present, return true                                                                                                            
    [[ ! -z "$job" ]] && return
}


# make sure volume data file is present to avoid "No such file" error
sudo touch /tmp/user_vsdata.txt

create_record_script

create_run_script

append_to_cron

append_to_profile

