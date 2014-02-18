package ym.simulation.cloud;

import java.util.Vector;

/**
* A server that holds a Task for an exponentially distributed amout of time
* and releases it.
*/

public class Server extends Event {
	public Server(long serverID, double speed, CloudSimulator sim, ClusterManager cm) {
		super();
		this.m_serverID = serverID;
		this.m_speedScale = speed;
		
		this.m_simulator = sim;
		this.m_cm = cm;
		
		this.m_taskQueue = new Queue(sim);
	}
    
	private CloudSimulator m_simulator;
    private ClusterManager m_cm;
    
	public long m_serverID;
    public double m_speedScale;
    
    private Task TaskBeingServed;
    public Queue m_taskQueue;
    
    void execute(AbstractSimulator simulator) {

    	TaskBeingServed.rec_outTS = ((Simulator)simulator).now();
    	// update the event
    	m_simulator.m_recorder.updateOutEvent(TaskBeingServed);
    	// store the task log
    	m_simulator.m_recorder.addLog(TaskBeingServed);
    	
        TaskBeingServed = null; // clear the server
        //try to schedule
        Task tskTask = m_cm.getNextjob(this);
        if (tskTask != null) {
			serveTask(m_simulator, tskTask);
		}
    }
    
    boolean isAvailable() {
        return (TaskBeingServed == null);
    }
    
    /**
    * Start a Task's service.
    */
    void serveTask(AbstractSimulator simulator, Task task) {
        if (TaskBeingServed != null) {
            /* Should never reach here */
            System.out.println("Error: I am busy serving someone else");
            return;
        }
        
        TaskBeingServed = task;
        TaskBeingServed.rec_svrID = this.m_serverID;
        TaskBeingServed.rec_serveTS = ((Simulator)simulator).now();
        TaskBeingServed.rec_currentQlen = m_taskQueue.size(); 

        //update the log
        //m_simulator.m_recorder.updateEmitEvent(this.m_serverID, task);
        
        CodingSet cset = task.getCodingResult(task.rec_preset);
        
        double codingTime=0;
        if (cset != null){
        	codingTime = cset.codingTime;
        }else {
			System.err.println("preset "+task.rec_preset+" cannot find in the trace");
		}
        
        time = m_simulator.now() + codingTime/m_speedScale;
        simulator.insert(this);
    }

	public double getResidualTime() {
		double residual_time=0;
		// get the residual time of finish
		if (TaskBeingServed!=null) {
			CodingSet cSet = TaskBeingServed.getCodingResult(TaskBeingServed.rec_preset);
			double codingTime = cSet.codingTime;
			residual_time += codingTime - (m_simulator.now() - TaskBeingServed.rec_serveTS);
		}
		// accumulate all queuing task workload time(default preset)
		residual_time += m_taskQueue.getQueueWorkload();
		
		return residual_time;
	}
}