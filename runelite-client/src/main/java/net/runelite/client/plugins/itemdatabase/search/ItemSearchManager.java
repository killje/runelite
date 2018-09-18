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
package net.runelite.client.plugins.itemdatabase.search;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemdatabase.util.Trie;

@Singleton
public class ItemSearchManager
{
	@Inject
	private ItemManager itemManager;

	private Trie itemTrie;

	public ItemSearchManager()
	{
		itemTrie = new Trie();
		Map<Integer, List<String>> items = Arrays.stream(ItemID.class.getDeclaredFields())
			.filter((Field f) ->{
				int modifiers = f.getModifiers();
				return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && f.getType().getName().equals("int");
			})
			.collect(Collectors.toMap(
				(Field f) ->{
					try
					{
						return (int)f.get(null);
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					return null;
				},
				(Field f) ->{
					return Arrays.asList(f.getName().toLowerCase().split("_"));
				})
			);
		for (Map.Entry<Integer, List<String>> item : items.entrySet())
		{
			for (String itemNameSubstring : item.getValue())
			{
				itemTrie.insert(itemNameSubstring, item.getKey());
			}
		}
	}

	public List<Integer> search(String s)
	{
		return itemTrie.find(s).stream().sorted((Integer id1, Integer id2) -> {
			ItemComposition ic1 = itemManager.getItemComposition(id1);
			ItemComposition ic2 = itemManager.getItemComposition(id2);

			if(ic1.getName().equals(ic2.getName()))
			{
				return 0;
			}

			if (ic1.getName().startsWith(s) && !ic2.getName().startsWith(s))
			{
				return -1;
			}

			if (ic2.getName().startsWith(s) && !ic1.getName().startsWith(s))
			{
				return 1;
			}

			return ic1.getName().compareTo(ic2.getName());
		}).collect(Collectors.toList());
	}
}
