package ym.simulation.cloud;

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
		double lastTS = 100.0;
		double avg_interval = 3.0; // for arrival time
		double avg_joblen = 1.0; // for service time
		events = new ListQueue();

		/* Create the generator, queue, and simulator */
		
		Queue queue = new Queue();
		
		Recorder record = new Recorder(lastTS);
		record.queue = queue;
		queue.record = record;

		for (int i = 0; i < 4; i++) {
			Server server = new Server(i);
			server.record = record;
			queue.mountServer(server);
		}
		queue.initSvrLimit(1);

		/* Start the generator by creating one Task immediately */
		Generator generator = new Generator(lastTS, avg_interval);
		generator.queue = queue;
		generator.time = 0.0;
		generator.parseTrace("video.xml");
		insert(generator);

//		Generator generator2 = new Generator(lastTS, avg_interval*2, avg_joblen*2);
//		generator2.queue = queue;
//		generator2.time = lastTS/2;
//		insert(generator2);
//		
//		Generator generator3 = new Generator(lastTS, avg_interval, avg_joblen);
//		generator3.queue = queue;
//		generator3.time = lastTS/2;
//		insert(generator3);

		record.time = 0.0; // recorder event
		insert(record);
		
		doAllEvents();
		
		System.out.println("AVG-QLen="+record.getAvgQlen()
						+ "  LOG-Avg-Delay=" + record.getLogAvgDelay() );
		record.outputRcord("queueData.m",lastTS);
	}
}