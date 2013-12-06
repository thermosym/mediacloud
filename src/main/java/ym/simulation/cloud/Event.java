package ym.simulation.cloud;

interface Comparable {
	boolean lessThan(Comparable y);
}

abstract class AbstractEvent implements Comparable {
	abstract void execute(AbstractSimulator simulator);
}

abstract class Event extends AbstractEvent {
	double time;

	public boolean lessThan(Comparable y) {
		Event e = (Event) y; // Will throw an exception if y is not an Event
		return this.time < e.time;
	}
}

