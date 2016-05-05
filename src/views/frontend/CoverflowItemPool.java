package views.frontend;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles pooling and upooling CoverflowItem objects.
 * @author Thomas Debouverie
 *
 */
public class CoverflowItemPool {

	private static final int INITIAL_POOL_SIZE = 40;
	private static List<CoverflowItem> itemPool = new ArrayList<CoverflowItem>();
	
	static {
		fillItemPool();
	}
	
	private static void fillItemPool() {
		for (int index = 0; index < INITIAL_POOL_SIZE; ++index) {
			itemPool.add(new CoverflowItem());
		}
	}
	
	public static void recycleItem(CoverflowItem item) {
		itemPool.add(item);
	}
	
	public static CoverflowItem dequeueItem() {
		if (itemPool.isEmpty()) {
			fillItemPool();
		}
		CoverflowItem item = itemPool.get(itemPool.size() - 1);
		itemPool.remove(itemPool.size() - 1);
		return item;
	}
	
}
