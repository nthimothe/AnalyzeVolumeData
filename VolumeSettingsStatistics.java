//(c) 2019 Nathan Thimothe
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Calendar;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class VolumeSettingsStatistics{
    Map<VolumeSettings,Calendar> map;

    private Map<VolumeSettings,Calendar> populateMap(){
	map = new LinkedHashMap<>();

	//today's information
	int[] currentDate = dateInfo(null);
	int currMonth = currentDate[0];
	int currDayOfMonth = currentDate[1];
	int currYear = currentDate[2];

	//waiting for input of a file name 
	Scanner s = new Scanner(System.in);
	while (s.hasNextLine()){
	    String line = s.nextLine(); //actual line of data
	    String[] split = line.split("\\|"); //split along the pipe character

	    //parse data relevant to VolumeSettingsObject
	    //split along the comma to parse the different parts of a VolumeSettings Object
	    String[] sett = split[0].split(","); 
	    int outputVolume = Integer.parseInt(sett[0].trim());
	    int inputVolume = Integer.parseInt(sett[1].trim());
	    int alertVolume = Integer.parseInt(sett[2].trim());
	    int muted = Integer.parseInt(sett[3].trim());
	    VolumeSettings settings = new VolumeSettings(outputVolume,inputVolume,alertVolume,muted);

	    //parse data relevant to Date Object;
	    String stringDate = split[1].trim();
	    //format of Date object 
	    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
	    Date date = null; //set it equal to null since it needs to be initialized
	    //must catch for ParseException
	    try{
		date = format.parse(stringDate); //formatting stringDate and putting it in Dateobj
	    } catch (ParseException e){
		System.err.println("Bad date.");
	    }
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    //month, doM , and year taken from the the Date in the file's contents
	    int[] dateInfo = dateInfo(calendar);
	    int month = dateInfo[0];
	    int dayOfMonth = dateInfo[1];
	    int year = dateInfo[2];

	    //only if I have come across yesterday's date should I put it in my map to analyze
	    //this will become more and more time costly as the file grows larger
	    //must figure out a way to jump around based off of specific dates
	    if ( (month == currMonth) && (dayOfMonth == (currDayOfMonth-1)) && (year == currYear)){
		//put information in map
		map.put(settings,calendar);
	    }
	}
	return map;
    }

    int countEntries(){
	int counter = 0;
	for (Map.Entry<VolumeSettings,Calendar> entry : map.entrySet()){
	    counter++;
	}
	return counter;
    }
      
    //will return the most common outputVolume and the number of times it was accessed that day
    Map<Integer,Integer> outputVolMode(){
	//a map of frequencies (k = outputVol, v = number of times accessed)
	Map<Integer,Integer> ovFrequencies = new Hashtable<>();
	int outputVol, currCount;
	//traverse this.map and extract outputVolume
	for (Map.Entry<VolumeSettings,Calendar> entry : map.entrySet()){
	    outputVol = entry.getKey().getOutputVol();
	    //if it is the first time you are seeing a particular volume value, put it in the map with count 1
	    if (!(ovFrequencies.containsKey(outputVol))){
		ovFrequencies.put(outputVol,1);
		//if the vol has been seen before, remove it from map, increment the current count, and put back
	    } else if (ovFrequencies.containsKey(outputVol)){
		currCount = ovFrequencies.get(outputVol);
		ovFrequencies.put(outputVol, ++currCount);
	    }
	}

	//return the outputVol with the highest count
	return findMax(ovFrequencies);
    }
    

    //will return the most common inputVolume and the number of times it was accessed that day
    Map<Integer,Integer> inputVolMode(){
	//a map of frequencies (k = inputVol, v = number of times accessed)
	Map<Integer,Integer> ivFrequencies = new Hashtable<>();
	int inputVol, currCount;
	//traverse this.map and extract outputVolume
	for (Map.Entry<VolumeSettings,Calendar> entry : map.entrySet()){
	    inputVol = entry.getKey().getInputVol();
	    //if it is the first time you are seeing a particular volume value, put it in the map with count 1
	    if (!(ivFrequencies.containsKey(inputVol))){
		ivFrequencies.put(inputVol,1);
		//if the vol has been seen before, remove it from map, increment the current count, and put back
	    } else if (ivFrequencies.containsKey(inputVol)){
		currCount = ivFrequencies.get(inputVol);
		ivFrequencies.put(inputVol, ++currCount);
	    }
	}
	return findMax(ivFrequencies);
    }

    //find the key with the highest value and return that entry
    Map<Integer,Integer> findMax(Map<Integer,Integer> frequencies){
	Map<Integer,Integer> highest = new Hashtable<>();
	int key = 0, maxVal = 0, val;
	for (Map.Entry<Integer,Integer> entry : frequencies.entrySet()){
	    val = entry.getValue();
	    //if I find a value that is greater than my max, then my max is now that value
	    if (val > maxVal){
		key = entry.getKey();
		maxVal = val;
	    }
	}
	highest.put(key,maxVal);
	return highest;
    }
    
    int volumeAverage(int volumeType){ //0 volumeType indicates output, 1 volumeType indicates input
	int totalAccesses = countEntries();
	double accumulation = 0;
	for (Map.Entry<VolumeSettings,Calendar> entry : map.entrySet()){
	    VolumeSettings currentVS = entry.getKey();
	    if (volumeType == 0){
		accumulation += currentVS.getOutputVol();
	    } else if (volumeType == 1){
		accumulation += currentVS.getInputVol();
	    }
	}
	return (int)(Math.round(accumulation / totalAccesses));
    }
    
    //provided a map of (VS,Cal), calculate the number of times a field shifts
    int trackShifts(int volType){ //0 indicates output, 1 indicates input, 2 indicates muted, def: alert
	int current = 0;
	int previous = -1;
	int numShifts = 0;
	for (Map.Entry<VolumeSettings,Calendar> entry : map.entrySet()){
	    VolumeSettings currentVS = entry.getKey();
	    switch (volType){
	    case 0:
		current = currentVS.getOutputVol();
		break;
	    case 1:
		current = currentVS.getInputVol();
		break;
	    case 2:
		current = currentVS.getOutputMuted();
		break;
	    default:
		current = currentVS.getAlertVol();
	    }
	    //we want to track differences between current and previous
	    if (current != previous){
		//if outputMuted is what we want information for, we know that the computer was muted
		//only if the muted status went from not muted (0) to muted (1)
		if ((volType == 2) && (previous == 0) && (current == 1)){
		    numShifts++;
		}
		else if (volType != 2)
		    numShifts++;
	    }
	    //previous becomes current for the next iteration of the loop
	    previous = current;
	}

	//the first time the loop runs, previous is bound to be different from current
	//we don't want to count that first iteration as a shift, so we subtract 1 at the end
	//unless we're counting the number of times muted
	if (volType == 2){
	    return numShifts;
	}
	return --numShifts;
    }

    //provided a calendar, this method returns the month, day of month, and year in an int arr
    private int[] dateInfo(Calendar cal){
	if (cal==null){
	    Date now = new Date();
	    cal = Calendar.getInstance();
	    cal.setTime(now);	    
	}
	int[] dateInfo = {cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR)};
	return dateInfo;
    }
    //return formatted String based off of a date
    public String formattedDate(Calendar cal){
	//if cal is null, use the date in this.map
	if  (cal == null){
	    //set cal to be the first date in the map's calendar value
	    cal = map.entrySet().iterator().next().getValue();
	}
	Date date = cal.getTime();
	SimpleDateFormat format = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy");
	return format.format(date);
    }

    int percentage(int freq, int total){
	if (total != 0){
	    //make sure to round up!
	    return (Math.round( ((float)freq / (float)total) * 100));
	}
	return -1;
    }
    public static void main(String[] args){
	VolumeSettingsStatistics stats = new VolumeSettingsStatistics();
	//creating map
	Map<VolumeSettings, Calendar> populated = stats.populateMap();
	//only if the map is not empty, proceed to analyze data
	if (!(populated.isEmpty())){
	    //total entries in map
	    int totalAccesses = stats.countEntries();
	    int avgOV = 0;
	    int avgIV = 0;
	    int numOVShifts = 0;
	    int numIVShifts = 0;
	    int numOMShifts = 0;
	    String ovShiftStatement = "";
	    String ivShiftStatement = "";
	    String omShiftStatement = "";
	    
	    System.out.println("\033[1mAnalysis of " + stats.formattedDate(null) + "'s data.\033[0m\n");
	    
	    /* OUTPUT VOLUME */
	    //getting most accessed outputVolume
	    Map<Integer,Integer> ovFreqMax = stats.outputVolMode();
	    int ovMostAccessed = 0;
	    int ovFreq = 0;
	    for (Map.Entry<Integer,Integer> onlyEntry : ovFreqMax.entrySet()){
		ovMostAccessed = onlyEntry.getKey();
		ovFreq = onlyEntry.getValue();
		break;
	    }
	    
	    //printing most accessed outputVolume + calculating average output volume + numOVshifts
	    if (ovFreq == totalAccesses){
		System.out.println("Your output volume level was " + ovMostAccessed + " the entire day.");
		avgOV = ovMostAccessed; //if the volume hasn't changed over the whole day, no calcNeeded
		//printing average output volume
		System.out.println("Your average output volume level was " + avgOV + ".");
	    } else{
		System.out.println("For " + stats.percentage(ovFreq,totalAccesses) + "% of the day, your output volume level was " + ovMostAccessed + " (" + ovFreq + "/" + totalAccesses + ").");
		avgOV = stats.volumeAverage(0);
		//printing average output volume
		System.out.println("Your average output volume level was " + avgOV + ".");
		//calculating the number of outputVolume shifts
		numOVShifts = stats.trackShifts(0);
		//only if the number of shifts is 1 do you want to display "time"
		ovShiftStatement = (numOVShifts == 1) ? "You changed your output volume level " + numOVShifts + " time yesterday." : "You changed your output volume level " + numOVShifts + " times yesterday.";
		//will only print shifts if my output volume wasn't the same the entire day!
		System.out.println(ovShiftStatement);
	    }
	    
	    
	    //separating input and output 
	    System.out.println();
	    
	    
	    
	    /* INPUT VOLUME */
	    
	    //getting most accessed inputVolume
	    Map<Integer,Integer> ivFreqMax = stats.inputVolMode();
	    int ivMostAccessed = 0;
	    int ivFreq = 0;
	    for (Map.Entry<Integer,Integer> onlyEntry : ivFreqMax.entrySet()){
		ivMostAccessed = onlyEntry.getKey();
		ivFreq = onlyEntry.getValue();
		break;
	    }
	    
	    //printing most accessed inputVolume + calculating average input volume + numIVShifts
	    if (ivFreq == totalAccesses){
		System.out.println("Your input volume level was " + ivMostAccessed + " for the entire day.");
		avgIV = ivMostAccessed;
		//printing average input Volume
		System.out.println("Your average input volume level was " + avgIV + ".");

	    } else{
		System.out.println("For " + stats.percentage(ivFreq,totalAccesses) + "% of the day, your input volume level was " + ivMostAccessed + " (" + ivFreq + "/" + totalAccesses + ").");
		avgIV = stats.volumeAverage(1);
		//printing average input Volume
		System.out.println("Your average input volume level was " + avgIV + ".");
		//determining the number of shifts made to input volumte
		numIVShifts = stats.trackShifts(1);
		ivShiftStatement = (numIVShifts == 1) ? "You changed your input volume level 1 time yesterday." : "You changed your input volume level " + numIVShifts + " times yesterday.";
		//printing number of input volume shifts
		System.out.println(ivShiftStatement);
	    }

	    
	    /* OUTPUT MUTED */
	    //track the number of times the computer was muted
	    numOMShifts = stats.trackShifts(2);
	    
	    //print formatting
	    omShiftStatement = (numOMShifts == 1) ? "\nYou muted your computer 1 time yesterday." : "\nYou muted your computer " + numOMShifts + " times yesterday.";
	    System.out.println(omShiftStatement);
	    
	    
	    //disclaimer
	    System.out.println("\n***This data was collected once every minute that the machine was running yesterday.***");

	    // if there is no data, tell the user that there is no data
	} else{
	    String user = "";
	    try{
		Process p = new ProcessBuilder("whoami").start();
		BufferedReader bRead = new BufferedReader(new InputStreamReader(p.getInputStream()));
		//since the output of whoami is one line, there is no need to go through each lin
		user = bRead.readLine();
	    } catch (IOException e){
		e.printStackTrace();
	    }
	    System.out.println("Sorry, " + user + ". Unfortunately, there is no data to work with yet! Please check back tomorrow!");
	}
	
    }
}