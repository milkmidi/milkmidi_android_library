package milkmidi.pipi.command;

public class RunnableCommand extends Command {

	private Runnable mRunnable;
	public RunnableCommand( Runnable runnable) {
		this.mRunnable = runnable;
	}

	@Override
	public void execute() {
		this.mRunnable.run();
		this.executeComplete();		
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		this.mRunnable = null;
	}
	
	

}
