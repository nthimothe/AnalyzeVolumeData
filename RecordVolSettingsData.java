//(c) 2019 Nathan Thimothe
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

class RecordVolSettingsData{
    void keepTrack(VolumeSettings current){
	//format for 12hr date: MM/dd/yyyy hh:mm:ss aa
	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
	Date now = new Date();
	String currentDate = format.format(now);
	final File parentPath = new File("/tmp");
	final File path = new File(parentPath, "user_vsdata.txt");
	try{
	    //write the current VolumeSettings to this path
	    //make sure to append to this file!
	    FileWriter fWrite = new FileWriter(path, true);
	    BufferedWriter bWrite = new BufferedWriter(fWrite);
	    bWrite.write(String.valueOf(current.getOutputVol()));
	    bWrite.write(", " + current.getInputVol());
	    bWrite.write(", " + current.getAlertVol());
	    bWrite.write(", " + current.getOutputMuted() +" | " + currentDate + "\n");
	    bWrite.close();
	} catch (IOException e){
	    e.printStackTrace();
	}
    }
    public static void main(String[] args){
	VolumeSettings data = new VolumeSettings();
	RecordVolSettingsData write = new RecordVolSettingsData();
	write.keepTrack(data.getSysVolumeSettings());
    }
}
