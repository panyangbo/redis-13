package servetest;

import teledis.client.impl.TelekRedisClient;

public class MapServeType {

	public TelekRedisClient client = new TelekRedisClient("redis-map.xml");
	
	public void Set() {
		System.out.println(client.set("serve-one", "serve-one-key", "serve-one-value"));
		System.out.println(client.set("serve-two", "serve-two-key", "serve-two-value"));
		System.out.println(client.set("serve-three", "serve-three-key", "serve-three-value"));
	}
	
	public static void main(String[] args) {
		MapServeType mst = new MapServeType();
		mst.Set();
	}
}
