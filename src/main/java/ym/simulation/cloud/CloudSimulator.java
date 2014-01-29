package ym.simulation.cloud;

import java.util.ArrayList;
import java.util.Vector;

/**
* A server that holds a Task for an exponentially distributed amout of time
* and releases it.
*/
class Server extends Event {
	public Server(long serverID) {
		super();
		this.m_serverID = serverID;
	}

	long m_serverID;
	
    private Task TaskBeingServed;
    Queue queue;
    Recorder record;
    
    /**
    * The Task's service is completed so print a message.
    * If the queue is not empty, get the next Task.
    */
    void execute(AbstractSimulator simulator) {
//        System.out.println("Server-"+ m_serverID +" served task-" 
//        		+ TaskBeingServed.taskID + " at time=" + time 
//        		+ " wating_time = "+ (time - TaskBeingServed.inTS) 
//        		);
    	TaskBeingServed.rec_outTS = ((Simulator)simulator).now();
    	// update the log
    	record.updateOutEvent(TaskBeingServed);
    	
        TaskBeingServed = null; // clear the server
        if (queue.size()>0) {
        	queue.schedule(simulator);
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
        TaskBeingServed.rec_currentQlen = queue.size(); //TODO: current queue length info
        TaskBeingServed.rec_currentWorkSize = queue.getWorkSize();
        TaskBeingServed.rec_currentSvrNum = queue.getWorkingParallel();
        //update the log
        record.updateEmitEvent(this.m_serverID, task);
        
        CodingSet cset = task.getCodingResult(task.rec_preset);
        double codingTime=0;
        if (cset != null){
        	codingTime = cset.codingTime;
        }else {
			System.err.println("preset "+task.rec_preset+" cannot find in the trace");
		}
        
        
        time = ((Simulator)simulator).now() + codingTime;
        simulator.insert(this);
    }
}

public class CloudSimulator extends Simulator {
	
	public static void main(String[] args) {
		new CloudSimulator().start();
	}

	void start() {
		routine_show_avg_V();
	}
	
	void routine_show_avg_V(){
		int max_v = 50; // max_v value;
		double lastTS = 1000.0;
		
		for (int i=0; i<max_v; i++){
//			Recorder rc = routine_test_v (i, lastTS);
			Recorder rc = routine_multiQ_v (i, lastTS);
//			System.out.println("AVG-QLen="+rc.getAvgQlen()
//					+ "  LOG-Avg-Delay=" + rc.getLogAvgDelay() );
//			rc.outputRcord("queueData.m",lastTS);
			double avg_qlen = rc.getAvgQlen();
			double avg_delay = rc.getLogAvgDelay();
		}
	}
	
	Recorder routine_multiQ_v (int v, double lastTS){
		int serverNum = 1;
		double avg_interval = 5.0; // for arrival time 5s
		events = new ListQueue(); // event queue
		
		Recorder record = new Recorder(lastTS); //TODO: new recorder class 
		
		String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
		Vector<Server> serverVector = new Vector<Server>(); // server array
		
		for (int i = 0; i < serverNum; i++) {
			Server server = new Server(i);
			serverVector.add(server);
			server.record = record;
		}
		
		
		for (String videoName : videoBaseNameStrings) {
			Queue queue = new Queue(); // video segment queue for the video stream
			// register record and queue
			record.addQueueListen(queue);
			queue.record = record;
			queue.mountServer(serverVector); //TODO: need register queue in the server obj?
			
			Generator generator = new Generator(lastTS, avg_interval);
			generator.queue = queue;
			generator.time = 0.0;
			generator.parseTraceTXT(videoName);
			insert(generator);
		}
		
		

		record.time = 0.0; // recorder event
		insert(record);
		
		doAllEvents();

		return record;
	}
	
}