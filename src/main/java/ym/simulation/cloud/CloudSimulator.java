package ym.simulation.cloud;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;





public class CloudSimulator extends Simulator {
	
//	Vector<Server> m_serverVector;
	Recorder m_recorder;
	
	ClusterManager m_cluster;
	
//    private String all_presets[]={"ultrafast", "superfast", "veryfast", "faster", "fast", 
//			"medium", "slow", "slower", "veryslow" };
	public String all_presets[]={"superfast", "faster", "slow", "slower"};
	public String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
//	public String[] videoBaseNameStrings= {"bbb_trans_trace_"};
    

	public static void main(String[] args) {
//		try {
//			PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("stdout.txt")),true);
//			System.setOut(ps);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		new CloudSimulator().start();
	}


	void start() {
		routine_show_avg_preset_static();
//		routine_show_singel_preset_static();
	}
	void routine_show_singel_preset_static(){
		double lastTS = 600.0;
		String psetStrings[] = {"faster"}; // default preset, static scheduling use
		double v=10;
		for (int i=0; i<psetStrings.length;i++){
			String pset = psetStrings[i];
			routine_multiQ_v (v, lastTS, pset, 2, 1); // do simulation
			m_recorder.outputRecord("result_singtel_static.m", lastTS);
			cleaning();
		}
	}

	void routine_show_avg_preset_static(){
		double lastTS = 600.0;
		String psetStrings[] = all_presets;//{"faster"}; // default preset, static scheduling use
		double v=10;
		
		StringBuffer sb_delay = new StringBuffer();
		StringBuffer sb_quality = new StringBuffer();
		StringBuffer sb_qlen = new StringBuffer();
		StringBuffer sb_qbacklog = new StringBuffer();
		
		sb_delay.append("avg_delay_preset=[");
		sb_quality.append("avg_quality_preset=[");
		sb_qlen.append("avg_qlen_preset=[");
		sb_qbacklog.append("avg_qbacklog_preset=[");
		
		for (int i=0; i<psetStrings.length;i++){
			String pset = psetStrings[i];
			routine_multiQ_v (v, lastTS, pset, 4, 1);
			
			sb_delay.append(m_recorder.getTskAvgDelayArray()).append(";");
			sb_quality.append(m_recorder.getTskAvgQualityArray()).append(";");
			sb_qlen.append(m_recorder.getAvgQlenArrayString()).append(";");
			sb_qbacklog.append(m_recorder.getAvgQBacklogArrayString()).append(";");
			cleaning();
		}
		
		sb_delay.append("];");
		sb_quality.append("];");
		sb_qlen.append("];");
		sb_qbacklog.append("];");
		
		try {
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream("result_avg_static.m")), true);
			pw.println(sb_delay.toString());
			pw.println(sb_quality.toString());
			pw.println(sb_qlen.toString());
			pw.println(sb_qbacklog.toString());
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
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

		for (int i=0; i<videoBaseNameStrings.length; i++) {
			String videoName = videoBaseNameStrings[i];
			
			// register record and queue			
			Generator generator = new Generator(i, lastTS, avg_interval, all_presets);
			
			generator.m_cm = m_cluster;
			generator.m_videoName = videoName;
			generator.time = 0.0;
			generator.parseTraceTXT(videoName);
			insert(generator);
		}
		
		
		m_recorder.init();
		m_recorder.time = 0.0; // recorder event
		insert(m_recorder);
		
		doAllEvents();
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
		m_cluster.clean();
		m_recorder.removeAllData();
	}
}