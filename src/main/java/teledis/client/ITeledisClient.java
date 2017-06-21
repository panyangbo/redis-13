package teledis.client;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author Paul-Pan
 *
 */
public interface ITeledisClient{

	String set(String servtype, String key, String value);

	String get(String servtype, String key);

	List<Object> get(String servtype, Set<String> keys);

	String set(String key, String value);

	String get(String key);

}
