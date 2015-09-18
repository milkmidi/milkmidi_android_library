package milkmidi.pipi.command;

public abstract class BaseCommand extends Command {
	@Override
	final public void execute() {
		this.atExecute();
		this.executeComplete();
	}
	abstract protected void atExecute();
}
