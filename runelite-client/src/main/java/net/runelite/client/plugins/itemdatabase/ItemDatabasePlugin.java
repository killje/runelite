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
package net.runelite.client.plugins.itemdatabase;

import com.google.common.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemdatabase.layout.ItemDatabasePanel;
import net.runelite.client.plugins.itemdatabase.properties.Chance;
import net.runelite.client.plugins.itemdatabase.recipes.Recipe;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeGroup;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeItem;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Item Database",
	description = "Search for items and get all the information about these items.",
	tags = {"item", "recipe", "info"},
	enabledByDefault = false
)
@Slf4j
public class ItemDatabasePlugin extends Plugin
{
	@Inject
	private PluginManager pluginManager;

	@Inject
	private ScheduledExecutorService executorService;

	@Inject
	private RecipeManager recipeManager;


	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private ItemDatabasePanel itemDatabasePanel;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	private NavigationButton navButton;

	@Override
	protected void startUp()
	{

		BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "ItemDbIcon.png");

		navButton = NavigationButton.builder()
			.tooltip("Item Database")
			.icon(icon)
			.panel(itemDatabasePanel)
			.priority(4)
			.build();

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			initRecipesManager();
			clientToolbar.addNavigation(navButton);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			initRecipesManager();
			clientToolbar.addNavigation(navButton);
		}
	}

	private void initRecipesManager()
	{
		clientThread.invokeLater(() ->
		{
			if (recipeManager == null)
			{
				log.warn("RecipeManager not initialized!");
				scheduleShutdown();
				return;
			}
			recipeManager.loadRecipeGroups();
			if (recipeManager.getRecipeGroups() == null || recipeManager.getRecipeGroups().isEmpty())
			{
				log.warn("RecipeManager didn't load correctly!");
				scheduleShutdown();
				return;
			}
			log.info("Number of recipe groups: " + recipeManager.getRecipeGroups().size());
			for (RecipeGroup recipeGroup : recipeManager.getRecipeGroups())
			{
				log.info(" - " + recipeGroup.getClass().getSimpleName() + ":");
				log.info("   Number of recipes: " + recipeGroup.getRecipes().size());
				for (Recipe recipe : recipeGroup.getRecipes())
				{
					log.info("    - " + recipe.getClass().getSimpleName() + ":");
					log.info("      Number of input items: " + recipe.getInputs().size());
					for (RecipeItem recipeItem : recipe.getInputs())
					{
						log.info("       - id: " + recipeItem.getId());
						log.info("         quantity: " + recipeItem.getQuantity());
						log.info("         price: " + recipeItem.getItemComposition().getPrice());
					}
					log.info("      Number of output items: " + recipe.getOutputs().size());
					for (RecipeItem recipeItem : recipe.getOutputs())
					{
						log.info("       - id: " + recipeItem.getId());
						log.info("         quantity: " + recipeItem.getQuantity());
						if (recipe instanceof Chance)
						{
							log.info("         chance: " + ((Chance) recipe).getChanceFraction().toProperString() + "(" + ((Chance)recipe).getChancePercentage(3) + "%)");
						}
						log.info("         price: " + recipeItem.getItemComposition().getPrice());
					}
				}
			}
		});
	}

	private void scheduleShutdown()
	{
		executorService.schedule(() -> {
			pluginManager.setPluginEnabled(this, false);
			try
			{
				pluginManager.stopPlugin(this);
			}
			catch (PluginInstantiationException e)
			{
				log.warn("Error when stopping plugin " + getClass().getSimpleName(), e);
			}
		}, 1, TimeUnit.SECONDS);
	}

}
