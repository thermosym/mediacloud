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
	long idx_task_low;
	long idx_task_high;
	double time_low;
	double time_interval;
	long Qlen;
	long arriveNumber;
	long serverdNumber;
	long emittedNumber;
	int parallel;
	int parallel_real;
	
	public SlotLog(double time_low, double time_interval, long Qlen){
		this.time_low = time_low;
		this.time_interval = time_interval;
		this.Qlen = Qlen;
		
		this.idx_task_low = -1;
		this.idx_task_high = -1;
		this.arriveNumber = 0;
		this.serverdNumber = 0;
		this.emittedNumber = 0;
		this.parallel = 0;
		this.parallel_real = 0;
	}
}

public class Recorder extends Event{
	public Recorder(double lastTS) {
		super();
		this.lastTS = lastTS;
	}

	double lastTS;
	double interval_qlencheck=1.0;
	Vector<Queue> queueVector = new Vector<Queue>();
		
	ArrayList<Task> tasklog = new ArrayList<Task>();
	ArrayList<SlotLog> slotLog = new ArrayList<SlotLog>();
	
	SlotLog lastSlotLogObj;
	@Override
	void execute(AbstractSimulator simulator) {
		 
//		// new slot event log
//		lastSlotLogObj =
//			new SlotLog(
//				((Simulator)simulator).now(), 
//				interval_qlencheck, 
//				queue.size()+queue.getServNum())  ;
//		// update server limit
////		queue.updateSvrLimit();
//		lastSlotLogObj.parallel = queue.getM_svrLimit();
//		lastSlotLogObj.parallel_real = queue.getWorkingParallel();
//		slotLog.add( lastSlotLogObj );
		
		// schedule next record
        time += interval_qlencheck;
        if (time < lastTS) simulator.insert(this);
	}
	
	/*
	 * get average arrival rate for the last interval time
	 */
	public double getAvgArrivalRate(double interval){
		double rate=0;
		
		return rate;
	}
	
	public double getAvgQlen(){
		double avg = 0.0;
		
		for(int i=0; i<slotLog.size(); i++){
			avg += slotLog.get(i).Qlen;
		}		
		avg = (slotLog.size() >0) ? (avg/slotLog.size()) : 0;  
		
		return avg;
	}

	public void addLog(Task tsk){
		tasklog.add(tsk);
	}
	
	public double getLogAvgDelay(){
		double avg=0.0;
		
		for (int i=0; i<tasklog.size(); i++){
			double alldelay = tasklog.get(i).rec_outTS - tasklog.get(i).rec_inTS; 
			avg += alldelay;
		}
		avg = (tasklog.size()>0) ? (avg/tasklog.size()) : 0;
		return avg;
	}
	
	public void outputRcord(String outFile, double lastTS) {
		try {
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(outFile)), true);
			
			pw.print("index_delay=[");
			for (int i=0; i<tasklog.size(); i++){
				double index_delay = tasklog.get(i).rec_inTS; 
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(index_delay+",");
				}else{
					break;
				}
				
			}
			pw.print("];");
			pw.println();
			
			pw.print("task_delay=[");
			for (int i=0; i<tasklog.size(); i++){
				double delay = tasklog.get(i).rec_outTS - tasklog.get(i).rec_inTS; 
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(delay+",");
				}else{
					break;
				}
				
			}
			pw.print("];");
			pw.println();
			
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

			pw.print("task_svrsize=[");
			for (int i=0; i<tasklog.size(); i++){
				if (tasklog.get(i).rec_outTS <= lastTS){
					pw.print(tasklog.get(i).rec_currentSvrNum+",");
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
			
			pw.print("task_Sqlen=[");
			for (int i=0; i<slotLog.size(); i++){
				long qlen = slotLog.get(i).Qlen;
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(qlen+",");
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
			
			pw.print("task_Sqlen=[");
			for (int i=0; i<slotLog.size(); i++){
				long qlen = slotLog.get(i).Qlen;
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(qlen+",");
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
			
			pw.print("task_Spara=[");
			for (int i=0; i<slotLog.size(); i++){
				if (slotLog.get(i).time_low <= lastTS){
					pw.print(slotLog.get(i).parallel_real+",");
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
			if (lastSlotLogObj.idx_task_low == -1) {
				lastSlotLogObj.idx_task_low = task.taskID;
				lastSlotLogObj.idx_task_high = task.taskID;
			}else{
				lastSlotLogObj.idx_task_low = Math.min(task.taskID, lastSlotLogObj.idx_task_low);
				lastSlotLogObj.idx_task_high = Math.max(task.taskID, lastSlotLogObj.idx_task_high);
			}
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

	public void addQueueListen(Queue queue) {
		// TODO Auto-generated method stub
		queueVector.add(queue);
	}
}
