package org.abitoff.mc.eot.recipe;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.WorldTypeEOT;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.item.crafting.ShapelessRecipe.Serializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public final class EOTShapelessRecipe extends ShapelessRecipe
{
	public static final Serializer SERIALIZER = (Serializer) new Serializer()
	{
		public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			ShapelessRecipe recipe = super.read(recipeId, json);
			return new EOTShapelessRecipe(recipe);
		}

		public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			ShapelessRecipe recipe = super.read(recipeId, buffer);
			return new EOTShapelessRecipe(recipe);
		}
	}.setRegistryName(Constants.SHAPELESS_RECIPE_SERIALIZER_RL);

	public EOTShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
			NonNullList<Ingredient> recipeItemsIn)
	{
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
	}

	private EOTShapelessRecipe(ShapelessRecipe recipe)
	{
		super(recipe.getId(), recipe.getGroup(), recipe.getRecipeOutput(), recipe.getIngredients());
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn)
	{
		return super.matches(inv, worldIn) && worldIn.getWorldType() == WorldTypeEOT.get();
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return SERIALIZER;
	}
}