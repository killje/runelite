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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.AsyncBufferedImage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemdatabase.util.ListItemPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.http.api.item.Item;

public class SearchResultItemPanel extends ListItemPanel<Item>
{
	
	private final static Color BACKGROUND_COLOR = ColorScheme.MEDIUM_GRAY_COLOR;
	private final static Color HOVER_COLOR = ColorScheme.MEDIUM_GRAY_COLOR.brighter();
	
	@Inject
	private ItemManager itemManager;
	
	public SearchResultItemPanel()
	{
	}

	@Override
	public void initializePanel(Item item)
	{
		ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
		
		setLayout(new BorderLayout(0, 0));
		setBackground(BACKGROUND_COLOR);
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setSize(0, 36);
		
		AsyncBufferedImage icon = itemManager.getImage(item.getId());
		ImageIcon imageIcon = new ImageIcon(icon);
		JLabel jLabel = new JLabel(imageIcon);
		jLabel.setSize(24, 24);
		icon.onChanged(() ->
		{
			SwingUtilities.invokeLater(() ->
			{
				jLabel.updateUI();
			});
		});
		add(jLabel, BorderLayout.WEST);
		add(new JLabel(itemComposition.getName()));
		
		addMouseListener(new MouseAdaptor(this));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	private static class MouseAdaptor extends MouseInputAdapter
	{

		private final SearchResultItemPanel searchResultItemPanel;
		
		public MouseAdaptor(SearchResultItemPanel searchResultItemPanel)
		{
			this.searchResultItemPanel = searchResultItemPanel;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
		}
		
		@Override
		public void mouseEntered(MouseEvent mouseEvent)
		{
			searchResultItemPanel.setBackground(HOVER_COLOR);
			
		}

		@Override
		public void mouseExited(MouseEvent mouseEvent)
		{
			searchResultItemPanel.setBackground(BACKGROUND_COLOR);
		}
	}
	
}
