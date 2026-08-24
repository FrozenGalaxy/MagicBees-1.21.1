package magicbees.forestry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeeSpeciesRuntimePatcherTest {
    @Test
    void optionalOutputsAreInjectedOnlyForPresentLoadout() {
        Map<ResourceLocation, JsonElement> definitions = Map.of(
                id("ae_skystone"), species("Transmutapis"),
                id("te_blizzy"), species("Detestabilapis"),
                id("batty"), species("Fleshyapis"),
                id("scholarly"), species("Doctapis"));
        Set<ResourceLocation> items = Set.of(ResourceLocation.parse("ae2:sky_stone_block"),
                ResourceLocation.parse("thermal:blizz_powder"), ResourceLocation.withDefaultNamespace("gunpowder"),
                id("resource_lore_fragment"));

        Map<ResourceLocation, JsonElement> absent = BeeSpeciesRuntimePatcher.patch(definitions,
                new BeeSpeciesRuntimePatcher.Loadout(false, false, false, false, true), items::contains, tag -> Optional.empty());
        assertFalse(absent.containsKey(id("ae_skystone")));
        assertFalse(absent.containsKey(id("te_blizzy")));
        assertFalse(absent.get(id("batty")).getAsJsonObject().has("specialties"));
        assertFalse(absent.get(id("scholarly")).getAsJsonObject().has("specialties"));

        Map<ResourceLocation, JsonElement> present = BeeSpeciesRuntimePatcher.patch(definitions,
                new BeeSpeciesRuntimePatcher.Loadout(true, true, false, true, true), items::contains, tag -> Optional.empty());
        assertSpecialty(present, "ae_skystone", "ae2:sky_stone_block", 0.02f);
        assertSpecialty(present, "te_blizzy", "thermal:blizz_powder", 0.09f);
        assertSpecialty(present, "batty", "minecraft:gunpowder", 0.33f);
        assertSpecialty(present, "scholarly", "magicbees:resource_lore_fragment", 0.15f);
    }

    @Test
    void unavailableOreSpeciesIsRemovedAndAvailableTagInjectsSpecialty() {
        Map<ResourceLocation, JsonElement> definitions = Map.of(id("silver"), species("Metalliapis"));
        var loadout = new BeeSpeciesRuntimePatcher.Loadout(false, false, false, false, true);
        assertTrue(BeeSpeciesRuntimePatcher.patch(definitions, loadout, item -> false, tag -> Optional.empty()).isEmpty());

        ResourceLocation silver = ResourceLocation.parse("example:silver_nugget");
        Map<ResourceLocation, JsonElement> present = BeeSpeciesRuntimePatcher.patch(definitions, loadout,
                item -> false, tag -> tag.equals(ResourceLocation.parse("c:nuggets/silver")) ? Optional.of(silver) : Optional.empty());
        assertSpecialty(present, "silver", silver.toString(), 0.16f);
    }

    @Test
    void platinumDependentThermalLineIsRemovedUntilItHasARegistrySafeModernSource() {
        Map<ResourceLocation, JsonElement> definitions = Map.of(
                id("te_winsome"), species("Detestabilapis"),
                id("te_endearing"), species("Detestabilapis"));
        var loadout = new BeeSpeciesRuntimePatcher.Loadout(false, true, false, false, true);

        assertTrue(BeeSpeciesRuntimePatcher.patch(definitions, loadout,
                item -> false, tag -> Optional.empty()).isEmpty());

        ResourceLocation platinum = ResourceLocation.parse("example:platinum_nugget");
        Map<ResourceLocation, JsonElement> available = BeeSpeciesRuntimePatcher.patch(definitions, loadout,
                item -> false,
                tag -> tag.equals(ResourceLocation.parse("c:nuggets/platinum"))
                        ? Optional.of(platinum) : Optional.empty());
        assertTrue(available.isEmpty());
    }

    @Test
    void showAllBeesPreservesAlwaysSecretSpecies() {
        JsonObject alwaysSecret = species("Malevolenapis");
        alwaysSecret.addProperty("secret", true);
        Map<ResourceLocation, JsonElement> definitions = Map.of(id("skulking"), alwaysSecret, id("scholarly"), species("Doctapis"));
        Map<ResourceLocation, JsonElement> hidden = BeeSpeciesRuntimePatcher.patch(definitions,
                new BeeSpeciesRuntimePatcher.Loadout(false, false, false, false, false), item -> true, tag -> Optional.empty());
        assertTrue(hidden.get(id("skulking")).getAsJsonObject().get("secret").getAsBoolean());
        assertTrue(hidden.get(id("scholarly")).getAsJsonObject().get("secret").getAsBoolean());
    }

    private static JsonObject species(String genus) {
        JsonObject species = new JsonObject();
        species.addProperty("genus", genus);
        species.add("products", new JsonArray());
        return species;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("magicbees", path);
    }

    private static void assertSpecialty(Map<ResourceLocation, JsonElement> patched, String species, String item, float chance) {
        JsonArray outputs = patched.get(id(species)).getAsJsonObject().getAsJsonArray("specialties");
        assertEquals(1, outputs.size());
        assertEquals(item, outputs.get(0).getAsJsonObject().get("item").getAsString());
        assertEquals(chance, outputs.get(0).getAsJsonObject().get("chance").getAsFloat(), 0.00001f);
    }
}
