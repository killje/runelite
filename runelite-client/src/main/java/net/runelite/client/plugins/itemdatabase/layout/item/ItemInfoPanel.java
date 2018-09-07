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
package net.runelite.client.plugins.itemdatabase.layout.item;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.runelite.client.game.AsyncBufferedImage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.http.api.item.Item;

@Singleton
public class ItemInfoPanel extends JPanel
{
	
	@Inject
	private ItemManager itemManager;
	
	private final JLabel itemIcon = new JLabel();
	private final JLabel itemName = new JLabel();
	private final JLabel itemDescription = new JLabel();

	public ItemInfoPanel()
	{
		super(new BorderLayout(6, 6));
		
		JPanel itemInfo = new JPanel(new GridLayout(0, 2));
		
		setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
		itemIcon.setSize(24, 24);
		add(itemIcon, BorderLayout.WEST);
		add(itemInfo, BorderLayout.CENTER);
		itemInfo.add(new JLabel("Name"));
		itemInfo.add(itemName);
		itemInfo.add(new JLabel("Description"));
		itemInfo.add(itemDescription);
	}

	public void showItemInfo(Item item)
	{
		AsyncBufferedImage icon = itemManager.getImage(item.getId());
		ImageIcon imageIcon = new ImageIcon(icon);
		itemIcon.setIcon(imageIcon);
		itemName.setName(item.getName());
		itemDescription.setName(item.getDescription());
		icon.onChanged(() ->
		{
			SwingUtilities.invokeLater(() ->
			{
				itemIcon.updateUI();
			});
		});
	}

}
