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
