package status.flocking.demos;

import java.util.Map;

import steering.ActionSelection;
import steering.Locomotion;
import vector.Vector2D;

public class SimulationThread extends Thread {

	private final Map<Locomotion, ActionSelection> flock;
	private boolean shouldTerminate;
	private final double radius;
	private final double width;
	private final double height;

	public SimulationThread(Map<Locomotion, ActionSelection> boids, double width, double height,
			double radius) {
		this.flock = boids;
		this.width = width;
		this.height = height;
		this.radius = radius;
	}

	@Override
	public void run() {
		do {
			try {
				sleep(1000 / 50);
			} catch (InterruptedException ignored) {
				// ignored
			}
			if (shouldTerminate)
				break;
			step();
		} while (shouldTerminate == false);
	}

	private void step() {
		synchronized (flock) {
			for (Locomotion boid : flock.keySet()) {
				ActionSelection selector = flock.get(boid);
				Vector2D steering = selector.selectAction().steeringForce();
				boid.steer(steering);
				boid.move();
				wallMove(boid, width, height, radius);
			}
		}
	}

	public void terminate() {
		shouldTerminate = true;
		this.interrupt();
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void wallMove(Locomotion boid, double width, double height, double radius) {
		// check if we've run into a wall
		if ((boid.position().x - radius < 0) || ((boid.position().x) > width)) {
			boid.position().x = (boid.position().x - radius) < 0 ? radius : width;
			boid.velocity().x *= -1;
		}

		if ((boid.position().y - radius < 0) || ((boid.position().y) > height)) {
			boid.position().y = (boid.position().y - radius) < 0 ? radius : height;
			boid.velocity().y *= -1;
		}
	}
}