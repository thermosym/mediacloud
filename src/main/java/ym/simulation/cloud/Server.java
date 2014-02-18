package ym.simulation.cloud;

/**
* A server that holds a Task for an exponentially distributed amout of time
* and releases it.
*/

public class Server extends Event {
	public Server(long serverID, CloudSimulator sim, double speed) {
		super();
		this.m_serverID = serverID;
		this.m_simulator = sim;
		this.m_speedScale = speed;
	}

	long m_serverID;
    private Task TaskBeingServed;
    private CloudSimulator m_simulator;
    public double m_speedScale;

    
    /**
    * The Task's service is completed so print a message.
    * If the queue is not empty, get the next Task.
    */
    void execute(AbstractSimulator simulator) {

    	TaskBeingServed.rec_outTS = ((Simulator)simulator).now();
    	// update the event
    	m_simulator.m_recorder.updateOutEvent(TaskBeingServed);
    	// store the task log
    	m_simulator.m_recorder.addLog(TaskBeingServed);
    	
        TaskBeingServed = null; // clear the server
        //try to schedule
//        if (!m_simulator.onlySlotSchedule) {
//        	m_simulator.schedule(simulator);
//        }
        Task tskTask = m_simulator.m_cluster.getNextjob();
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
        TaskBeingServed.rec_currentQlen = m_simulator.m_queueVector.get(task.queueIndex).size(); 

        //update the log
        m_simulator.m_recorder.updateEmitEvent(this.m_serverID, task);
        
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
			int pset_index =  m_simulator.getPresetIndex(TaskBeingServed.rec_preset);
			double codingTime = TaskBeingServed.codingSets.get(pset_index).codingTime;
			residual_time = codingTime - (m_simulator.now() - TaskBeingServed.rec_serveTS);
		}else{
			residual_time = 0;
		}
		return residual_time;
	}
}