package milkmidi.pipi.command.lists;

import java.util.Observable;

import milkmidi.pipi.command.Command;

public class SerialCommand extends CommandList {
	
	private boolean mRunning = false;
	private Command mCurrentCmd;

	
	public SerialCommand() {
	}
	public SerialCommand(Command... cmds) {
		this.addCommand( cmds );
	}
	

	@Override
	public void execute() {
		if (!this.mRunning) {
			super.reset();
		}
		this.mRunning = true;
		if (super.hasNextCommand()) {			
			this.mCurrentCmd = super.getNextCommand();		
			this.mCurrentCmd.setData( this.getData() );
			this.mCurrentCmd.execute();
		}else{
			this.mCurrentCmd = null;
			this.executeComplete();
		}
	}

	
	@Override
	public void update(Observable observable, Object data) {
		if (data.equals(CommandType.EXECUTE_COMPLETE)) {
			this.setData( mCurrentCmd.getData() );
			this.execute();			
		}else if (data.equals(CommandType.EXECUTE_INTERRUPT)) {
			this.mCurrentCmd = null;
			this.interrupt();
		}
//		Log.i(TAG, getPosition()+"/"+getTotal());
	}


	@Override
	public void destroy() {
		if( this.getDestroyed() ){			
			return;			
		}
		this.mCurrentCmd = null;
		super.destroy();
	}
	
	
	
	


}
