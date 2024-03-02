import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyHost extends Host {
	private final PriorityBlockingQueue<Task> taskQueue;
	private volatile Task currentTask = null;
	private final AtomicBoolean preempted = new AtomicBoolean(false);
	private long startTime;

	public MyHost() {
		this.taskQueue = new PriorityBlockingQueue<>(10, new PriorityComparator());
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				Task task = taskQueue.poll();
				if (task == null) {
					synchronized (this) {
						wait();
					}
					continue;
				}

				synchronized (this) {
					currentTask = task;
					startTime = System.nanoTime();
				}

				long durationNano = Objects.equals(task.getLeft(), task.getDuration())
						? task.getDuration() * 1000000L
						: task.getLeft() * 1000000L;
				long endTime = startTime + durationNano;

				while (System.nanoTime() < endTime && !preempted.get()) {
					Thread.sleep(1000);
				}

				synchronized (this) {
					task.finish();
					currentTask = null;
					preempted.set(false);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public synchronized void addTask(Task task) {
		if (currentTask != null && currentTask.isPreemptible() && task.getPriority() > currentTask.getPriority()) {
			preempted.set(true);
			//calculez cat timp a mai ramas din task-ul curent
			long timeLeft = startTime + currentTask.getDuration() * 1000000L - System.nanoTime();
			currentTask.setLeft(timeLeft / 1000000L);
			notify();
			taskQueue.put(currentTask);
		}
		taskQueue.put(task);
		notify();
	}


	@Override
	public int getQueueSize() {
		int queueSize = taskQueue.size();

		synchronized (this) {
			if (currentTask != null) {
				queueSize++;
			}
		}
		return queueSize;
	}

	@Override
	public long getWorkLeft() {
		long totalWorkLeft = 0;
		synchronized (taskQueue) {
			for (Task task : taskQueue) {
				totalWorkLeft += task.getLeft();
			}
		}
		synchronized (this) {
			if (currentTask != null) {
				totalWorkLeft += currentTask.getLeft();
			}
		}
		return totalWorkLeft;
	}


	@Override
	public void shutdown() {
		interrupt();
	}
}

class PriorityComparator implements java.util.Comparator<Task> {
	@Override
	public int compare(Task t1, Task t2) {
		int priorityComparison = Integer.compare(t2.getPriority(), t1.getPriority());
		if (priorityComparison != 0) {
			return priorityComparison;
		}

		return Integer.compare(t1.getStart(), t2.getStart());
	}
}


