package net.runelite.client.plugins.itemdatabase;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Item Database",
	description = "Search for items and get all the information about these items.",
	tags = {"item", "recipe", "info"},
	enabledByDefault = false
)
@Slf4j
public class ItemDatabasePlugin extends Plugin
{
	@Override
	protected void startUp()
	{
		log.info("Hello World");
	}

	@Override
	protected void shutDown()
	{
	}
}
