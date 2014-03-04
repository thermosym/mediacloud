package ym.simulation.cloud;

import java.util.ArrayList;

public class Schedulor {
	protected CloudSimulator m_simulator;
	protected String m_preset_default;
	protected double V=0;
	

	public Schedulor(CloudSimulator m_simulator){
		this.m_simulator = m_simulator;
	}
	public String SchedulePreset(Server svr, Task tsk) {
		return m_preset_default;
	}

	public String getPreset_default() {
		return m_preset_default;
	}
	public void setPreset_default(String preset) {
		m_preset_default = preset;
	}
	
	public double getV() {
		return V;
	}
	public void setV(double v) {
		V = v;
	}

}

class BaseSchedulor extends Schedulor{
	public BaseSchedulor(CloudSimulator m_simulator) {
		super(m_simulator);
	}
	public String SchedulePreset(Server svr, Task tsk) {
		return getCodingPreset_baseStatic(tsk);
	}
	
    private String getCodingPreset_baseStatic(Task tskTask) {
		return m_preset_default;
	}

	private String getCodingPreset_baseAdaptive(Task tskTask) {
		//TODO: adaptive scheduling for static
    	String presetString = m_preset_default;
		return presetString;
	}
}

class LyapunovSchedulor extends Schedulor{
	double lyapFunc_Z=0;
	double lyapFunc_Q=0;
	double threshold_drift=0;
	double vqueue_Z = 0;
	
	public LyapunovSchedulor(CloudSimulator m_simulator){
		super(m_simulator);		
	}
	
	public String SchedulePreset(Server svr, Task tsk){
		// min-drift_penalty = V*D-Q*C
		double F_min = Double.MAX_VALUE;
		int min_index =- 1;
		double cT_default = tsk.getCodingResult(m_preset_default).codingTime;
		double F_v[] = new double[ tsk.codingSets.size()];
		for (int i = 0; i < tsk.codingSets.size(); i++) {
			CodingSet cSet = tsk.codingSets.get(i);
			double cT = svr.CPUCodingTime(cSet.codingTime);
			double D = cSet.outputBitR / 1000.0;
			double F_temp = V*D - (svr.getResidualBacklogTime()/m_simulator.m_recorder.slot_interval)*(cT_default/cT);
			F_v[i] = F_temp;
			if (F_temp < F_min) {
				F_min = F_temp;
				min_index = i;
			}
		}
//		System.out.printf("tsk=%d, min_index=%d, min_bitrate=%f, ",tsk.taskID,min_index,tsk.codingSets.get(min_index).outputBitR );
//		System.out.println(getCodingbtrString(tsk.codingSets)+"  "+getFvalueString(F_v));
		
		
		return tsk.codingSets.get(min_index).preset;
//    	// update the lyapunov virtual queue, even no schedule
//    	vqueue_Z = svr.getResidualTime();
//		
//		// drift
//		double D=0;
//		double base_drift = m_lyaSolver.para_V * D + m_lyaSolver.vqueue_Z*(0-m_cluster.getECtime()) ;
//		double min_drift = Double.MAX_VALUE;
//		int min_drift_i=0;
//		int min_drift_j=0;
//		double[][] func = new double[m_queueVector.size()][all_presets.length];
//		
//		for (int i = 0; i < m_queueVector.size(); i++) {
//			if (m_queueVector.get(i).size() > 0) {
//				// this queue has job
//				for (int j = 0; j < all_presets.length; j++) {
//					Task job = m_queueVector.get(i).getHead();
//					CodingSet codingSet = job.codingSets.get(j);
//					D = (codingSet.outputBitR - job.getMinBitrate())/job.getMinBitrate();
//					// the function of drift
//					double vd = m_lyaSolver.para_V *1* D/100.0;
//					double qsize = m_queueVector.get(i).size();
//					double zsize = m_lyaSolver.vqueue_Z;
//					double ct = codingSet.codingTime;
////					double max_et = m_recorder.slot_interval * m_cluster.m_serverVector.size();
//					double max_et = m_cluster.getECtime();
//					func[i][j] = vd - qsize +zsize*(ct - max_et);  
//
//					if (func[i][j] < min_drift) {
//						min_drift = func[i][j]; 
//						min_drift_i = i;
//						min_drift_j = j;
//					}
//					
//					System.out.println("jq-"+i+"--set="+j+", D="+D+", q="+qsize+", z="+zsize+", ct="+ct+", et="+max_et);
//				}
//			}
//		}
//		
//		// find the optimal strategy and schedule it
//		Queue selectedQueue = m_queueVector.get(min_drift_i);
//		if (selectedQueue.size() > 0) {
//			Task tskTask = selectedQueue.remove();
//			tskTask.rec_preset = all_presets[min_drift_j];
//			m_cluster.serveTask(tskTask);
//			
//			// print the func
//			System.out.print("t="+now()+"|| preset="+min_drift_j +" || ");
//			for (double[] es : func) {
//				for (double e : es) {
//					System.out.print(e+",");
//				}
//			}
//			System.out.println();
//		}
//
    }

	private String getFvalueString(double[] f_v) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("FV=[");
		for (int i = 0; i < f_v.length; i++) {
			sb.append(f_v[i]).append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	private String getCodingbtrString(ArrayList<CodingSet> codingSets) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("Br=[");
		for (int i = 0; i < codingSets.size(); i++) {
			CodingSet csSet = codingSets.get(i);
			sb.append(csSet.outputBitR).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}