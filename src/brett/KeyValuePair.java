package brett;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class KeyValuePair {
	@PrimaryKey
	String key;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	String data;
}
