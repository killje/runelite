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
package net.runelite.client.plugins.itemdatabase.layout.search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemdatabase.layout.ContentPanelWrapper;
import net.runelite.client.plugins.itemdatabase.util.ArrayListModel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.Item;
import net.runelite.http.api.item.SearchResult;

@Singleton
public class SearchBar extends IconTextField implements ActionListener
{

	private static final ImageIcon SEARCH_ICON;
	private static final ImageIcon LOADING_ICON;
	private static final ImageIcon ERROR_ICON;

	@Inject
	private ContentPanelWrapper contentPanelWrapper;

	@Inject
	private ItemManager itemManager;
	
	private final ArrayListModel<Item> searchResults;
	
	static
	{
		SEARCH_ICON = new ImageIcon(ImageUtil.alphaOffset(ImageUtil.grayscaleOffset(ImageUtil.getResourceStreamFromClass(IconTextField.class, "search.png"), 0f), 1.75f));
		LOADING_ICON = new ImageIcon(IconTextField.class.getResource("loading_spinner.gif"));
		ERROR_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(IconTextField.class, "error.png"));
	}

	@Inject
	public SearchBar(SearchResultPanel searchResultPanel)
	{
		searchResults = searchResultPanel.getArrayListModel();
		initialize();
	}

	private void initialize()
	{
		setPreferredSize(new Dimension(100, 30));
		setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
		setHoverBackgroundColor(ColorScheme.MEDIUM_GRAY_COLOR.brighter());
		setIcon(SEARCH_ICON);
		addActionListener(this); 
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		
		setIcon(LOADING_ICON);
		
		contentPanelWrapper.showPanel("SEARCH_RESULTS");
		
		try
		{
			SearchResult searchResult = itemManager.searchForItem(getText());
			List<Item> searchResultItems = searchResult.getItems();

			searchResults.clear();
			searchResults.addAll(searchResultItems);
			searchResults.addAll(searchResultItems);

			setIcon(SEARCH_ICON);
		} catch (ExecutionException ex)
		{
			setIcon(ERROR_ICON);
		}
		
	}
}
