# Analayze Volume Data

Summarize your input and output volume over the course of a day with this program!

## Running The Program
* Ensure that you have [Java](https://www.java.com/en/) installed and running on your machine.
* Run the following command: `./install.sh` The script will prompt you for your password since it is modifying the root's cron jobs.
  This will do 3 key things:
  1. Create the script that will collect the volume data of your machine throughout the day.
  2. Ensure that this script runs as often as possible by installing a cron job.
  3. Create the script that will summarize your machine's volume data every time a new bash shell is created (via an addition to your bash_profile)

* Once everything is installed, open a new bash shell or type `bash -l`. It should say it doesn't have information to provide a summary.
* Starting tomorrow, whenever you start a new bash session, you'll be able to see your volume information summarized at the top of your bash shell.

* If at anytime you would like to uninstall the program simply run: `./uninstall.sh` and delete this repository.

## Screenshots

![Summary](https://github.com/nthimothe/AnalyzeVolumeData/blob/master/Screenshots/summary.png)

