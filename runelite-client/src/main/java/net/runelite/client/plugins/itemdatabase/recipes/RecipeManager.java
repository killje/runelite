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

import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;
import org.reflections.Reflections;

@Singleton
public class RecipeManager
{
	private List<RecipeGroup> recipeGroups;
	private boolean loaded;

	public void loadRecipeGroups()
	{
		recipeGroups = new ArrayList<>();
		Reflections reflections = new Reflections("net.runelite.client.plugins.itemdatabase.recipes");
		Set<Class<? extends RecipeGroup>> recipeGroupClasses = reflections.getSubTypesOf(RecipeGroup.class);

		for (Class<? extends RecipeGroup> recipeGroupClass : recipeGroupClasses)
		{
			Injector injector = RuneLite.getInjector();
			RecipeGroup recipeGroup = injector.getInstance(recipeGroupClass);
			recipeGroups.add(recipeGroup);
		}
		this.loaded = true;
	}

	public List<RecipeGroup> getRecipeGroups()
	{
		return recipeGroups;
	}

	public Map<RecipeGroup, List<Recipe>> getRecipesFromInput(int itemId)
	{
		if (!loaded)
		{
			return null;
		}
		Map<RecipeGroup, List<Recipe>> recipesFromInput = new HashMap<>();
		for (RecipeGroup recipeGroup : recipeGroups)
		{
			List<Recipe> recipes = recipeGroup.getRecipesFromInput(itemId);
			if (!recipes.isEmpty())
			{
				recipesFromInput.put(recipeGroup, recipes);
			}
		}
		return recipesFromInput;
	}

	public Map<RecipeGroup, List<Recipe>> getRecipesFromOutput(int itemId)
	{
		if (!loaded)
		{
			return null;
		}
		Map<RecipeGroup, List<Recipe>> recipesFromOutput = new HashMap<>();
		for (RecipeGroup recipeGroup : recipeGroups)
		{
			List<Recipe> recipes = recipeGroup.getRecipesFromOutput(itemId);
			if (!recipes.isEmpty())
			{
				recipesFromOutput.put(recipeGroup, recipes);
			}
		}
		return recipesFromOutput;
	}

	public boolean isLoaded()
	{
		return loaded;
	}
}
