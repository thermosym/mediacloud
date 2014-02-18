package ym.simulation.cloud;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;



class LyapunovSolver{
	int para_V=0;
	double lyapFunc_Z=0;
	double lyapFunc_Q=0;
	double threshold_drift=0;
	double vqueue_Z = 0;
}

public class CloudSimulator extends Simulator {
	
//	boolean onlySlotSchedule=true;
	Vector<Queue> m_queueVector;
	
//	Vector<Server> m_serverVector;
	Recorder m_recorder;
	
	ClusterManager m_cluster;
	
//    private String all_presets[]={"ultrafast", "superfast", "veryfast", "faster", "fast", 
//			"medium", "slow", "slower", "veryslow" };
	private String all_presets[]={"superfast", "faster", "slow", "slower"};
	
    private int last_preset_index=6;
    
	private double threshold_VQ_q=2;
	
	LyapunovSolver m_lyaSolver;
	
	public static void main(String[] args) {
		try {
			PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("stdout.txt")),true);
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		int v=10000;
		for (String pset: psetStrings){
			//run simulation
			routine_multiQ_v (v, lastTS, pset, 1, 0.1);
			
			double avg_qlen[] = new double[m_queueVector.size()];
			double avg_delay[] = new double[m_queueVector.size()];

			for (int j = 0; j < m_queueVector.size(); j++) {
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
		m_lyaSolver.para_V = v;
		last_preset_index = getPresetIndex(pset);
		
		double avg_interval = 5.0; // for arrival time 5s
		double slot_interval = 0.1; // time slot interval
		events = new ListQueue(); // event queue
		m_cluster = new ClusterManager(serverNum, this, speedScale); 
		
		m_queueVector = new Vector<Queue>();
		m_recorder = new Recorder(lastTS,this,slot_interval);  
		
//		String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
		String[] videoBaseNameStrings= {"bbb_trans_trace_"};
		
		
		
		for (int i=0; i<videoBaseNameStrings.length; i++) {
			String videoName = videoBaseNameStrings[i];
			Queue queue = new Queue(this); // video segment queue for the video stream
			
			// register record and queue			
			Generator generator = new Generator(i, lastTS, avg_interval, all_presets);
			
			generator.queue = queue;
			generator.time = 0.0;
			generator.parseTraceTXT(videoName);
			insert(generator);
			
			m_queueVector.add(queue); // add it in queue vector
		}
		
		
		m_recorder.init();
		m_recorder.time = 0.0; // recorder event
		insert(m_recorder);
		
		doAllEvents();
	}
	
	public void schedule(AbstractSimulator simulator) {
		LyapunovSchedule(simulator);
//		baseSchedule(simulator);
		// maxQSchedule(simulator);
	}

    public void LyapunovSchedule(AbstractSimulator simulator){
    	// update the lyapunov virtual queue, even no schedule
    	m_lyaSolver.vqueue_Z = m_cluster.getResidualWorkTime();
		
		// drift
		double D=0;
		double base_drift = m_lyaSolver.para_V * D + m_lyaSolver.vqueue_Z*(0-m_cluster.getECtime()) ;
		double min_drift = Double.MAX_VALUE;
		int min_drift_i=0;
		int min_drift_j=0;
		double[][] func = new double[m_queueVector.size()][all_presets.length];
		
		for (int i = 0; i < m_queueVector.size(); i++) {
			if (m_queueVector.get(i).size() > 0) {
				// this queue has job
				for (int j = 0; j < all_presets.length; j++) {
					Task job = m_queueVector.get(i).getHead();
					CodingSet codingSet = job.codingSets.get(j);
					D = (codingSet.outputBitR - job.getMinBitrate())/job.getMinBitrate();
					// the function of drift
					double vd = m_lyaSolver.para_V *1* D/100.0;
					double qsize = m_queueVector.get(i).size();
					double zsize = m_lyaSolver.vqueue_Z;
					double ct = codingSet.codingTime;
//					double max_et = m_recorder.slot_interval * m_cluster.m_serverVector.size();
					double max_et = m_cluster.getECtime();
					func[i][j] = vd - qsize +zsize*(ct - max_et);  

					if (func[i][j] < min_drift) {
						min_drift = func[i][j]; 
						min_drift_i = i;
						min_drift_j = j;
					}
					
					System.out.println("jq-"+i+"--set="+j+", D="+D+", q="+qsize+", z="+zsize+", ct="+ct+", et="+max_et);
				}
			}
		}
		
		// find the optimal strategy and schedule it
		Queue selectedQueue = m_queueVector.get(min_drift_i);
		if (selectedQueue.size() > 0) {
			Task tskTask = selectedQueue.remove();
			tskTask.rec_preset = all_presets[min_drift_j];
			m_cluster.serveTask(tskTask);
			
			// print the func
			System.out.print("t="+now()+"|| preset="+min_drift_j +" || ");
			for (double[] es : func) {
				for (double e : es) {
					System.out.print(e+",");
				}
			}
			System.out.println();
		}

    }

	public void baseSchedule(AbstractSimulator simulator) {

		Server idleServer = m_cluster.getIdleServer();
		// select a long queue length for dispatching

		Queue maxQueue = null;
		int maxQlen = 0;
		// find the max Q
		for (Queue que : m_queueVector) {
			if ((que.size() > maxQlen)) {
				maxQueue = que;
				maxQlen = que.size();
			}
		}

		if ((idleServer != null) && (maxQueue != null)) {
			// schedule the selected job
			Task tskTask = maxQueue.remove();

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
		Queue tskQueue = m_queueVector.get(tskTask.queueIndex);

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
		m_queueVector.removeAllElements();
		m_cluster.clean();
		m_recorder.removeAllData();
	}
}