package eu.dsix.magicbees.forestry;

import forestry.api.core.genetics.ForestryTaxa;
import forestry.api.plugin.IGeneticRegistration;
import forestry.apiimpl.plugin.GeneticRegistration;

/** Supplies the missing root taxonomy spine required by ForestryCE addon registration. */
final class ForestryTaxonomyBridge {
    private ForestryTaxonomyBridge() {
    }

    static void registerBeeSpine(IGeneticRegistration genetics) {
        if (!(genetics instanceof GeneticRegistration registration)) {
            throw new IllegalStateException("ForestryCE requires its GeneticRegistration implementation for addon taxa");
        }
        registration.defineDomain(ForestryTaxa.DOMAIN_EUKARYOTA)
                .defineSubTaxon(ForestryTaxa.KINGDOM_ANIMAL, animalia ->
                        animalia.defineSubTaxon(ForestryTaxa.PHYLUM_ARTHROPODS, arthropoda ->
                                arthropoda.defineSubTaxon(ForestryTaxa.CLASS_INSECTS, insecta ->
                                        insecta.defineSubTaxon(ForestryTaxa.ORDER_HYMNOPTERA, order ->
                                                order.defineSubTaxon(ForestryTaxa.FAMILY_BEES)))))
        ;
    }
}