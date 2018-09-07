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

import java.awt.CardLayout;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.JPanel;
import net.runelite.client.plugins.itemdatabase.layout.ContentPanelWrapper;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.http.api.item.Item;

@Singleton
public class ItemPanel extends JPanel
{

	private final ContentPanelWrapper contentPanelWrapper;

	private final ItemInfoPanel itemInfoPanel;
	private final DescriptionTabPanel descriptionTabPanel;
	private final RecipeTabPanel recipeTabPanel;
	private final UsagesTabPanel usagesTabPanel;
	private final TabContentPanel tabContentPanel;

	@Inject
	public ItemPanel(
			ContentPanelWrapper contentPanelWrapper,
			Provider<ItemTab> itemTabProvider,
			ItemInfoPanel itemInfoPanel,
			TabContentPanel tabContentPanel,
			DescriptionTabPanel descriptionTabPanel,
			RecipeTabPanel recipeTabPanel,
			UsagesTabPanel usagesTabPanel
	)
	{
		super(new DynamicGridLayout(0, 1, 0, 6));

		this.contentPanelWrapper = contentPanelWrapper;
		this.itemInfoPanel = itemInfoPanel;
		this.descriptionTabPanel = descriptionTabPanel;
		this.recipeTabPanel = recipeTabPanel;
		this.usagesTabPanel = usagesTabPanel;
		this.tabContentPanel = tabContentPanel;
		
		contentPanelWrapper.addPanel(this, "ITEM");

		ItemTab descriptionTab = itemTabProvider.get();
		ItemTab recipesTab = itemTabProvider.get();
		ItemTab useagesTab = itemTabProvider.get();

		descriptionTab.initializeTab("Description", descriptionTabPanel);
		recipesTab.initializeTab("Recipes", recipeTabPanel);
		useagesTab.initializeTab("Useages", usagesTabPanel);

		JPanel tabWrapper = new JPanel(new DynamicGridLayout(1, 3));
		tabWrapper.add(descriptionTab);
		tabWrapper.add(recipesTab);
		tabWrapper.add(useagesTab);

		add(itemInfoPanel);
		add(tabWrapper);
		add(tabContentPanel);
	}

	public void showItem(Item item)
	{
		// TODO reset scrollbar on tab panels
		contentPanelWrapper.showPanel("ITEM");
		itemInfoPanel.showItemInfo(item);
		tabContentPanel.showComponent("Recipes");
	}

}
