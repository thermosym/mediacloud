package ym.simulation.cloud;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Recorder extends Event{
	SlotLog lastSlotLogObj;
	double lastTS;
	CloudSimulator m_simulator;
	double slot_interval;
	String prefix;
	
	ArrayList<SlotLog> slotLogList = new ArrayList<SlotLog>();
	ArrayList<Task> tasklog = new ArrayList<Task>();
	
	public Recorder(double lastTS, CloudSimulator sim, double slot_interval) {
		super();
		this.lastTS = lastTS;
		this.m_simulator = sim;
		this.slot_interval = slot_interval;
		this.prefix = m_simulator.prefixString;
	}
	public void addLog(Task tsk){
		tasklog.add(tsk);
	}
	
	@Override
	void execute(AbstractSimulator simulator) {		 
		// new slot event log
		lastSlotLogObj = new SlotLog(m_simulator.now(), 
				slot_interval)  ;
		// parallel 
		int parallel_num=0;
		for (Server svr : m_simulator.m_cluster.m_serverVector) {
			if (!svr.isAvailable()){
				parallel_num++;
			}
		}
		lastSlotLogObj.parallel = parallel_num;
		
		// update queue length
		int[] QlenVector = new int[m_simulator.m_cluster.m_serverVector.size()];
		double[] QbacklogVector = new double[m_simulator.m_cluster.m_serverVector.size()];
		for (int i = 0; i < QlenVector.length; i++) {
			Server svr = m_simulator.m_cluster.m_serverVector.get(i);
			int qlen =0;
			qlen += svr.isAvailable() ? 0:1;
			qlen += svr.m_taskQueue.size();
			QlenVector[i] = qlen;
			
			QbacklogVector[i] = svr.getResidualBacklogTime();
		}
		lastSlotLogObj.QlenVector = QlenVector;
		lastSlotLogObj.QbacklogVector = QbacklogVector;
		
		// insert it into the list
		slotLogList.add(lastSlotLogObj);
			
		// schedule next record
        time += slot_interval;
        
        if (time < lastTS) {
        	simulator.insert(this);
        }
	}
	private double getAvgQBacklog(int queueIndex) {
		double avg = 0.0;
		
		for(int i=0; i<slotLogList.size(); i++){
			avg += slotLogList.get(i).QbacklogVector[queueIndex];
		}		
		avg = (slotLogList.size() >0) ? (avg/slotLogList.size()) : 0;  
		
		return avg;
	}
	
	public String getAvgQBacklogArrayString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
			sb.append(getAvgQBacklog(i)).append(",");
		}
		sb.append("];");
		return sb.toString();
	}
	
	private String getAvgQBacklogSingleString(){
		double avg=0;
		StringBuffer sb = new StringBuffer();
		sb.append("avg_QBacklog=");
		for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
			avg += getAvgQBacklog(i);
		}
		avg = avg/m_simulator.m_cluster.m_serverVector.size();
		sb.append(avg).append(";");
		return sb.toString();		
	}
	
	private double getAvgQlen(int queueIndex){
		double avg = 0.0;
		
		for(int i=0; i<slotLogList.size(); i++){
			avg += slotLogList.get(i).QlenVector[queueIndex];
		}		
		avg = (slotLogList.size() >0) ? (avg/slotLogList.size()) : 0;  
		
		return avg;
	}
	
	public String getAvgQlenArrayString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
			sb.append(getAvgQlen(i)).append(",");
		}
		sb.append("];");
		return sb.toString();
	}

	private String getAvgQlenSingleString(){
		double avg=0;
		StringBuffer sb = new StringBuffer();
		sb.append("avg_Qlen=");
		for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
			avg += getAvgQlen(i);
		}
		avg = avg/m_simulator.m_cluster.m_serverVector.size();
		sb.append(avg).append(";");
		return sb.toString();		
	}
	
	private String getSlotQBacklogTrace(int queueIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("slot_QBacklog_"+queueIndex+"=[");
		for (SlotLog log: slotLogList){
			if ( log.time_low+log.time_interval <= lastTS){
				sb.append(log.QbacklogVector[queueIndex]).append(",");
			}
		}
		sb.append("];");
		return sb.toString();
	}
	
	private String getSlotQLenTrace(int queueIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("slot_Qlen_"+queueIndex+"=[");
		for (SlotLog log: slotLogList){
			if ( log.time_low+log.time_interval <= lastTS){
				sb.append(log.QlenVector[queueIndex]).append(",");
			}
		}
		sb.append("];");
		return sb.toString();
	}

	private String getSlotQBacklogAllTrace() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("slot_QBacklogAll=[");
		for (SlotLog log: slotLogList){
			if ( log.time_low+log.time_interval <= lastTS){
				sb.append("[");
				for (int i = 0; i < log.QbacklogVector.length; i++) {
					sb.append(log.QbacklogVector[i]).append(",");	
				}
				sb.append("];");
			}
		}
		sb.append("];");
		return sb.toString();
	}
	
	private String getSlotQLenAllTrace() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("slot_QlenAll=[");
		for (SlotLog log: slotLogList){
			if ( log.time_low+log.time_interval <= lastTS){
				sb.append("[");
				for (int i = 0; i < log.QlenVector.length; i++) {
					sb.append(log.QlenVector[i]).append(",");	
				}
				sb.append("];");
			}
		}
		sb.append("];");
		return sb.toString();
	}

	private String getTaskAvgDelaySingleString(int videoIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("avg_delay_"+videoIndex+"=");
		sb.append(getTskAvgDelay(m_simulator.videoBaseNameStrings[videoIndex])).append(";");
		return sb.toString();
	}


	private String getTaskAvgQualitySingleString(int videoIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("slot_quality_"+videoIndex+"=");
		sb.append(getTskAvgQuality(m_simulator.videoBaseNameStrings[videoIndex])).append(";");
		return sb.toString();
	}


	private String getTaskDelayTrace(int vNameIndex) {
		LinkedList<Task> loglist = getTasklog(m_simulator.videoBaseNameStrings[vNameIndex]);
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("task_delay_"+vNameIndex+"=[");
		for (int i=0; i < loglist.size(); i++){
			Task tskTask = loglist.get(i);
			assert(tskTask.taskID == i);
			sb.append(tskTask.rec_outTS - tskTask.rec_inTS).append(",");
		}
		sb.append("];");
		return sb.toString();
	}
	
	
	private String getTaskQualityTrace(int vNameIndex) {
		LinkedList<Task> loglist = getTasklog(m_simulator.videoBaseNameStrings[vNameIndex]);
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("task_quality_"+vNameIndex+"=[");
		for (int i=0; i < loglist.size(); i++){
			Task tskTask = loglist.get(i);
			assert(tskTask.taskID == i);
			CodingSet cset = tskTask.getCodingResult(tskTask.rec_preset);
			sb.append(cset.outputBitR).append(",");
		}
		sb.append("];");
		return sb.toString();
	}
	
	private LinkedList<Task> getTasklog(String vNameBase) {
		LinkedList<Task> loglist = new LinkedList<Task>();
		
		for (Task task : tasklog) {
			if (task.videoName.equals(vNameBase)) {
				int i=0;
				while(i < loglist.size() && task.taskID > loglist.get(i).taskID){
					i++;
				}
				loglist.add(i, task);
			}
		}
		
		return loglist;
	}
	

	private String getTaskPresetTrace(int vNameIndex) {
		LinkedList<Task> loglist = getTasklog(m_simulator.videoBaseNameStrings[vNameIndex]);
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("task_preset_"+vNameIndex+"=[");
		for (int i=0; i < loglist.size(); i++){
			Task tskTask = loglist.get(i);
			assert(tskTask.taskID == i);
			sb.append(m_simulator.getPresetIndex(tskTask.rec_preset)).append(",");
		}
		sb.append("];");
		return sb.toString();
	}

	public double getTskAvgDelay(String videoName){
		double avg=0.0;
		int number=0;
		for (Task task : tasklog) {
			if (task.videoName.equals(videoName)) {
				avg += task.rec_outTS - task.rec_inTS;
				number++;
			}
		}
		avg = (number>0) ? (avg/number) : 0;
		return avg;
	}
	
	public String getTskAvgDelayArray(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (String videoName : m_simulator.videoBaseNameStrings) {
			sb.append(getTskAvgDelay(videoName)).append(",");	
		}
		sb.append("]");
		return sb.toString();
	}
	
	public double getTskAvgQuality(String videoName){
		double avg=0.0;
		int number=0;
		for (Task task : tasklog) {
			if (task.videoName.equals(videoName)) {
				CodingSet cSet = task.getCodingResult(task.rec_preset);
				avg += cSet.outputBitR;
				number++;
			}
		}
		avg = (number>0) ? (avg/number) : 0;
		return avg;
	}
	
	public String getTskAvgQualityArray(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (String videoName : m_simulator.videoBaseNameStrings) {
			sb.append(getTskAvgQuality(videoName)).append(",");	
		}
		sb.append("]");
		return sb.toString();
	}

	public void init() {
		
		
	}

	public void outputRecord(String outFile, double lastTS) {
		try {
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(outFile)), true);
						
			// print delay trace for each video stream
			for (int i = 0; i < m_simulator.videoBaseNameStrings.length; i++) {
				pw.println(getTaskDelayTrace(i));
			}

			// print quality trace for each video stream
			for (int i = 0; i < m_simulator.videoBaseNameStrings.length; i++) {
				pw.println(getTaskQualityTrace(i));
			}
			
			// print preset configuration for each video stream
			for (int i = 0; i < m_simulator.videoBaseNameStrings.length; i++) {
				pw.println(getTaskPresetTrace(i));
			}
			
			// print all presets
			StringBuffer sb = new StringBuffer().append(prefix).append("all_presets=[");
			for (int i = 0; i < m_simulator.all_presets.length; i++) {
				sb.append("\'").append(m_simulator.all_presets[i]).append("\',");
			}
			sb.append("];");
			pw.println(sb.toString());
			
