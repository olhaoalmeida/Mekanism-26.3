# Notas de port para MC 26.3 / Mekanism 26.3

Descobertas acumuladas (atualizar a cada rodada).

## Minecraft / NeoForge 26.3
- `ResourceLocation` -> `Identifier`; GUI retained-mode (`GuiGraphicsExtractor`); `PoseStack` 2D = `Matrix3x2fStack` (pushMatrix/popMatrix/translate(x,y)).
- Datapacks: receitas, advancements e loot tables mudaram de formato.
- `assets/<ns>/items/<id>.json` é obrigatório para cada item.

## Mekanism 10.8 (26.3)
- Recipes usam displays: `recipe.getInput().display()`, `getOutputDisplay()` (substitui `getRepresentations()` / `getOutputDefinition()`).
- `TileEntityRecipeMachine<RECIPE, CACHE_LOOKUP extends RecipeCacheLookupMonitor<RECIPE>>` tem 2 parâmetros de tipo.
- `ItemBlockTooltip` não é mais genérica.
- `mekanism.api.chemical.attribute.*` removido: `ChemicalAttributeValidator` agora em `mekanism.api.chemical`.
- `QuadTransformation`, `GuiUpArrow/GuiDownArrow` (usar `GuiTexturedElement.upArrow/downArrow`), `HashedItem`, `Action` removidos/substituídos (transações NeoForge, `ItemResource`).
- `Outlines` movido para `mekanism.client.render.outline`; `ComputerConstants` para `mekanism.common.integration.computer`; `ResourceType` para `mekanism.common.resource`.

## Ferramentas
- `scratchpad/fiximports.py` (relocar imports de classes do Mekanism por nome simples único).
- Workflows: build, boot, client (headless), release, moremachine.

## More Machine -> Mekanism 26.3 (plano da camada de receitas)
- `MekanismRecipe<INPUT, BOOK_CATEGORY, BOOK_INFO>` (3 params) + `Recipe.CommonInfo` + `experience`; receitas de 1 item estendem `SingleInputRecipe.ItemInputRecipe<OUTPUT, CAT, INFO>`.
- Cada receita precisa de `display()` (`List<RecipeDisplay>`), `getOutputDisplay()` (`SlotDisplay`), `getOutputDefinition(ContextMap)`.
- `MekanismRecipeType<INPUT, CAT, INFO, RECIPE, CACHE>` (5 params); `register(name, MekanismRecipeBookCategories.X, cacheFactory)`.
- Book categories registradas por `RecipeBookCategoryDeferredRegister` do Mekanism. Atalho para o MM: reutilizar `*BookCategory/*BookInfo` existentes (ex.: Sawing/Crushing) em vez de registrar novos.
- O MM usa um mixin em `MekanismRecipeType` (registra tipos sob o modid do Mekanism): precisa ser revisto para a nova assinatura.
- Tiles: `getRecipe(int)` devolve `RecipeHolder<R>`; `createNewCachedRecipe(recipe, idx, ContextMap)`; novos: `hasItemOutput()`, `getRecipeContainers(int)`, `energyContainer()`; `updateAndProcess(level)`; `*UpgradeData` novo padrão.
- Ordem: receitas -> registros/tipos -> tiles/fábricas -> GUI/JEI -> modelos.
