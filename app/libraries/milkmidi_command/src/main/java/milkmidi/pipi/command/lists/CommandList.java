package milkmidi.pipi.command.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import milkmidi.pipi.command.Command;

public abstract class CommandList extends Command implements Observer {

	private List<Command> mCmdStack;
	
	private int mPositioni;
	private boolean mAutoDestroy = true;
	

	public CommandList() {
		super();
		this.mCmdStack = new ArrayList<>();
	}	
	
	/*public void addCommand(long delay){
		this.addCommand(new Wait(delay));		
	}
	public void addCommand(String tag , String msg){
		this.addCommand(new Trace(tag,msg));
	}	*/
	public void addCommand(Command cmd){
		this.mCmdStack.add(cmd);
		cmd.addObserver(this);
		trace("addCommand", "getTotal():"+ getTotal(),cmd);
	}
	
	@SuppressWarnings("unchecked")
	public void addCommand(Command... cmds){
		for (int i = 0; i < cmds.length; i++) {
			this.addCommand(cmds[i]);
		}
	}
	
	

	final protected boolean hasNextCommand() {
		return this.mPositioni < getTotal() ;
	}
	final protected void reset() {
		this.mPositioni = 0;
	}
	
	protected Command getNextCommand(){
		return mCmdStack.get(mPositioni++);
	}



	@Override
	public void execute() {
	}

	@Override
	public void interrupt() {
		super.interrupt();
		if(this.mAutoDestroy){
			this.destroy();
		}
	}

	@Override
	public void executeComplete() {
		super.executeComplete();
		if(this.mAutoDestroy){
			this.destroy();
		}
	}

	
	public abstract void update(Observable observable, Object data); 
		
	
	
	public int getPosition() { return this.mPositioni; }
	public int getTotal() { return this.mCmdStack.size();}


	@Override
	public void destroy() {
		synchronized( this ){
			if( getDestroyed() ){			
				return;
			}
			for (Command cmd : mCmdStack) {
				cmd.deleteObserver(this);
				cmd.destroy();
			}
			mCmdStack.clear();
			mCmdStack = null;
		}		
		super.destroy();
	}
	
	@Override
	public String toString(){
		return TAG +" total:"+ getTotal();
	}


}
