/* Implement this class. */

import java.util.Comparator;
import java.util.List;

public class MyDispatcher extends Dispatcher {

	private int lastAssignedHostIndex = -1; // indexul ultimului host la care s-a adaugat o task
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
		synchronized (this) {
			switch (algorithm) {
				case ROUND_ROBIN:
					scheduleRoundRobin(task);
					break;
				case SHORTEST_QUEUE:
					scheduleShortestQueue(task);
					break;
				case SIZE_INTERVAL_TASK_ASSIGNMENT:
					scheduleSizeIntervalTaskAssignment(task);
					break;
				case LEAST_WORK_LEFT:
					scheduleLeastWorkLeft(task);
					break;
				default:
					break;
			}
		}
    }

	private void scheduleRoundRobin(Task task) {
		lastAssignedHostIndex = (lastAssignedHostIndex + 1) % hosts.size();
		hosts.get(lastAssignedHostIndex).addTask(task);
	}

	private void scheduleShortestQueue(Task task) {
		Host shortestQueueHost = hosts.get(0);
		int minQueueSize = shortestQueueHost.getQueueSize();

		for (Host host : hosts) {
			int currentQueueSize = host.getQueueSize();
			if (currentQueueSize < minQueueSize) {
				shortestQueueHost = host;
				minQueueSize = currentQueueSize;
			}
		}
		shortestQueueHost.addTask(task);
	}


	private void scheduleSizeIntervalTaskAssignment(Task task) {
		Host designatedHost = null;
		switch (task.getType()) {
			case SHORT:
				designatedHost = hosts.get(0);
				break;
			case MEDIUM:
				designatedHost = hosts.get(1);
				break;
			case LONG:
				designatedHost = hosts.get(2);
				break;
			default:
				break;
		}
		if (designatedHost != null) {
			designatedHost.addTask(task);
		}
	}

	private void scheduleLeastWorkLeft(Task task) {
		Host leastWorkLeftHost = hosts.get(0);
		long minWorkLeft = leastWorkLeftHost.getWorkLeft();

		for (Host host : hosts) {
			long currentWorkLeft = host.getWorkLeft();
			if (currentWorkLeft < minWorkLeft) {
				leastWorkLeftHost = host;
				minWorkLeft = currentWorkLeft;
			}
		}
		leastWorkLeftHost.addTask(task);
	}
}
