package ym.simulation.cloud;

import java.util.ArrayList;

class CodingSet{
	String preset;
	double codingTime;
	double outputBitR;
	double psnr;
}

public class Task {
	String videoName;
	int queueIndex;
	double origBitR;	
	long userID;
	long taskID;
	ArrayList<CodingSet> codingSets;
	
	long rec_svrID;
	long rec_currentQlen;
	double rec_currentWorkSize;
	
	double rec_inTS; //time stamp
	double rec_serveTS;
	double rec_outTS;
	String rec_preset;

	public Task() {
		super();
	}
	
	public Task(Task task){
		
		this.videoName = task.videoName;
		this.queueIndex = task.queueIndex;
		this.origBitR = task.origBitR;
		this.userID = task.userID;
		this.taskID = task.taskID;
		this.codingSets = task.codingSets;
		
		this.rec_svrID = task.rec_svrID;
		this.rec_currentQlen = task.rec_currentQlen;
		this.rec_currentWorkSize = task.rec_currentWorkSize;
		
		this.rec_inTS = task.rec_inTS;
		this.rec_serveTS = task.rec_serveTS;
		this.rec_outTS = task.rec_outTS;
		this.rec_preset = task.rec_preset;
	}

	
	public CodingSet getCodingResult(String preset){
		CodingSet cset = null;
		for (CodingSet coding : codingSets) {
			if (coding.preset.equals(preset)) {
				cset = coding;
			}
		}
		return cset;
	}

	public String getContent() {
		StringBuffer content = new StringBuffer();
		content.append(videoName).append(":");
		for (CodingSet cset : codingSets) {
			content.append(cset.preset).append("=[").append(cset.codingTime)
					.append(",").append(cset.outputBitR).append("], ");
		}
		
		return content.toString();
	}

	public double getMinBitrate() {
		double min=Double.MAX_VALUE;
		// get the minimal coding time
		for (CodingSet cSet : codingSets) {
			if (cSet.outputBitR <= min) {
				min = cSet.outputBitR;
			}
		}
		return min;
	}
}
