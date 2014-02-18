package ym.simulation.cloud;

import java.util.ArrayList;
import java.util.Vector;

import org.omg.CORBA.PRIVATE_MEMBER;

abstract class OrderedSet {
	abstract void insert(Comparable x);

	abstract Comparable removeFirst();

	abstract int size();

	abstract Comparable remove(Comparable x);
}

/**
 * Queue used in event system queue
 * @author yangming
 */
class ListQueue extends OrderedSet {
	java.util.Vector elements = new java.util.Vector();

	void insert(Comparable x) {
		int i = 0;
		while (i < elements.size()
				&& ((Comparable) elements.elementAt(i)).lessThan(x)) {
			i++;
		}
		elements.insertElementAt(x, i);
	}

	Comparable removeFirst() {
		if (elements.size() == 0)
			return null;
		Comparable x = (Comparable) elements.firstElement();
		elements.removeElementAt(0);
		return x;
	}

	Comparable remove(Comparable x) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.elementAt(i).equals(x)) {
				Object y = elements.elementAt(i);
				elements.removeElementAt(i);
				return (Comparable) y;
			}
		}
		return null;
	}

	public int size() {
		return elements.size();
	}

}


/**
 * Queue used in buffer task
 * @author yangming
 */
public class Queue {
	
	private CloudSimulator m_simulator;
	public Queue(CloudSimulator sim) {
		this.m_simulator = sim;
	}
    /**
    * Use the Java Vector to implement a FIFO queue.
    */
    private java.util.Vector<Task> m_Tasks = new java.util.Vector<Task>();

    /**
    * Add a Task to the queue.
    * If the server is available, 
    * pass the Task on to the scheduler.  
    * Otherwise add the Task to the queue.
    */
    public void insert(AbstractSimulator simulator, Task task) {
    	// insert into queue
    	m_Tasks.addElement(task);
    	// update the log
//    	record.updateArrivalEvent(task);
//    	
//        if (!m_simulator.onlySlotSchedule) {
//        	m_simulator.schedule(simulator);
//        }
    }
    
    /**
    * @return the first Task in the queue
    */
    public Task remove() {
        Task task = (Task) m_Tasks.firstElement();
        m_Tasks.removeElementAt(0);
        return task;
    }
    
    public int size() {
        return m_Tasks.size();
    }
    
    public Task getHead() {
    	if (m_Tasks.size() > 0) {
			return (Task)m_Tasks.firstElement();
		}else{
			return null;
		}
    }

}