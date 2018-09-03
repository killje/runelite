package net.runelite.client.plugins.itemdatabase;

import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.itemdatabase.properties.Chance;
import net.runelite.client.plugins.itemdatabase.recipes.Recipe;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeGroup;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeItem;
import net.runelite.client.plugins.itemdatabase.recipes.RecipeManager;

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

	@Override
	protected void startUp()
	{

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			initRecipesManager();
		}
	}

	@Override
	protected void shutDown()
	{
		log.info("Shutting down.");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			initRecipesManager();
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
