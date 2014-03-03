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
//	public String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_","sintel_trans_trace_"};
//	public String[] videoBaseNameStrings= {"bbb_trans_trace_","ele_trans_trace_"};
	public String[] videoBaseNameStrings= {"bbb_trans_trace_"};
    
//	boolean opt = false; // mark static
	boolean opt = true; // mark lyapunov
//	String prefixString = "lya_";
	String prefixString;
	double avg_interval; // for arrival time 5s
	
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
		avg_interval = 5.0;
		
		if (opt) {
			prefixString = "lya_";
		}else{
			prefixString = "sta_";
		}
		
//		routine_show_singel_static();
		
		if (opt) {
			routine_show_avg_v_lyap();
		}else{
			routine_show_avg_preset_static();
		}
		
	}

	void routine_show_singel_static(){
		double lastTS = 2000.0;
		String pset = "faster";
		double v=100;
		int numServer=2;
		double scale=1;
		
		routine_multiQ_v (v, lastTS, pset, numServer, scale); // do simulation
		String outFileNameString;
		if (opt) {
			outFileNameString = videoBaseNameStrings[0]+"result_single_lyap_"+pset+".m";	
		} else {
			outFileNameString = videoBaseNameStrings[0]+"result_single_static_"+pset+".m";
		}

		m_recorder.outputRecord(outFileNameString, lastTS);
		
		
		cleaning();
	}

	void routine_show_avg_preset_static(){
		prefixString = "sta_";
		double lastTS = 2000.0;
		String psetStrings[] = all_presets;//{"faster"}; // default preset, static scheduling use
		double v=5;
				
		StringBuffer sb_delay = new StringBuffer();
		StringBuffer sb_quality = new StringBuffer();
		StringBuffer sb_qlen = new StringBuffer();
		StringBuffer sb_qbacklog = new StringBuffer();
		
		sb_delay.append(prefixString).append("avg_delay=[");
		sb_quality.append(prefixString).append("avg_quality=[");
		sb_qlen.append(prefixString).append("avg_qlen=[");
		sb_qbacklog.append(prefixString).append("avg_qbacklog=[");
		
		for (int i=0; i<psetStrings.length;i++){
			String pset = psetStrings[i];
			routine_multiQ_v (v, lastTS, pset, 1, 1);
			
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
			String outFileNameString = "result_avg_static.m";
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(outFileNameString)), true);
			pw.println(sb_delay.toString());
			pw.println(sb_quality.toString());
			pw.println(sb_qlen.toString());
			pw.println(sb_qbacklog.toString());
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	void routine_show_avg_v_lyap(){
		prefixString = "lya_";
		String pset = "faster"; // default set
		
		double lastTS = 600.0;
		int min_v = 1, max_v = 100;
		
		StringBuffer sb_delay = new StringBuffer();
		StringBuffer sb_quality = new StringBuffer();
		StringBuffer sb_qlen = new StringBuffer();
		StringBuffer sb_qbacklog = new StringBuffer();
		StringBuffer sb_vIndex = new StringBuffer();
		
		sb_delay.append(prefixString).append("avg_delay=[");
		sb_quality.append(prefixString).append("avg_quality=[");
		sb_qlen.append(prefixString).append("avg_qlen=[");
		sb_qbacklog.append(prefixString).append("avg_qbacklog=[");
		sb_vIndex.append(prefixString).append("v=[");
		
		for (int v=min_v; v<=max_v; v++){
			routine_multiQ_v (v, lastTS, pset, 1, 1);
			sb_vIndex.append(v).append(",");
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
		sb_vIndex.append("];");
		
		try {
			String outFileNameString = "result_avg_lyap.m";	
			PrintWriter pw = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(outFileNameString)), true);
			pw.println(sb_delay.toString());
			pw.println(sb_quality.toString());
			pw.println(sb_qlen.toString());
			pw.println(sb_qbacklog.toString());
			pw.println(sb_vIndex.toString());
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void routine_multiQ_v(double v, double lastTS, String pset, int serverNum, double speedScale){
		
		
		double slot_interval = 0.1; // time slot interval
		
		events = new ListQueue(); // event queue
		
		// manage all servers
		m_cluster = new ClusterManager(serverNum, this, speedScale); 

		if (opt) {
			m_cluster.m_schedulor = new LyapunovSchedulor(this);	
		} else {
			m_cluster.m_schedulor = new BaseSchedulor(this);
		}
		

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