package lab1;
public class Philosophers {
	private static int NUM_PHIL = 5;
	public static void main(String[] args) throws InterruptedException {
		Object[] forks = new Object[NUM_PHIL];
		Philosopher[] phils = new Philosopher[NUM_PHIL];
		// Create forks
		for(int i=0; i<phils.length; i++) {
			forks[i]=new Object();
		}
		// Initialize philosophers with two forks each
		for(int i=0; i<phils.length; i++) {

			if(i != 4){
			phils[i]=new Philosopher(i, forks[i], forks[(i+1) % phils.length]);
			}else{
				phils[i] = new Philosopher(i, forks[(i+1) % phils.length],forks[i] );
			}


		}
		
		for(Thread p : phils)  {
			// TODO start the philosophers
			p.start();
		}
		// Keep running until the user presses ENTER
		new java.util.Scanner(System.in).nextLine();
		for(Philosopher p : phils) {
			// TODO request termination from all philosophers
			p.terminate();
			
			
		}
		for(Thread p : phils) {
			// TODO wait for all philosophers to finish
			p.join();
		}
		System.out.println("All philosophers finished dining.");
	}

	static class Philosopher extends Thread {
		private final Object leftFork, rightFork;
		private final int id;
		private boolean run = true;


		public Philosopher(int id, Object leftFork, Object rightFork) {
			this.id = id;
			this.leftFork = leftFork;
			this.rightFork = rightFork;
		}

		public  void terminate(){
			this.run = false;
		}

		private void eat() throws InterruptedException {
			System.out.println("Philosopher "+id+" is eating.");
			Thread.sleep((long)(Math.random()*3));
		}

		private void think() throws InterruptedException {
			System.out.println("Philosopher "+id+" is thinking.");
			Thread.sleep((long)(Math.random()*3));
		}

		private void waiting() throws InterruptedException {
			System.out.println("Philosopher "+id+" is waiting.");
			Thread.sleep((long)(Math.random()*3));
		}

		public void run( ) {
			try {
				while(run) {
					think();
					waiting();
					// TODO pick up forks by synchronizing on the two forks
					// TODO to eat, forks have to be "held"
					synchronized(leftFork){
						synchronized(rightFork){
						eat();
						}
					}

					// TODO put down forks by existing the synchronized context
				}
			} catch(InterruptedException ie) {
				// Do nothing, interruption may cause this, but safe to ignore
			}
		}
	}
}
