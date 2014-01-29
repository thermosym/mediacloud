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
	public Recorder(double lastTS, CloudSimulator sim) {
		super();
		this.lastTS = lastTS;
		this.m_simulator = sim;
	}
	CloudSimulator m_simulator;
	
	double lastTS;
	double interval_qlencheck=1.0;

	ArrayList<Task> tasklog = new ArrayList<Task>();
	ArrayList<SlotLog> slotLog = new ArrayList<SlotLog>();
	
	SlotLog lastSlotLogObj;
	@Override
	void execute(AbstractSimulator simulator) {		 
		// new slot event log
		lastSlotLogObj = new SlotLog(m_simulator.now(), 
				interval_qlencheck)  ;
		// parallel 
		int parallel_num=0;
		for (Server svr : m_simulator.m_serverVector) {
			if (!svr.isAvailable()){
				parallel_num++;
			}
		}
		lastSlotLogObj.parallel = parallel_num;
		
		// update queue length
		int[] QlenVector = new int[m_simulator.m_queuVector.size()];
		for (int i = 0; i < QlenVector.length; i++) {
			QlenVector[i] = m_simulator.m_queuVector.get(i).size();
		}
		lastSlotLogObj.QlenVector = QlenVector;

		// insert it into the list
		slotLog.add(lastSlotLogObj);
		
		// schedule next record
        time += interval_qlencheck;
        if (time < lastTS) simulator.insert(this);
	}
	
	public double getAvgQlen(int queueIndex){
		double avg = 0.0;
		
		for(int i=0; i<slotLog.size(); i++){
			avg += slotLog.get(i).QlenVector[queueIndex];
		}		
		avg = (slotLog.size() >0) ? (avg/slotLog.size()) : 0;  
		
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
			for (int i = 0; i < m_simulator.m_queuVector.size(); i++) {
				pw.println(getTaskDelayTrace(i));
			}
			
			pw.print("index_qlen=[");
			for (int i=0; i<tasklog.size(); i++){
				double index_qlen = tasklog.get(i).rec_inTS;
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(index_qlen+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			
			pw.print("task_qlen=[");
			for (int i=0; i<tasklog.size(); i++){
				long qlen = tasklog.get(i).rec_currentQlen;
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(qlen+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			
			/*
			 * some thing
			 */
			pw.print("task_wsize=[");
			for (int i=0; i<tasklog.size(); i++){
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(tasklog.get(i).rec_currentWorkSize+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();

			
			pw.print("index_Sqlen=[");
			for (int i=0; i<slotLog.size(); i++){
				double index_qlen = slotLog.get(i).time_low;
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(index_qlen+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			

			
			pw.print("index_Sqlen=[");
			for (int i=0; i<slotLog.size(); i++){
				double index_qlen = slotLog.get(i).time_low;
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(index_qlen+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();

			
			pw.print("index_Sarrival=[");
			for (int i=0; i<slotLog.size(); i++){
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(slotLog.get(i).time_low+slotLog.get(i).time_interval+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			
			pw.print("task_Sarrival=[");
			for (int i=0; i<slotLog.size(); i++){
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(slotLog.get(i).arriveNumber+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			
			pw.print("index_Slimit=[");
			for (int i=0; i<slotLog.size(); i++){
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(slotLog.get(i).time_low+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();
			
			pw.print("task_Slimit=[");
			for (int i=0; i<slotLog.size(); i++){
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(slotLog.get(i).parallel+",");
				}else{
					break;
				}
			}
			pw.print("];");
			pw.println();


			
			//close the file
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getTaskDelayTrace(int queueIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append("task_delay=[");
		for (Task tskTask: tasklog){
			if ( (tskTask.queueIndex == queueIndex) && (tskTask.rec_outTS <= lastTS)){
				sb.append(tskTask.rec_outTS - tskTask.rec_inTS).append(",");
			} 
		}
		sb.append("];");
		return sb.toString();
	}

	public void updateArrivalEvent(Task task) {
		// try to find the index of task
		int index = indexOfTask(task);
		
		if ( index != -1) {
			// log has this task
			System.out.println("error! the task is exist!");
		} else {
			tasklog.add(task);
			// update slot log
			lastSlotLogObj.arriveNumber ++;
		}
	}
	
	
	private int indexOfTask(Task task) {
		int index=-1;
		for (int i=tasklog.size()-1; i>=0; i--){
			if (tasklog.get(i) == task){
				index = i;
				break;
			}
		}
		if (index !=-1){
			System.out.println("taskID="+task.taskID);
		}
		return index;
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
}
