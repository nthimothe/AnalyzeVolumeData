#!/bin/bash

# write to crontab 

loc=`pwd`
# record volume stats data script
RVSD_SCRIPT="runRVSD.sh"
VSSTATS_SCRIPT="runVSStats.sh"

function delete_record_script {
    # if the script, doesn't exist, write it and set permissions
    if [ -f $RVSD_SCRIPT ]; then
	rm -f $RVSD_SCRIPT
	echo "Successfully deleted $RVSD_SCRIPT..."
    else
	echo "$RVSD_SCRIPT has already been deleted..." 
    fi
}

function delete_run_script {
    if [ -f $VSSTATS_SCRIPT ]; then
	rm -f $VSSTATS_SCRIPT
	echo "Successfully deleted $VSSTATS_SCRIPT..."
    else
	echo "$VSSTATS_SCRIPT has already been deleted..." 
    fi 
}

function delete_cron_file {
    if [ -f "collect.cron" ]; then
	rm -f "collect.cron"
	echo "Successfully deleted collect.cron..."
    else
	echo "collect.cron has already been deleted..." 
    fi
}

function delete_cron_job {
    didExist=false
    # check if job exists before attempt to remove job
    if cron_job_exists; then
	didExist=true
    fi

    if ! $didExist; then 
	echo "No cron job present to uninstall"
	return
    fi

    # preserve all other lines of cron besides specific cron job installed by this program
    lines=`sudo crontab -l | grep -v "* * * * * . ${loc}/runRVSD.sh"`
    # overwrite sudo crontab
    echo "$lines" > tmp.cron
    sudo crontab tmp.cron
    rm -f tmp.cron
    

    # check if job currently exists
    exists=true
    if ! cron_job_exists; then
	exists=false
    fi
    

    # if cron job previously existed, but no longer exists now, then it's been successfully removed
    if $didExist && ! $exists; then 
	echo "Successfully deleted cron job..."
    else
	echo "Unable to delete cron job..."
    fi
}

function delete_bash_prof_append {
    if ! bash_job_exists; then
	echo "No bash job present to remove"
    else 
        # preserve all other lines of bash_prof besides specific line installed by this program
	lines=`grep -v ". ${loc}/${VSSTATS_SCRIPT}" ${HOME}/.bash_profile`
	sudo echo "$lines" > ${HOME}/.bash_profile	
	echo "Successfully deleted job in ${HOME}/.bash_profile..."
    fi
}


function delete_volume_data {
    if [ -f "/tmp/user_vsdata.txt" ]; then
	sudo rm -f "/tmp/user_vsdata.txt"
	echo "Successfully deleted volume data..."
    else 
	echo "No volume data present to delete"
    fi
}

function cron_job_exists {
    job=`sudo crontab -l | grep "* * * * * . ${loc}/runRVSD.sh"`
    # job is not present, return false
    [[ -z "$job" ]] && false
    # job is present, return true
    [[ ! -z "$job" ]] && return
}

function bash_job_exists {
    job=`grep ". ${loc}/${VSSTATS_SCRIPT}" ${HOME}/.bash_profile`   
    # job is not present, return false
    [[ -z "$job" ]] && false
    # job is present, return true
    [[ ! -z "$job" ]] && return
}

delete_record_script
delete_run_script
delete_cron_file
delete_cron_job
delete_bash_prof_append
delete_volume_data
