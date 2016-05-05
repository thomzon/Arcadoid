package views.frontend;

/**
 * Defines an object capable of providing items information to a CoverflowList
 * @author Thomas Debouverie
 *
 */
public interface CoverflowListDataSource {

	public int numberOfItemsInCoverflowList(CoverflowList coverflowList);
	public CoverflowItem nodeForItemAtIndex(int index, CoverflowList coverflowList);
	
}
