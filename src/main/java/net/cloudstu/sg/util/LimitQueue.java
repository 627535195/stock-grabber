package net.cloudstu.sg.util;

import java.util.LinkedList;

/**
 * 定长队列
 *
 * @author zhiming.li
 */
public class LimitQueue<E> {
	private int limit; // 队列长度

	private LinkedList<E> queue = new LinkedList<E>();

	public LimitQueue(int limit){
		this.limit = limit;
	}

	/**
	 * 入列：当队列大小已满时，把队头的元素poll掉
	 */
	public synchronized void offer(E e){
		if(queue.size() >= limit){
			queue.poll();
		}
		queue.offer(e);
	}

	public boolean isFull() {
		if(queue.size() < limit){
			return false;
		}
		return true;
	}

	public E get(int position) {
		return queue.get(position);
	}

	public E getLast() {
		return queue.getLast();
	}

	public E getFirst() {
		return queue.getFirst();
	}

	public int getLimit() {
		return limit;
	}

	public int size() {
		return queue.size();
	}

	public void clear() {
		queue.clear();
	}

	@Override
	public String toString() {
		return queue.toString();
	}
}
