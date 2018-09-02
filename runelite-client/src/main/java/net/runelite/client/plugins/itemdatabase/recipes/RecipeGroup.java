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
package net.runelite.client.plugins.itemdatabase.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.api.Item;

public class RecipeGroup
{
	private Map<RecipeID, Recipe> recipes;
	private Map<Integer, List<RecipeID>> recipesWithInputs;
	private Map<Integer, List<RecipeID>> recipesWithOutputs;

	public RecipeGroup()
	{
		recipes = new HashMap<>();
		recipesWithInputs = new HashMap<>();
		recipesWithOutputs = new HashMap<>();
	}

	protected boolean addRecipe(Recipe recipe)
	{
		if (recipes.get(recipe.getId()) != null)
		{
			return false;
		}
		recipes.put(recipe.getId(), recipe);

		for (Item item : recipe.getInputs())
		{
			List<RecipeID> recipesWithInput = recipesWithInputs.computeIfAbsent(item.getId(), k -> new ArrayList<>());
			recipesWithInput.add(recipe.getId());
		}

		for (Item item : recipe.getOutputs())
		{
			List<RecipeID> recipesWithOutput = recipesWithOutputs.computeIfAbsent(item.getId(), k -> new ArrayList<>());
			recipesWithOutput.add(recipe.getId());
		}
		return true;
	}

	protected removeRecipe(Recipe recipe)
	{
		for (Item item : recipe.getOutputs())
		{
			recipesWithOutputs.get(item.getId()).remove(recipe.getId());
		}

		for (Item item : recipe.getInputs())
		{
			recipesWithInputs.get(item.getId()).remove(recipe.getId());
		}

		recipes.remove(recipe.getId());
	}

	public List<Recipe> getRecipesFromInput(int itemID)
	{
		return recipesWithInputs.get(itemID).stream().map((RecipeID id) -> recipes.get(id)).collect(Collectors.toList());
	}

	public List<Recipe> getRecipesFromOutput(int itemID)
	{
		return recipesWithOutputs.get(itemID).stream().map((RecipeID id) -> recipes.get(id)).collect(Collectors.toList());
	}
}
