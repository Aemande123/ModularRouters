package me.desht.modularrouters.integration.jei;

import me.desht.modularrouters.recipe.EnchantModuleRecipe;
import me.desht.modularrouters.recipe.ResetModuleRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.util.ResourceLocation;

import static me.desht.modularrouters.util.MiscUtil.RL;

@JeiPlugin
public class JEIModularRoutersPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return RL("default");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        List<IRecipe> recipes = ImmutableList.of(
//                new EnchantModuleRecipe(RL("module_enchant")),
//                new ResetModuleRecipe(RL("module_reset"))
//        );
//        registration.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(EnchantModuleRecipe.class, SpecialRecipeCategoryExtension::new);
        registration.getCraftingCategory().addCategoryExtension(ResetModuleRecipe.class, SpecialRecipeCategoryExtension::new);
    }
    //    @Override
//    public void register(IModRegistry registry) {
//        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
//        registry.handleRecipes(ModuleResetRecipe.class, recipe -> new ModuleEnhancementRecipeWrapper(jeiHelpers, recipe), VanillaRecipeCategoryUid.CRAFTING);
//    }
}
