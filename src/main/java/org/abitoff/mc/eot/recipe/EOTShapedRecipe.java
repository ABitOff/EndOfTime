package org.abitoff.mc.eot.recipe;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.WorldTypeEOT;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapedRecipe.Serializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public final class EOTShapedRecipe extends ShapedRecipe
{
	public static final Serializer SERIALIZER = (Serializer) new Serializer()
	{
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			ShapedRecipe recipe = super.read(recipeId, json);
			return new EOTShapedRecipe(recipe);
		}

		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			ShapedRecipe recipe = super.read(recipeId, buffer);
			return new EOTShapedRecipe(recipe);
		}
	}.setRegistryName(Constants.SHAPED_RECIPE_SERIALIZER_RL);

	public EOTShapedRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
			NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn)
	{
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
	}

	private EOTShapedRecipe(ShapedRecipe recipe)
	{
		super(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(),
				recipe.getIngredients(), recipe.getRecipeOutput());
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