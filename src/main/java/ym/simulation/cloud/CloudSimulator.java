package ym.simulation.cloud;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;





public class CloudSimulator extends Simulator {
	
//	Vector<Server> m_serverVector;
	Recorder m_recorder;
	
	ClusterManager m_cluster;
	
//    private String all_presets[]={"ultrafast", "superfast", "veryfast", "faster", "fast", 
//			"medium", "slow", "slower", "veryslow" };
	public String all_presets[]={"superfast", "faster", "slow", "slower"};
	
    
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
		
		String psetStrings[] = {"medium"}; // default preset, static scheduling use
		
		double v=1000;
		for (String pset: psetStrings){
			//run simulation
			routine_multiQ_v (v, lastTS, pset, 1, 0.1);
						
			m_recorder.outputRcord("result_static.m", lastTS);
			cleaning();
		}
	}

	private void routine_multiQ_v(double v, double lastTS, String pset, int serverNum, double speedScale){
		
		double avg_interval = 5.0; // for arrival time 5s
		double slot_interval = 0.1; // time slot interval
		
		events = new ListQueue(); // event queue
		
		// manage all servers
		m_cluster = new ClusterManager(serverNum, this, speedScale); 

		m_cluster.m_schedulor = new BaseSchedulor(this);
//		m_cluster.m_schedulor = new LyapunovSchedulor(this);

		// set parameters; default preset, V 
		m_cluster.m_schedulor.setPreset_default(pset);
		m_cluster.m_schedulor.setV(v);
		
		m_recorder = new Recorder(lastTS,this,slot_interval);  
		
//		String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
		String[] videoBaseNameStrings= {"bbb_trans_trace_"};

		for (int i=0; i<videoBaseNameStrings.length; i++) {
			String videoName = videoBaseNameStrings[i];
			
			// register record and queue			
			Generator generator = new Generator(i, lastTS, avg_interval, all_presets);
			
			generator.m_cm = m_cluster;
			generator.time = 0.0;
			generator.parseTraceTXT(videoName);
			insert(generator);
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

    

	
    
    private String scheduleCodingPreset_lyapunov(Task tskTask) {
		// TODO Auto-generated method stub
		return all_presets[last_preset_index];
	}
    
	// always encode video with static preset


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