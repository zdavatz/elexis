package ch.rgw.tools;

import java.util.LinkedList;

/**
 * A Stack that has a limited capacity. If more elements are pushed in on top, oldest elements are
 * "pushed out" at the bottom. In all other respects, this class behaves like any other stack
 * implementation
 * 
 * @author gerry
 * 
 * @param <T>
 */
@SuppressWarnings("serial")
public class LimitSizeStack<T> extends LinkedList<T> {
	private int max;
	
	public LimitSizeStack(int limit){
		max = limit;
	}
	
	public void push(T elem){
		if (size() >= max) {
			remove(size() - 1);
		}
		add(0, elem);
	}
	
	public T pop(){
		if (size() == 0) {
			return null;
		}
		return remove(0);
	}
}