//			// print time slot--queue length trace
//			for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
//				pw.println(getSlotQLenTrace(i));
//			}
//
//			// print time slot--queue backlog trace
//			for (int i = 0; i < m_simulator.m_cluster.m_serverVector.size(); i++) {
//				pw.println(getSlotQLenTrace(i));
//			}

			pw.println(getSlotQLenAllTrace());
			
			pw.println(getSlotQBacklogAllTrace());
			
			
			//close the file
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void outputRecord_AVG(String outFile, double lastTS2) {
//		try {
//			PrintWriter pw = new PrintWriter(
//					new OutputStreamWriter(new FileOutputStream(outFile)), true);
//			
//			// print avg delay for each video stream
//			for (int i = 0; i < m_simulator.videoBaseNameStrings.length; i++) {
//				pw.println(getTaskAvgDelaySingleString(i));
//			}
//			
//			// print avg quality for each video stream
//			for (int i = 0; i < m_simulator.videoBaseNameStrings.length; i++) {
//				pw.println(getTaskAvgQualitySingleString(i));
//			}
//			
//			// print time average--queue length result
//			pw.println(getAvgQlenSingelString());
////			pw.println(getAvgQlenArrayString());
//			
//			// print time average--queue backlog result
//			pw.println(getAvgQBacklogSingelString());
////			pw.println(getAvgQBacklogArrayString());
//			
//			//close the file
//			pw.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void removeAllData() {
		tasklog.clear();
		slotLogList.clear();
	}

	public void updateArrivalEvent(Task task) {
		lastSlotLogObj.arriveNumber ++;
	}

	public void updateEmitEvent(long m_serverID, Task task) {
		// TODO Auto-generated method stub
		lastSlotLogObj.emittedNumber++;
	}

	public void updateOutEvent(Task taskBeingServed) {
		// TODO Auto-generated method stub
		lastSlotLogObj.serverdNumber++;
	}


}

class SlotLog {
	long arriveNumber;
	long emittedNumber;
	int parallel;
	double[] QbacklogVector;
	int[] QlenVector;
	long serverdNumber;
	double time_interval;
	double time_low;

	
	public SlotLog(double time_low, double time_interval){
		this.time_low = time_low;
		this.time_interval = time_interval;
		
		this.arriveNumber = 0;
		this.serverdNumber = 0;
		this.emittedNumber = 0;
		this.parallel = 0;
	}
}
