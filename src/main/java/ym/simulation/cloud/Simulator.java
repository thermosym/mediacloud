package ym.simulation.cloud;

class AbstractSimulator {
	OrderedSet events;

	void insert(AbstractEvent e) {
		events.insert(e);
	}

	AbstractEvent cancel(AbstractEvent e) {
		throw new java.lang.RuntimeException("Method not implemented");
	}
}


public class Simulator extends AbstractSimulator {
	double time;

	double now() {
		return time;
	}

	void doAllEvents() {
		Event e;
		while ((e = (Event) events.removeFirst()) != null) {
			time = e.time;
			e.execute(this);
		}
	}
}

