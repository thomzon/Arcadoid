package data;

public abstract class Game extends BaseItem {

	protected Process process;
	
	public Game(long identifier) {
		super(identifier);
	}
	
	public abstract void execute();
	public abstract void terminate();

}
