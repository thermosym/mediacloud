package ym.simulation.cloud;

import java.util.ArrayList;
import java.util.Vector;

abstract class OrderedSet {
	abstract void insert(Comparable x);

	abstract Comparable removeFirst();

	abstract int size();

	abstract Comparable remove(Comparable x);
}

/**
 * Queue used in event system queue
 * @author yangming
 */
class ListQueue extends OrderedSet {
	java.util.Vector elements = new java.util.Vector();

	void insert(Comparable x) {
		int i = 0;
		while (i < elements.size()
				&& ((Comparable) elements.elementAt(i)).lessThan(x)) {
			i++;
		}
		elements.insertElementAt(x, i);
	}

	Comparable removeFirst() {
		if (elements.size() == 0)
			return null;
		Comparable x = (Comparable) elements.firstElement();
		elements.removeElementAt(0);
		return x;
	}

	Comparable remove(Comparable x) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.elementAt(i).equals(x)) {
				Object y = elements.elementAt(i);
				elements.removeElementAt(i);
				return (Comparable) y;
			}
		}
		return null;
	}

	public int size() {
		return elements.size();
	}

}


/**
 * Queue used in buffer task
 * @author yangming
 */
public class Queue {
	int m_svrLimit;

	boolean onlySlotSchedule=false;
	Recorder record;
    /**
    * Use the Java Vector to implement a FIFO queue.
    */
    private java.util.Vector<Task> m_Tasks = new java.util.Vector<Task>();
    private java.util.Vector<Server> m_Vserver = new java.util.Vector<Server>();

	private int minSvrNum;
	private int maxSvrNum;
    /**
    * Add a Task to the queue.
    * If the server is available, 
    * pass the Task on to the scheduler.  
    * Otherwise add the Task to the queue.
    */
    public void insert(AbstractSimulator simulator, Task task) {
    	// insert into queue
    	m_Tasks.addElement(task);
    	// update the log
//    	record.updateArrivalEvent(task);
    	
        schedule(simulator); // schedule the task
    }
    /**
     * schedule the task, when new arrival or finish job
     * @param simulator
     * @param task
     */
    public void schedule(AbstractSimulator simulator){
    	if ( !onlySlotSchedule ) {
//			energySchedule(simulator);
//			baseSchedule(simulator);
//			maxQSchedule(simulator);
    	}
    }
    /*
     * energy efficient schedule: lyapunov algorithm
     */
    public void energySchedule(AbstractSimulator simulator){
		Server idleServer = getIdleServer();
		if (!m_Tasks.isEmpty() && (idleServer != null)) {
			Task task = remove(); // get first element
			idleServer.serveTask(simulator, task);
		}
    }
    
    /*
     * keep the max queue size schedule algorithm
     * 1. when Q > maxQ : increase one VM
     * 2. when Q < maxQ : decrease one VM
     */
    public void maxQSchedule(AbstractSimulator simulator){
    	
    	Server idleServer = getIdleServerLimit(); // under server number limit
		if (!m_Tasks.isEmpty() && (idleServer != null)) {
			Task task = remove(); // get first element
			idleServer.serveTask(simulator, task);
		}
    }
    
    /*
     * The base schedule: use all the VMs 
     */
    public void baseSchedule(AbstractSimulator simulator){
		Server idleServer = getIdleServer();
		if (!m_Tasks.isEmpty() && (idleServer != null)) {
			Task task = remove(); // get first element
			idleServer.serveTask(simulator, task);
		}
    }
    
    /**
    * @return the first Task in the queue
    */
    Task remove() {
        Task Task = (Task) m_Tasks.firstElement();
        m_Tasks.removeElementAt(0);
        return Task;
    }
    
    public int size() {
        return m_Tasks.size();
    }
    
    public void mountServer(Vector<Server> serverVector){
    	this.m_Vserver = serverVector;
    }
    
    public void initSvrLimit(int startNumber){
    	minSvrNum = 1;
    	maxSvrNum = m_Vserver.size();
    	m_svrLimit = (startNumber>maxSvrNum)?maxSvrNum:startNumber;
    }
    
//    public void updateSvrLimit(){
////    	int maxQsize = 6;
////    	int minQsize = 4;
////    	//update the server number limitation
////    	if (m_Tasks.size() < minQsize){
////    		setM_svrLimit(getM_svrLimit()-1); // try to down scale
////    	}else if(m_Tasks.size() > maxQsize){
////    		setM_svrLimit(getM_svrLimit()+1); // try to up scale
////		}
//    	
//    	double maxWsize = 3;
//    	double minWsize = 1;
//    	double taskSize = getWorkSize();
//    	if (taskSize < minWsize){
//    		setM_svrLimit(getM_svrLimit()-1); // try to down scale
//    	}else if(taskSize > maxWsize){
//    		setM_svrLimit(getM_svrLimit()+1); // try to up scale
//		}
//    }
    /**
     * @return server which is idle. If no idle server, then return null.
     */
    public Server getIdleServer(){
    	Server idleS = null;
    	for(Server s:m_Vserver){
    		if (s.isAvailable()){
    			idleS = s;
    			break;
    		}
    	}
    	return idleS;
    }
    
    public Server getIdleServerLimit(){
    	Server idleS = null;
    	for(int i=0; i< getM_svrLimit(); i++){
    		if (m_Vserver.get(i).isAvailable()){
    			idleS = m_Vserver.get(i);
    			break;
    		}
    	}
    	return idleS;
    }
    
    public int getServNum(){
    	int servingNum=0;
    	for (Server s:m_Vserver){
    		if (!s.isAvailable()){
    			servingNum += 1;
    		}
    	}
    	return servingNum;
    }

	public int getM_svrLimit() {
		return m_svrLimit;
	}

	public void setM_svrLimit(int svrLimit) {
		if ( (svrLimit >= minSvrNum) && (svrLimit <= maxSvrNum ) ){
			this.m_svrLimit = svrLimit;
		}
		
	}
	
	public int getWorkingParallel() {
		int para =0;
		for(Server s:m_Vserver){
			if (!s.isAvailable()){
				para++;
			}
		}
		return para;
	}
	public double getWorkSize() {
		// get accumulate work size;
		double workSize=0;
		for (Task tsk:m_Tasks) {
			workSize += tsk.getCodingResult(tsk.rec_preset).codingTime;
		}
		return workSize;
	}
    
}