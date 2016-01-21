package com.aisla.newrolit.global;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Pair<S, T> 
{
	private S first;
	private T second;
	
	public Pair(S first, T second)
	{
		this.first = first;
		this.second = second;
	}
	
	public S getFirst() {
		return first;
	}

	public void setFirst(S first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}
	
	
	/**
	 * Converts a list of pairs to a hashtable, where the keys are the first components,
	 * and the values, the second components.
	 * The resulting hashtable will have sets as values, in case if two different 
	 * values are under the same key.
	 * 
	 * To call: result = Pair.<Character, CustomizedFieldProperty> ConvertPairsToDictionary(pairsList)
	 * 
	 * @param <S>
	 * @param <T>
	 * 
	 * @param pairsList
	 * 		List of pairs to convert
	 * 
	 * @return
	 * 		Hashtable with keys of type S (first elements of the pairs list)
	 * 		and values of type Set<T>: each value is the set of all second elements
	 * 		in pairsList whose first element is the key. 
	 */
	public static <S, T> Hashtable<S, Set<T>> ConvertPairsToDictionary(List<Pair<S, T>> pairsList)
	{
		Hashtable<S, Set<T>> result = new Hashtable<S, Set<T>>();
		
		for (Pair<S, T> currPair : pairsList)
		{
			S currKey = currPair.getFirst();
			T currValue = currPair.getSecond();

			if (!result.contains(currKey))
			{
				Set<T> valuesSet = new HashSet<T>();
				result.put(currKey, valuesSet);
			}
			
			result.get(currKey).add(currValue);
		}
		
		return result;
	}
	

	/**
	 * Returns a set with all the objects that belong to the first 
	 * or the second component of a Pair, as specified in componentNumber 
	 * 
	 * @param pairsList
	 * 		List of pairs from which the Set will be made.
	 * 
	 * @param componentNumber
	 * 		Component number from which to take all the elements.
	 * 		This argument can have a value of 1 or 2; in any other case, 
	 * 		the method returns null.
	 * 
	 * @return
	 * 		A set of all the first (if componentNumber = 1) or second 
	 * 		(if componentNumber = 2) components of each pair on the list.
	 * 		If componentNumber is different from 1 and 2, it returns null.
	 * 
	 */
	
	public static <S, T> Set<T> GetSecondComponents(List<Pair<S, T>> pairsList) 
	{
		Set<T> result = new HashSet<T>(pairsList.size());
		
		for (Pair<S, T> p : pairsList)
		{
			if (p != null)
				result.add(p.getSecond());
		}
		
		return result;
	}
}
