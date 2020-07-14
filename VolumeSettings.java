//(c) 2019 Nathan Thimothe
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
public class VolumeSettings{
    /* INSTANCE VARIABLES */
    int outputVolume, inputVolume, alertVolume, outputMuted;

    /* CONSTRUCTORS */
    public VolumeSettings(){
	outputVolume = -1;
	inputVolume = -1;
	alertVolume = -1;
	outputVolume = -1;
    }

    public VolumeSettings(int outputVol, int inputVol, int alertVol, int outputMuted){
	this.outputVolume = outputVol;
	this.inputVolume = inputVol;
	this.alertVolume = alertVol;
	this.outputMuted = outputMuted;
    }

    /* ACCESSOR METHODS */
    public int getOutputVol(){ 
	return this.outputVolume;
    }
    public int getInputVol(){ 
	return this.inputVolume;
    }
    public int getAlertVol(){
	return this.alertVolume; 
    }
    public int getOutputMuted(){ 
	return this.outputMuted;
    }
    /* SETTER METHODS (of System and Object) */
    public boolean setSystemOutput(int outputV){
	String arg = "set volume output volume " + outputV;
	try{
	    //system approx reflects the change user wanted
	    Process p = new ProcessBuilder("osascript", "-e", arg).start();
	    //wait for the process to end
	    try{		
		p.waitFor();
	    } catch (InterruptedException ie){ 
		System.out.println("Thread has been interrupted");
	    }
	    /*
	    //since the requested outputV may not match the actual system outputV, find out what the system has approximated the argument too
	    /* This is not ideal because we are rewriting the other 2 unchanged values */
	    getVolumeSettings();

	    return true;
	} catch (IOException e){
	    e.printStackTrace();
	}

	return false;
    }
    public boolean setSystemInput(int inputV){
	String arg = "set volume input volume " + inputV;
	try{
	    //system approx reflects the change user wanted
	    Process p = new ProcessBuilder("osascript", "-e", arg).start();
	    //wait for the process to end before I fetch the system settings
	    try{		
		p.waitFor();
	    } catch (InterruptedException ie){ 
		System.out.println("Thread has been interrupted");
	    }
	    //since the requested inputV may not match the actual system input size, find out what the system has approximated the argument too
	    /* This is not ideal because we are rewriting the other 3 unchanged values */
	    getVolumeSettings();

	    return true;
	} catch (IOException e){
	    e.printStackTrace();
	}
	return false;
    }
    //get VolumeSettings from System Settings and set the parameters of the current object
    public VolumeSettings getVolumeSettings(){
	//VolumeSettings settings = new VolumeSettings();
	try{
	    //start the osascript process with -e (as arg1) and 'get volume settings' (as arg2)
	    Process p = new ProcessBuilder("osascript", "-e","get volume settings").start();
	    //get the data from the command that was run
	    BufferedReader buffRead = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    //get the first line of buffRead (which should be the entire thing)
	    String line = buffRead.readLine();
	    //if the line is not null then parse it
	    if (line != null){
		//split the string to extract the outputVol, inputVol, alertVol, and outputMuted
		String[] fourParts = line.split(",");
		//for each element in fourParts, get the integer value
		this.outputVolume = Integer.valueOf((fourParts[0].split(":"))[1]);
		this.inputVolume = Integer.valueOf((fourParts[1].split(":"))[1]);
		this.alertVolume = Integer.valueOf((fourParts[2].split(":"))[1]);
		//convert the boolean value to an integer
		this.outputMuted = ((fourParts[3].split(":"))[1].equals("true")) ? 1 : 0;
		//advance the line
		line = buffRead.readLine();
	    }
	    else{
		System.out.println("That process returned no data.");
	    }
	} catch (IOException e){ //in the case that I encounter some error
	    e.printStackTrace();
	}
	return this;
    }
    
    @Override
    public String toString(){
	String s = "\toutputVol: " + getOutputVol() + "\n\tinputVol: " + getInputVol() + "\n\talertVol: " + getAlertVol() + "\n\toutputMuted: " + getOutputMuted();
    return s;
    }
    public static void main(String[] args){
	VolumeSettings vol = new VolumeSettings();
	System.out.println(vol.getVolumeSettings().toString());
	Scanner s = new Scanner(System.in);
	//prompt user
	System.out.print("Please enter an output volume: ");
	int output = s.nextInt();
	vol.setSystemOutput(output);
	System.out.println(vol);
	System.out.print("Please enter an input volume: ");
	int input = s.nextInt();
	vol.setSystemInput(input);
	System.out.println(vol);
    }
}