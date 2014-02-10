package ym.simulation.cloud;

import java.util.Vector;

/**
* A server that holds a Task for an exponentially distributed amout of time
* and releases it.
*/
class Server extends Event {
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
        if (!m_simulator.onlySlotSchedule) {
        	m_simulator.schedule(simulator);
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
        TaskBeingServed.rec_currentQlen = m_simulator.m_queuVector.get(task.queueIndex).size(); 

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
}

class LyapunovSolver{
	int para_V=0;
	double lyapFunc_Z=0;
	double lyapFunc_Q=0;
	double threshold_drift=0;
}

public class CloudSimulator extends Simulator {
	
	boolean onlySlotSchedule=true;
	Vector<Queue> m_queuVector;
	Vector<Server> m_serverVector;
	Recorder m_recorder;
	
    private String all_presets[]={"ultrafast", "superfast", "veryfast", "faster", "fast", 
			"medium", "slow", "slower", "veryslow" };
    private int last_preset_index=6;
    
	private double VQ_z=0;
	
	private double threshold_VQ_z=0;
	private double threshold_VQ_q=2;
	
	LyapunovSolver m_lyaSolver;
	
	public static void main(String[] args) {
		new CloudSimulator().start();
	}


	void start() {
		routine_show_avg_V();
	}
	
	void routine_show_avg_V(){
		int max_v = 1; // max_v value;
		double lastTS = 600.0;
		
//		for (int i=0; i<max_v; i++){
//			routine_multiQ_v (i, lastTS);
//
//			double avg_qlen[] = new double[m_queuVector.size()];
//			double avg_delay[] = new double[m_queuVector.size()];
//
//			for (int j = 0; j < m_queuVector.size(); j++) {
//				avg_qlen[j] = m_recorder.getAvgQlen(j);
//				avg_delay[j] = m_recorder.getTskAvgDelay(j);
//				System.out.println("qlen="+avg_qlen[j]+"; delay="+avg_delay[j]);
//			}
//			
//			m_recorder.outputRcord("result.m", lastTS);
//		}
		
		String psetStrings[] = {"medium"}; //default preset
		int v=1;
		for (String pset: psetStrings){
			//run simulation
			routine_multiQ_v (v, lastTS, pset, 1, 1);
			
			double avg_qlen[] = new double[m_queuVector.size()];
			double avg_delay[] = new double[m_queuVector.size()];

			for (int j = 0; j < m_queuVector.size(); j++) {
				avg_qlen[j] = m_recorder.getAvgQlen(j);
				avg_delay[j] = m_recorder.getTskAvgDelay(j);
				System.out.println("qlen="+avg_qlen[j]+"; delay="+avg_delay[j]);
			}
			
			m_recorder.outputRcord("result_static.m", lastTS);
			cleaning();
		}
	}

	private void routine_multiQ_v(int v, double lastTS, String pset, int serverNum, double speedScale){
		m_lyaSolver = new LyapunovSolver();
		last_preset_index = getPresetIndex(pset);
		
		double avg_interval = 5.0; // for arrival time 5s
		events = new ListQueue(); // event queue
		m_serverVector = new Vector<Server>(); // server array
		m_queuVector = new Vector<Queue>();
		m_recorder = new Recorder(lastTS,this);  
		
//		String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
		String[] videoBaseNameStrings= {"bbb_trans_trace_"};
		
		for (int i = 0; i < serverNum; i++) {
			Server server = new Server(i, this, speedScale);
			m_serverVector.add(server);
		}
		
		for (int i=0; i<videoBaseNameStrings.length; i++) {
			String videoName = videoBaseNameStrings[i];
			Queue queue = new Queue(this); // video segment queue for the video stream
			
			// register record and queue			
			Generator generator = new Generator(i, lastTS, avg_interval);
			generator.queue = queue;
			generator.time = 0.0;
			generator.parseTraceTXT(videoName);
			insert(generator);
			
			m_queuVector.add(queue); // add it in queue vector
		}
		
		
		m_recorder.init();
		m_recorder.time = 0.0; // recorder event
		insert(m_recorder);
		
		doAllEvents();
	}
	
