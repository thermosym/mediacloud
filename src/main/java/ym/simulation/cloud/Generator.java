package ym.simulation.cloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
* Generate a stream of Tasks for 8.0 time units.
*/
public class Generator extends Event {
    Queue queue;
    int queueIndex;
    long m_taskIndex;
    double lastTS;
    double avg_interval;
    
    private ArrayList<Task> traceList;
    private int lastArriveIndex;
    public String presets[]={"ultrafast", "superfast", "veryfast", "faster", "fast", 
			"medium", "slow", "slower", "veryslow" };

    public Generator(int queueIndex, double lastTS, double avg_interval){
    	this.queueIndex = queueIndex;
    	this.lastTS = lastTS;
    	this.avg_interval = avg_interval;
    	m_taskIndex = 0;
    	traceList = new ArrayList<Task>();
    	lastArriveIndex = 0;
    }
    /**
    * Create a new Task.  Add the Task to the queue  and
    * schedule the creation of the next Task
    */
    void execute(AbstractSimulator simulator) {
    	Task task = getOneVideo();
    	if (task != null){
        	task.rec_inTS = ((Simulator)simulator).now();
        	task.queueIndex = this.queueIndex;
        	task.rec_preset = "fast"; //TODO: set default preset 
            queue.insert(simulator, task); // insert the task to queue and schedule it
            
            String contentString = task.getContent(); 
//            System.out.println(contentString);
            
            time += avg_interval; //MyRandom.exponential(avg_interval)
            //time += avg_interval;
            if (time < lastTS) simulator.insert(this);    		
    	}
    }
    
    private Task getOneVideo() {
    	Task videoTask = null;
		if (traceList.size() <= 0) {
			System.err.println("The trace has no video.");
		}
		
		// loop select the video from the trace
		if (lastArriveIndex >= traceList.size()-1 ) {
//			lastArriveIndex = 0; // reach the end of list, go back again
//			videoTask = traceList.get(lastArriveIndex);
		} else {
			videoTask = new Task(traceList.get(lastArriveIndex));
			lastArriveIndex++; // next video
		}
		
		return videoTask;
	}
    
	public void parseTraceTXT(String videoName) {
		
		for (String pset : presets) {
			// reading data from txt file
			File file = new File("./trace/"+videoName+pset+".txt");
			BufferedReader br;
			String[] enT_String = null;
			String[] bitR_String = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				enT_String = br.readLine().split("\\s+");
				bitR_String = br.readLine().split("\\s+");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//for each video segment
			for (int i = 0; i < enT_String.length; i++) {

				String vName = videoName+i;
				double origBitR = 0;
				double enT = Double.parseDouble(enT_String[i]);
				double bitR = Double.parseDouble(bitR_String[i]); 
				updateTaskTrace(pset,vName,origBitR,enT,bitR);
			}

		}
		
	}

	private void updateTaskTrace(String pset, String vName, double origBitR,
			double enT, double enbitR) {
		Task tskTask=null;
		
		for (int i = 0; i < traceList.size(); i++) {
			if (traceList.get(i).videoName.equals(vName)){
				//find this video
				tskTask = traceList.get(i); 
				break;
			}
		}
		
		if (tskTask != null){
			//update it
			CodingSet cSet = new CodingSet();
			cSet.preset = pset;
			cSet.outputBitR = enbitR;
			cSet.codingTime = enT;
			tskTask.codingSets.add(cSet);
		}else{
			//create this video
			tskTask = new Task();
			tskTask.videoName = vName;
			tskTask.origBitR = origBitR;
			tskTask.codingSets = new ArrayList<CodingSet>();
			
			CodingSet cSet = new CodingSet();
			cSet.preset = pset;
			cSet.outputBitR = enbitR;
			cSet.codingTime = enT;
			tskTask.codingSets.add(cSet);
			
			traceList.add(tskTask);
		}
	}
}

class MyRandom {
	static java.util.Random rnd;
	static void setRandomSeed() {
		rnd.setSeed(1);
	}
	
	static double exponential(double mean) {
		if (rnd==null){
			rnd = new java.util.Random();
			setRandomSeed();
		}
		return -mean * Math.log(rnd.nextDouble());
	}

	static boolean bernoulli(double p) {
		if (rnd==null){
			rnd = new java.util.Random();
			setRandomSeed();
		}
		return rnd.nextDouble() < p;
	}
	/* .. and other distributions */
}