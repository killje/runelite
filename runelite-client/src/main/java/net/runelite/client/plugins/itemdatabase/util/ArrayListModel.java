/*
 * Copyright (c) 2018 Patrick Beuks (killje) <patrick.beuks@gmail.com>
 * Copyright (c) 2018 CC007 <Coolcat_the_best2@hotmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.itemdatabase.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ArrayListModel<T> extends ArrayList<T>
{
	
	private final ArrayList<ArrayListChangeListener<T>> listeners = new ArrayList<>();
	
	public boolean addArrayListChangeListener (ArrayListChangeListener<T> listener) {
		return listeners.add(listener);
	}
	
	public boolean removeArrayListChangeListener (ArrayListChangeListener<T> listener) {
		return listeners.remove(listener);
	}
	
	public void clearArrayListChangeListeners() {
		listeners.clear();
	}
	
	private void sendAddChange(T itemAdded) {
		
		ArrayListAddEvent<T> event = new ArrayListAddEvent<>(itemAdded);
		for (ArrayListChangeListener<T> listener : listeners)
		{
			listener.onArrayListAddEvent(event);
		}
	}
	
	private void sendRemoveChange(T itemRemoved) {
		
		ArrayListRemoveEvent<T> event = new ArrayListRemoveEvent<>(itemRemoved);
		for (ArrayListChangeListener<T> listener : listeners)
		{
			listener.onArrayListRemoveEvent(event);
		}
	}

	@Override
	public boolean add(T e)
	{
		
		boolean returnValue = super.add(e);
		sendAddChange(e);
		return returnValue;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean returnValue = super.addAll(c);
		for (T t : c)
		{
			sendAddChange(t);
		}
		return returnValue;
	}

	@Override
	public void add(int index, T element)
	{
		sendAddChange(element);
		super.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		boolean returnValue = super.addAll(index, c);
		for (T t : c)
		{
			sendAddChange(t);
		}
		return returnValue;
	}

	@Override
	public void clear()
	{
		for (T next : this)
		{
			sendRemoveChange(next);
		}
		super.clear();
	}

	@Override
	public boolean remove(Object o)
	{
		boolean returnValue = super.remove(o);
		if(returnValue) {
			sendRemoveChange((T) o);
		}
		return returnValue;
	}

	@Override
	public T remove(int index)
	{
		T returnValue = super.remove(index);
		sendRemoveChange(returnValue);
		return returnValue;
	}

	@Override
	public void forEach(Consumer<? super T> action)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException();
	}

	
	
	
	
}