	public void schedule(AbstractSimulator simulator) {
//		LyapunovSchedule(simulator);
		baseSchedule(simulator);
		// maxQSchedule(simulator);
	}

    public void LyapunovSchedule(AbstractSimulator simulator){
		Server idleServer = getIdleServer();
		if (idleServer == null){
			return; // if no idel server, go back, no need to schedule
		}
		
		// get lyapunov function value
		double lyapFunc_Q_now =0;
		
		// find the max Q
		for (Queue que : m_queuVector) {
			lyapFunc_Q_now += 0.5*Math.pow(que.size(), 2);
		}
		
		// drift
		double lyapDrift = lyapFunc_Q_now - m_lyaSolver.lyapFunc_Q;
		
		if (lyapDrift > m_lyaSolver.threshold_drift) {
			// need to dispatch a job to the cloud
			
			Queue maxQueue = null;
			int maxQlen = 0;
			// find the max Q
			for (Queue que : m_queuVector) {
				if ((que.size() > maxQlen)) {
					maxQueue = que;
					maxQlen = que.size();
				}
			}
			
			Task tskTask = maxQueue.remove();
			tskTask.rec_preset = scheduleCodingPreset_lyapunov(tskTask);
			idleServer.serveTask(simulator, tskTask);
		}
    }
    
	public void baseSchedule(AbstractSimulator simulator) {

		Server idleServer = getIdleServer();
		// select a long queue length for dispatching

		Queue maxQueue = null;
		int maxQlen = 0;
		// find the max Q
		for (Queue que : m_queuVector) {
			if ((que.size() > maxQlen)) {
				maxQueue = que;
				maxQlen = que.size();
			}
		}

		if ((idleServer != null) && (maxQueue != null)) {
			// schedule the selected job
			Task tskTask = maxQueue.remove();
			//set coding preset 
//			tskTask.rec_preset = scheduleCodingPreset_baseAdaptive(tskTask);
			tskTask.rec_preset = scheduleCodingPreset_baseStatic(tskTask);
			idleServer.serveTask(simulator, tskTask);
		}
	}
    
    private String scheduleCodingPreset_lyapunov(Task tskTask) {
		// TODO Auto-generated method stub
		return all_presets[last_preset_index];
	}
    
	// always encode video with static preset
    private String scheduleCodingPreset_baseStatic(Task tskTask) {
		return all_presets[last_preset_index];
	}


	private String scheduleCodingPreset_baseAdaptive(Task tskTask) {
    	String presetString = null;

//    	CodingSet cSet = tskTask.codingSets.get(i);
		Queue tskQueue = m_queuVector.get(tskTask.queueIndex);

		if ( (tskQueue.size() > threshold_VQ_q) && (last_preset_index>0) ) {
			last_preset_index--;
		}else {
			if (last_preset_index<8) {
				last_preset_index++;	
			}
		}
		
		presetString = all_presets[last_preset_index];
//		for (int i = tskTask.codingSets.size()-1; i >=0; i--) {
//		}
		return presetString;
	}


	/**
     * @return server which is idle. If no idle server, then return null.
     */
    public Server getIdleServer(){
    	Server idleS = null;
    	for(Server s:m_serverVector){
    		if (s.isAvailable()){
    			idleS = s;
    			break;
    		}
    	}
    	return idleS;
    }
    
    public int getPresetIndex(String preset){
    	int psetIndex = 0;
    	for (int i = 0; i < all_presets.length; i++) {
			if (all_presets[i].equals(preset)) {
				psetIndex = i;
				break;
			}
		}
    	return psetIndex;
    }
    
	private void cleaning() {
		// TODO Auto-generated method stub
		m_queuVector.removeAllElements();
		m_serverVector.removeAllElements();
		m_recorder.removeAllData();
	}
}