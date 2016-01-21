package com.aisla.newrolit.com;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * ReceiveDataQ holds the synchronized queue object
 * which is thread safe and designed to avoid deadlocks   
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class ReceiveDataQ {
	Queue<List<Integer>> _receiveDataQ = new LinkedList<List<Integer>>();
	   
	/**
	 * Return if the queue has or not elements
	 * 
	 * This method always returns immediately.
	 * 
	 * @return true / false 
	 *
	 */    
     synchronized boolean isEmpty() {
		      notify();
		      return _receiveDataQ.isEmpty();
	 }

 	/**
 	 * Return a queue item which contais 
 	 * a list of integers of the data stream
 	 * <p>
 	 * This method always returns immediately.
 	 * 
 	 * @return a list of integers of the data stream 
 	 *
 	 */    
	 synchronized List<Integer> get() {
	      return _receiveDataQ.poll();
	 }

 	/**
 	 * Add a queue item which contains 
 	 * a list of integers of the data stream
 	 * <p>
 	 * This method always returns immediately.
 	 * 
 	 * @param n a list of integers of the data stream 
 	 *
 	 */    
	 synchronized void put(List<Integer> n) {
	      _receiveDataQ.add(n);
	 }
	   
	 	/**
	 	 * Generate a notification for the caller thread 
	 	 * to reactivate it
	 	 * <p>
	 	 * This method always returns immediately.
	 	 *
	 	 */    
	 synchronized void notifyCaller() {
		      notify();
	 }
}
