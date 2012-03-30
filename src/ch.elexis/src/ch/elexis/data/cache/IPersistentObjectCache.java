package ch.elexis.data.cache;

public interface IPersistentObjectCache<K> {
	
	/**
	 * Insert an Object that will stay until it is manually removed, or memory gets low, or at most
	 * for ICacheable#getCacheTime seconds
	 */
	public void put(final K key, final Object object, final int timeToCacheInSeconds);
	
	/**
	 * retrieve a previously inserted object
	 * 
	 * @return the object or null, if the object was expired or removed bei the garbage collector.
	 */
	public Object get(final K key);
	
	public void remove(final K key);
	
	public void clear();
	
	/**
	 * write statistics to log
	 */
	public void stat();
	
	/**
	 * Expire and remove all Objects (without removing the SoftReferences, to avoid
	 * ConcurrentModificationExceptions)
	 * 
	 */
	public void purge();
	
	/**
	 * completely delete cache
	 */
	public void reset();
	
}