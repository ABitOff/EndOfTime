package org.abitoff.mc.eot.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MutationAcceleratorRecipe extends SpecialRecipe
{
	public static final MutationAcceleratorRecipeSerializer SERIALIZER =
			(MutationAcceleratorRecipeSerializer) new MutationAcceleratorRecipeSerializer()
					.setRegistryName(Constants.MUTATION_ACCELERATOR_SERIALIZER_RL);
	private final Map<Item, List<Pair<Item, Float>>> mutationTree;

	public MutationAcceleratorRecipe(ResourceLocation idIn, Map<Item, List<Pair<Item, Float>>> mutationTree)
	{
		super(idIn);
		this.mutationTree = mutationTree;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn)
	{
		return mutationTree.containsKey(inv.getStackInSlot(0).getItem());
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv)
	{
		return null;
	}

	public ItemStack getCraftingResult(CraftingInventory inv, Random rand)
	{
		List<Pair<Item, Float>> results = mutationTree.get(inv.getStackInSlot(0).getItem());
		if (results == null)
			return null;
		float f = rand.nextFloat();
		float sum = 0;
		for (Pair<Item, Float> p: results)
		{
			sum += p.getSecond();
			if (f < sum)
				return p.getFirst().getDefaultInstance();
		}
		// we should never get here, but if we do, assume it's from floating point errors and return the last entry.
		return results.get(results.size() - 1).getFirst().getDefaultInstance();
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return SERIALIZER;
	}

	private static final class MutationAcceleratorRecipeSerializer
			extends SpecialRecipeSerializer<MutationAcceleratorRecipe>
	{
		private static final Logger LOGGER = LogManager.getLogger(MutationAcceleratorRecipeSerializer.class);

		public MutationAcceleratorRecipeSerializer()
		{
			super(null);
		}

		@Override
		public MutationAcceleratorRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			// final container for the mutation tree
			Map<Item, List<Pair<Item, Float>>> finalTree = new HashMap<Item, List<Pair<Item, Float>>>();
			// container for the item groups
			Map<String, List<Pair<Item, Float>>> groups = new HashMap<String, List<Pair<Item, Float>>>();

			JsonArray mutationTree = JSONUtils.getJsonArray(json, "mutation_tree");
			// this first pass over the elements constructs the groups, assigning each item into its specified groups.
			for (JsonElement el: mutationTree)
			{
				JsonObject mtEntry = JSONUtils.getJsonObject(el, "mutation_tree entry");
				Item specimen = JSONUtils.getItem(mtEntry, "specimen");
				JsonArray specimenGroups = JSONUtils.getJsonArray(mtEntry, "groups", null);
				if (specimenGroups == null)
					continue; // skip specimen which don't aren't in any groups
				for (JsonElement groupEl: specimenGroups)
				{
					String groupName;
					float entryWeight;
					if (groupEl.isJsonObject())
					{
						JsonObject groupObj = groupEl.getAsJsonObject();
						groupName = JSONUtils.getString(groupObj, "group");
						entryWeight = JSONUtils.getFloat(groupObj, "weight", 1);
					} else if (groupEl.isJsonPrimitive() && groupEl.getAsJsonPrimitive().isString())
					{
						groupName = groupEl.getAsJsonPrimitive().getAsString();
						entryWeight = 1;
					} else
					{
						throw new JsonSyntaxException(
								"Expected each entry of \"groups\" to be a String or a JsonObject, was "
										+ JSONUtils.toString(groupEl));
					}
					// get the group, creating it if necessary.
					List<Pair<Item, Float>> group =
							groups.computeIfAbsent(groupName, s -> new ArrayList<Pair<Item, Float>>());
					group.add(new Pair<Item, Float>(specimen, entryWeight));
				}
			}

			// this second pass finds all the results for this specimen, including any results only referenced by group.
			for (JsonElement el: mutationTree)
			{
				List<Pair<Item, Float>> itemResults = new ArrayList<Pair<Item, Float>>();
				List<Pair<String, Float>> groupResults = new ArrayList<Pair<String, Float>>();
				JsonObject mtEntry = JSONUtils.getJsonObject(el, "mutation_tree entry");
				Item specimen = JSONUtils.getItem(mtEntry, "specimen");
				JsonArray results = JSONUtils.getJsonArray(mtEntry, "results");
				for (JsonElement result: results)
				{
					String group = null;
					Item item = null;
					float weight;
					if (result.isJsonObject())
					{
						JsonObject resultObj = result.getAsJsonObject();
						if (resultObj.has("group"))
							group = JSONUtils.getString(resultObj, "group");
						else
							item = JSONUtils.getItem(resultObj, "item");
						weight = JSONUtils.getFloat(resultObj, "weight", 1);
					} else if (result.isJsonPrimitive() && result.getAsJsonPrimitive().isString())
					{
						item = JSONUtils.getItem(result, null);
						weight = 1;
					} else
					{
						throw new JsonSyntaxException(
								"Expected each entry of \"results\" to be a String or a JsonObject, was "
										+ JSONUtils.toString(result));
					}
					if (item != null)
					{
						if (group != null)
						{
							// having "item" and "group" defined seems weird and is unintended.
							throw new JsonSyntaxException(
									"Each entry of \"results\" must either define \"item\" or \"group\". Not both! Was "
											+ JSONUtils.toString(result));
						}
						itemResults.add(new Pair<Item, Float>(item, weight));
					}
					if (group != null)
						groupResults.add(new Pair<String, Float>(group, weight));
				}

				// a specimen might have the same result listed twice, or include a result which was already included in
				// a group. we want to make them unique, so we take the last most-explicit definition of the result.
				// (i.e., if the result was included in a group reference and in a "item" reference, we take the "item"
				// reference. if it was included in multiple "item" references, we take the last one we come across.
				Map<Item, Float> uniqueResults = new HashMap<Item, Float>();
				// as we iterate through the elements again, we need to finally start calculating the true percentages
				// from the weights we've been given
				float weightSum = 0;
				for (Pair<String, Float> p: groupResults)
				{
					float weight = p.getSecond();
					List<Pair<Item, Float>> group = groups.get(p.getFirst());
					for (Pair<Item, Float> g: group)
					{
						// multiply the result weight with the group weight to get the actual weight
						float actualWeight = g.getSecond() * weight;
						// since there might be multiple entries for a single item, we need to be careful not to count
						// an item's weight twice. because of this, we use `previous` to check if we replaced an item,
						// and if we did, we need to subtract its weight from the total.
						Float previous = uniqueResults.put(g.getFirst(), actualWeight);
						previous = (previous == null) ? 0 : previous;
						weightSum += actualWeight - previous;
					}
				}
				for (Pair<Item, Float> p: itemResults)
				{
					Float previous = uniqueResults.put(p.getFirst(), p.getSecond());
					previous = (previous == null) ? 0 : previous;
					weightSum += p.getSecond() - previous;
				}

				// must be final for use in a lambda.
				final float sum = weightSum;
				// place all the results in a Pair, calculate their true percentage, collect them all into a list, and
				// finally add it to the tree
				List<Pair<Item, Float>> finalResuls = uniqueResults.entrySet().stream()
						.map(e -> new Pair<Item, Float>(e.getKey(), e.getValue() / sum)).collect(Collectors.toList());
				finalTree.put(specimen, finalResuls);
			}

			LOGGER.info("LOGGING MUTATION TREE");
			for (Entry<Item, List<Pair<Item, Float>>> e: finalTree.entrySet())
			{
				if (e.getValue().size() == 0)
					continue;
				LOGGER.info("{}:", e.getKey().getRegistryName());
				e.getValue().sort((a, b) ->
				{
					int compare = Float.compare(a.getSecond(), b.getSecond());
					if (compare != 0)
						return compare;
					return a.getFirst().getRegistryName().toString()
							.compareToIgnoreCase(a.getFirst().getRegistryName().toString());
				});
				for (Pair<Item, Float> p: e.getValue())
				{
					LOGGER.info("\t{}: {}", p.getFirst().getRegistryName(), p.getSecond());
				}
			}

			return new MutationAcceleratorRecipe(recipeId, ImmutableMap.copyOf(finalTree));
		}

		@Override
		public MutationAcceleratorRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			// does the inverse of write(), packaging up all the data and creating a new recipe instance.
			int treeSize = buffer.readInt();
			Map<Item, List<Pair<Item, Float>>> tree = new HashMap<Item, List<Pair<Item, Float>>>(treeSize);
			for (int i = 0; i < treeSize; i++)
			{
				Item item = Item.getItemById(buffer.readInt());
				int resultsLength = buffer.readInt();
				List<Pair<Item, Float>> results = new ArrayList<Pair<Item, Float>>(resultsLength);
				for (int j = 0; j < resultsLength; j++)
				{
					Item resultantItem = Item.getItemById(buffer.readInt());
					float weight = buffer.readFloat();
					results.add(new Pair<Item, Float>(resultantItem, weight));
				}
				tree.put(item, results);
			}
			return new MutationAcceleratorRecipe(recipeId, ImmutableMap.copyOf(tree));
		}

		@Override
		public void write(PacketBuffer buffer, MutationAcceleratorRecipe recipe)
		{
			// just sends the entire contents of the tree, writing the sizes of collections prior to writing the
			// collections, so the receiver knows what to expect.
			buffer.writeInt(recipe.mutationTree.size());
			for (Entry<Item, List<Pair<Item, Float>>> entry: recipe.mutationTree.entrySet())
			{
				buffer.writeInt(Item.getIdFromItem(entry.getKey()));
				buffer.writeInt(entry.getValue().size());
				for (Pair<Item, Float> p: entry.getValue())
				{
					buffer.writeInt(Item.getIdFromItem(p.getFirst()));
					buffer.writeFloat(p.getSecond());
				}
			}
		}
	}
}
