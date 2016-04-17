package views.frontend;

public interface CoverflowListDataSource {

	public int numberOfItemsInCoverflowList(CoverflowList coverflowList);
	public CoverflowItem nodeForItemAtIndex(int index, CoverflowList coverflowList);
	
}
