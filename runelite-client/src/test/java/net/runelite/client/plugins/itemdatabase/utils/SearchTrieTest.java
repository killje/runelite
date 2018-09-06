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
package net.runelite.client.plugins.itemdatabase.utils;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.itemdatabase.util.SearchTrie;
import net.runelite.client.plugins.itemdatabase.util.SearchTrieNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SearchTrieTest
{


	@Test
	public void testInsert()
	{
		SearchTrie trie = new SearchTrie();

		SearchTrieNode rootNode = trie.getRoot();
		assertTrue("Your trie wasn't empty after initialization", rootNode != null && rootNode.getChildren().isEmpty());

		String ironOre = "Iron ore";
		trie.insert(ironOre, ItemID.IRON_ORE);
		SearchTrieNode currentNode = rootNode;
		int i = 0;
		while (i < ironOre.length())
		{
			//test the branch nodes
			assertTrue(
				"The trie is missing a result. Expected (" + i + "): [" + ItemID.IRON_ORE + "], got: " + currentNode.getResults(),
				currentNode.getResults().contains(ItemID.IRON_ORE)
			);
			assertEquals(
				"The trie doesn't have the correct amount of child nodes. Expected (" + i + "): 1, got: " + currentNode.getChildren().size(),
				1,
				currentNode.getChildren().size()
			);
			assertTrue(
				"The trie has nodes that shouldn't be in the tree. Expected (" + i + "): " + ironOre.toLowerCase().charAt(i) + ", got: " + currentNode.getChildren().keySet().iterator().next(),
				currentNode.getChildren().containsKey(ironOre.toLowerCase().charAt(i))
					&& currentNode.getChildren().get(ironOre.toLowerCase().charAt(i)) != null
			);
			currentNode = currentNode.getChildren().get(ironOre.toLowerCase().charAt(i));
			i++;
		}
		//test the leaf node
		assertTrue(
			"The trie is missing a result. Expected (" + i + "): [" + ItemID.IRON_ORE + "], got: " + currentNode.getResults(),
			currentNode.getResults().contains(ItemID.IRON_ORE)
		);
		assertEquals(
			"The trie doesn't have the correct amount of child nodes. Expected (" + i + "): 0, got: " + currentNode.getChildren().size(),
			0,
			currentNode.getChildren().size()
		);
	}

	@Test
	public void testFind()
	{
		SearchTrie trie = new SearchTrie();
		String ironOre = "Iron ore";
		String ironBar = "Iron bar";

		//trie with 1 item
		trie.insert(ironOre, ItemID.IRON_ORE);

		for (int i = 0; i <= ironOre.length(); i++)
		{
			assertTrue(
				"Couldn't find the iron ore result everywhere in the trie",
				trie.find(ironOre.substring(0, i)).contains(ItemID.IRON_ORE)
			);
		}

		for (int i = 0; i <= ironBar.length(); i++)
		{
			assertFalse(
				"Found iron bar result in the trie, when it shouldn't be there",
				trie.find(ironBar.substring(0, i)).contains(ItemID.IRON_BAR)
			);
		}

		//trie with 2 items
		trie.insert(ironBar, ItemID.IRON_BAR);

		for (int i = 0; i <= ironOre.length(); i++)
		{
			assertTrue(
				"Couldn't find the iron ore result everywhere in the trie",
				trie.find(ironOre.substring(0, i)).contains(ItemID.IRON_ORE)
			);
		}

		for (int i = 0; i <= ironBar.length(); i++)
		{
			assertTrue(
				"Couldn't find the iron bar result everywhere in the trie",
				trie.find(ironBar.substring(0, i)).contains(ItemID.IRON_BAR)
			);
		}
	}
}
