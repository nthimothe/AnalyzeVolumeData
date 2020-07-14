# Analayze Volume Data

Summarize your input and output volume over the course of a day with this program!

## Running The Program
* Ensure that you have [Java](https://www.java.com/en/) installed and running on your machine.
* Edit your scheduled tasks with the following command: `sudo crontab -e`, and add the following line...
`* * * * * . /PATH_TO_REPO/runRVSD.sh`
* Change the line marked 'CHANGE THIS LINE' to the path to your repository.
* Add the following line to your .bash_profile: `. /PATH_TO_REPO/runVSStats.sh`
* After a minute, test it out with `bash -l`. It should say it doesn't have information. 

Starting tomorrow, whenever you start a new bash session, you'll be able to see your volume information summarized at the top.

## Screenshots

![Summary](https://github.com/nthimothe/Projects/blob/master/AnalyzeVolumeData/Screenshots/summary.png)