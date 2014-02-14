package ym.simulation.cloud;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

class SlotLog {
	double time_low;
	double time_interval;
	int[] QlenVector;
	double Zlen;
	long arriveNumber;
	long serverdNumber;
	long emittedNumber;
	int parallel;

	
	public SlotLog(double time_low, double time_interval){
		this.time_low = time_low;
		this.time_interval = time_interval;
		
		this.arriveNumber = 0;
		this.serverdNumber = 0;
		this.emittedNumber = 0;
		this.parallel = 0;
	}
}

public class Recorder extends Event{
	public Recorder(double lastTS, CloudSimulator sim, double slot_interval) {
		super();
		this.lastTS = lastTS;
		this.m_simulator = sim;
		this.slot_interval = slot_interval;
	}
	CloudSimulator m_simulator;
	double slot_interval;
	double lastTS;
	

	ArrayList<Task> tasklog = new ArrayList<Task>();
	ArrayList<SlotLog> slotLogList = new ArrayList<SlotLog>();
	
	SlotLog lastSlotLogObj;
	@Override
	void execute(AbstractSimulator simulator) {		 
		// new slot event log
		lastSlotLogObj = new SlotLog(m_simulator.now(), 
				slot_interval)  ;
		// parallel 
		int parallel_num=0;
		for (Server svr : m_simulator.m_serverVector) {
			if (!svr.isAvailable()){
				parallel_num++;
			}
		}
		lastSlotLogObj.parallel = parallel_num;
		
		// update queue length
		int[] QlenVector = new int[m_simulator.m_queueVector.size()];
		for (int i = 0; i < QlenVector.length; i++) {
			QlenVector[i] = m_simulator.m_queueVector.get(i).size();
		}
		lastSlotLogObj.QlenVector = QlenVector;

		//update Zlen
		lastSlotLogObj.Zlen = m_simulator.m_lyaSolver.vqueue_Z; 
				
		// insert it into the list
		slotLogList.add(lastSlotLogObj);
		
		// in every slot it tries to schedule the job
		m_simulator.schedule(simulator);
		
		// schedule next record
        time += slot_interval;
        if (time < lastTS) simulator.insert(this);
	}
	
	public double getAvgQlen(int queueIndex){
		double avg = 0.0;
		
		for(int i=0; i<slotLogList.size(); i++){
			avg += slotLogList.get(i).QlenVector[queueIndex];
		}		
		avg = (slotLogList.size() >0) ? (avg/slotLogList.size()) : 0;  
		
		return avg;
	}
	
	public double getTskAvgDelay(int queueIndex){
		double avg=0.0;
		for (Task tskTask : tasklog) {
			if (tskTask.queueIndex == queueIndex){
				avg += tskTask.rec_outTS - tskTask.rec_inTS;	
			}
		}
		avg = (tasklog.size()>0) ? (avg/tasklog.size()) : 0;
		return avg;
	}

	public void addLog(Task tsk){
		tasklog.add(tsk);
	}
	
	public void outputRcord(String outFile, double lastTS) {
		try {
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(outFile)), true);
						
			// print delay trace
			for (int i = 0; i < m_simulator.m_queueVector.size(); i++) {
				pw.println(getTaskDelayTrace(i));
			}
			
			// print preset
			for (int i = 0; i < m_simulator.m_queueVector.size(); i++) {
				pw.println(getTaskPresetTrace(i));
			}
			
			// print time slot--queue length trace
			for (int i = 0; i < m_simulator.m_queueVector.size(); i++) {
				pw.println(getSlotQLenTrace(i));
			}
			
			// print time slot--virtual queue length trace
			pw.println(getSlotZLenTrace());
			
			//close the file
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	

	private String getSlotZLenTrace() {
		StringBuffer sb = new StringBuffer();
		sb.append("slot_Zlen=[");
		for (SlotLog log: slotLogList){
			if ( log.time_low+log.time_interval <= lastTS){
				sb.append(log.Zlen).append(",");
			}
		}
		sb.append("];");
		return sb.toString();
	}

	private String getTaskDelayTrace(int queueIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("task_delay_"+queueIndex+"=[");
		for (Task tskTask: tasklog){
			if ( (tskTask.queueIndex == queueIndex) && (tskTask.rec_outTS <= lastTS)){
				sb.append(tskTask.rec_outTS - tskTask.rec_inTS).append(",");
			} 
		}
		sb.append("];");
		return sb.toString();
	}

	private String getTaskPresetTrace(int queueIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("task_preset_"+queueIndex+"=[");
		for (Task tskTask: tasklog){
			if ( (tskTask.queueIndex == queueIndex) && (tskTask.rec_outTS <= lastTS)){
				sb.append(m_simulator.getPresetIndex(tskTask.rec_preset)).append(",");
			}
		}
		sb.append("];");
		return sb.toString();
	}

	public void updateArrivalEvent(Task task) {
		lastSlotLogObj.arriveNumber ++;
	}
	
	public void updateOutEvent(Task taskBeingServed) {
		// TODO Auto-generated method stub
		lastSlotLogObj.serverdNumber++;
	}

	public void updateEmitEvent(long m_serverID, Task task) {
		// TODO Auto-generated method stub
		lastSlotLogObj.emittedNumber++;
	}

	public void init() {
		
		
	}

	public void removeAllData() {
		tasklog.clear();
		slotLogList.clear();
	}
}
