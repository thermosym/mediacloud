package ym.simulation.cloud;

import java.util.Vector;

public class ClusterManager {
	CloudSimulator m_simulator;
	Vector<Server> m_serverVector;
	Schedulor m_schedulor;
	
	public ClusterManager(int serverNum, CloudSimulator simulator, double speedScale){
		
		m_simulator = simulator;
		m_serverVector = new Vector<Server>(); // server array
		
		
		for (int i = 0; i < serverNum; i++) {
			Server server = new Server(i, speedScale, simulator, this);
			m_serverVector.add(server);
		}
	}
	
//	public double getResidualWorkTime(){
//    	double residual_time = 0;
//    	// residual time on servers
//    	for (Server svr : m_serverVector) {
//			residual_time += svr.getResidualTime();
//		}
//    	// residual time on buffer queue
//    	for (Task tskTask : m_bufferTask) {
//			residual_time += tskTask.getCodingResult(tskTask.rec_preset).codingTime;
//		}
//    	
//    	return residual_time/m_serverVector.get(0).m_speedScale+0.01;
//	}
//	
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
	
	public double getECtime() {
		// get executed computing time
		int num_running_svr=0;
		for (Server svr : m_serverVector) {
			if (!svr.isAvailable()) {
				num_running_svr++;
			}
		}
		
		return m_simulator.m_recorder.slot_interval*num_running_svr;
	}

	public void serveTask(Task tskTask) {
		Server svrServer = getIdleServer();
		if (m_bufferTask.isEmpty() && svrServer!=null) {
			svrServer.serveTask(m_simulator, tskTask);
		}else {
			m_bufferTask.add(tskTask);
		}
	}

	public void clean() {
		m_serverVector.removeAllElements();
		m_bufferTask.removeAllElements();
	}

	public Task getNextjob(Server svr) {
		// get the next job to be serve
		Task tskTask=null;
		if (!svr.m_taskQueue.isempty()) {
			tskTask = svr.m_taskQueue.remove(); // get the first task
		}
		// schedule the task preset
		
		return tskTask;
	}

	public void insertTask(Task task) {
		// insert the task to the shortest queue
		Server svrServer = findShortServer();
		if (svrServer != null) {
			svrServer.m_taskQueue.insert(m_simulator, task);
		}else {
			System.err.println("null server");
		}
	}

	private Server findShortServer() {
		// find the shortest queue
		Server minSvr=null;
		double backlog=Double.MAX_VALUE;
		
		for (Server svr : m_serverVector) {
			if (svr.m_taskQueue.size() < backlog) {
				backlog = svr.m_taskQueue.size();
				minSvr = svr;
			}
		}
		return minSvr;
	}
}
