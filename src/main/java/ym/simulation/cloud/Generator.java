package ym.simulation.cloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    long m_taskIndex;
    double lastTS;
    double avg_interval;
    @Deprecated
    double avg_joblen;
    
    private ArrayList<Task> traceList;
    private int lastArriveIndex;
    
    public Generator(double lastTS, double avg_interval, double avg_joblen){
    	this.lastTS = lastTS;
    	this.avg_interval = avg_interval;
    	this.avg_joblen = avg_joblen;
    	m_taskIndex = 0;
    	traceList = new ArrayList<Task>();
    	lastArriveIndex = 0;
    }
    
    public Generator(double lastTS, double avg_interval){
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
//    	double task_duration = avg_joblen; //Random.exponential(avg_joblen);
//    	double task_duration = MyRandom.exponential(avg_joblen);
    	
    	Task task = new Task(getOneVideo());
    	task.rec_inTS = ((Simulator)simulator).now();
    	task.rec_preset = "medium"; //TODO: set default preset 
        queue.insert(simulator, task);
        
        time += MyRandom.exponential(avg_interval);
        //time += avg_interval;
        if (time < lastTS) simulator.insert(this);
    }
    
    private Task getOneVideo() {
    	Task videoTask = null;
		if (traceList.size() <= 0) {
			System.err.println("The trace has no video.");
		}
		
		// loop select the video from the trace
		if (lastArriveIndex >= traceList.size()-1 ) {
			lastArriveIndex = 0; // reach the end of list, go back again
			videoTask = traceList.get(lastArriveIndex);
		} else {
			lastArriveIndex++; // next video
			videoTask = traceList.get(lastArriveIndex);
		}
		
		return videoTask;
	}
    
	public void parseTrace(String fileName){
    	try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			
			System.out.println("start parse XML Document");

			Element root = document.getRootElement();
			
			List<Element> segmentlist = root.elements("VideoSegment");
			for (Element elmSeg : segmentlist) {
				Task tsk = new Task();
				tsk.videoName = elmSeg.attributeValue("videoName");
				tsk.origSize = Integer.parseInt(elmSeg.attributeValue("OrigSize"));
				tsk.codingSets = new ArrayList<CodingSet>();
				
				List<Element> result = elmSeg.elements("codingResult");
				for (Element elmRt : result) {
					// add into the trace list
					CodingSet cSet = new CodingSet();
					cSet.preset = elmRt.attributeValue("preset");
					cSet.outputSize = Integer.parseInt(elmRt.attributeValue("OutputSize"));
					cSet.codingTime = Double.parseDouble(elmRt.attributeValue("time"));
					cSet.psnr = Double.parseDouble(elmRt.attributeValue("psnr"));
					tsk.codingSets.add(cSet);
				}
				traceList.add(tsk);
			}
			System.out.println("end parse XML Document");
		} catch (Exception e) {
			e.printStackTrace();
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