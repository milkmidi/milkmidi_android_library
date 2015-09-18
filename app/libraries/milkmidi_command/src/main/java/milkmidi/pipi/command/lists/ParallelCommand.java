package milkmidi.pipi.command.lists;

import java.util.Observable;

import milkmidi.pipi.command.Command;


public class ParallelCommand extends CommandList {

	
	private int mCmdCount = 0;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute() {
		while (super.hasNextCommand()) {		
			Command currentCmd = super.getNextCommand();
			currentCmd.setData( this.getData() );
			currentCmd.execute();
		}		
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data.equals(CommandType.EXECUTE_COMPLETE)) {			
			mCmdCount++;
			if (mCmdCount == getTotal()) {
				executeComplete();
			}
		}
	}

}
