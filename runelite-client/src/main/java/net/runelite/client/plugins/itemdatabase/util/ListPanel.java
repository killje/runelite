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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.ui.components.CustomScrollBarUI;

public abstract class ListPanel<T> extends JScrollPane implements ArrayListChangeListener<T>
{
	
	@Getter
	private final ArrayListModel<T> arrayListModel = new ArrayListModel<>();
	private final Provider<? extends ListItemPanel<T>> itemProvider;
	private final Map<T, ListItemPanel<T>> listItems = new HashMap<>();
	
	@Getter(AccessLevel.PROTECTED)
	private final JPanel listPanelWrapper = new JPanel(new BorderLayout());
	
	@Getter(AccessLevel.PROTECTED)
	private final JPanel listPanel = new JPanel(new GridBagLayout());
	
	protected GridBagConstraints constraints = new GridBagConstraints();

	public ListPanel(Provider<? extends ListItemPanel<T>> itemProvider)
	{
		super();
		setViewportView(listPanelWrapper);
		updateUI();
		listPanelWrapper.add(listPanel, BorderLayout.NORTH);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.insets = new Insets(0,0,6,0);
		
		this.itemProvider = itemProvider;
		init();
	}
	
	private void init() {
		JScrollBar bar = getVerticalScrollBar();
		bar.setUI(new CustomScrollBarUI());
		bar.setUnitIncrement(16);
		bar.setPreferredSize(new Dimension(12, 0));
		bar.setBorder(new EmptyBorder(0, 6, 0, 0));
		
		arrayListModel.addArrayListChangeListener(this);
	}
	
	@Override
	public void onArrayListAddEvent(ArrayListAddEvent<T> arrayListAddEvent)
	{
		ListItemPanel<T> newItem = itemProvider.get();
		newItem.initializePanel(arrayListAddEvent.getAddedItem());
		listItems.put(arrayListAddEvent.getAddedItem(), newItem);
		SwingUtilities.invokeLater(() ->
		{
			listPanel.add(newItem, constraints);
			updateUI();
		});
	}

	@Override
	public void onArrayListRemoveEvent(ArrayListRemoveEvent<T> arrayListRemoveEvent)
	{
		ListItemPanel<T> itemToRemove = listItems.get(arrayListRemoveEvent.getRemovedItem());
		SwingUtilities.invokeLater(() ->
		{
			listPanel.remove(itemToRemove);
			updateUI();
		});
	}
	
	
}
