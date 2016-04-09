package views.frontend;

public interface CoverflowListDataSource {

	public int numberOfItems();
	public CoverflowItem nodeForItemAtIndex(int index);
	
}
