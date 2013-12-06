package ym.simulation.cloud;

import java.util.ArrayList;

class CodingSet{
	String preset;
	double codingTime;
	int outputSize;
	double psnr;
}

public class Task {
	String videoName;
	double origSize;	
	long userID;
	long taskID;
	ArrayList<CodingSet> codingSets;
	
	long rec_svrID;
	long rec_currentQlen;
	double rec_currentWorkSize;
	int rec_currentSvrNum;
	
	double rec_inTS; //time stamp
	double rec_serveTS;
	double rec_outTS;
	String rec_preset;

	@Deprecated
	public Task(double inTS, double serveTS, double outTS, double bit,
			long userID, long taskID, ArrayList<CodingSet> codingSets) {
		super();
		init(inTS, serveTS, outTS, bit, userID, taskID, codingSets);
	}
	
	@Deprecated
	public Task(double bit, long userID, long taskID, ArrayList<CodingSet> codingSets) {
		super();
		init(0, 0, 0, bit, userID, taskID, codingSets);
	}
	
	public Task() {
		super();
//		init(0, 0, 0, 0, -1, -1, null);
	}
	
	public Task(Task task){
		this.videoName = task.videoName;
		this.origSize = task.origSize;
		this.userID = task.userID;
		this.taskID = task.taskID;
		this.codingSets = task.codingSets;
		
		this.rec_svrID = task.rec_svrID;
		this.rec_currentQlen = task.rec_currentQlen;
		this.rec_currentWorkSize = task.rec_currentWorkSize;
		this.rec_currentSvrNum = task.rec_currentSvrNum;
		
		this.rec_inTS = task.rec_inTS;
		this.rec_serveTS = task.rec_serveTS;
		this.rec_outTS = task.rec_outTS;

	}

	@Deprecated
	private void init(double inTS, double serveTS, double outTS, double bit,
			long userID, long taskID, ArrayList<CodingSet> codingSets){
		this.rec_inTS = inTS;
		this.rec_serveTS = serveTS;
		this.rec_outTS = outTS;
		this.origSize = bit;
		this.userID = userID;
		this.taskID = taskID;
		this.codingSets = codingSets;
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
}
