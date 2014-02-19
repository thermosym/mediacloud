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
	
//	public double getECtime() {
//		// get executed computing time
//		int num_running_svr=0;
//		for (Server svr : m_serverVector) {
//			if (!svr.isAvailable()) {
//				num_running_svr++;
//			}
//		}
//		
//		return m_simulator.m_recorder.slot_interval*num_running_svr;
//	}


	public void clean() {
		m_serverVector.removeAllElements();
	}

	public Task getNextjob(Server svr) {
		// get the next job to be serve
		Task tskTask=null;
		if (!svr.m_taskQueue.isempty()) {
			tskTask = svr.m_taskQueue.remove(); // get the first task
		}
		return tskTask;
	}
	
	// insert the task to a queue (server)
	public void insertTask(Task task) {
		// insert the task to the shortest queue
		Server svrServer = findShortServer();
		if (svrServer != null) {
			svrServer.m_taskQueue.insert(m_simulator, task);
			// try schedule
			if (svrServer.isAvailable()) {
				Task tskServe = getNextjob(svrServer);
				svrServer.serveTask(m_simulator, tskServe);
//				System.out.println("try scehdule:"+tskServe.getContent());
			}
		}else {
			System.err.println("null server");
		}
	}

	//TODO: need to revise for real backlog: lyapunov
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
