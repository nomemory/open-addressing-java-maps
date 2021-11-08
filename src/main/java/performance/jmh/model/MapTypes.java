package performance.jmh.model;

import net.andreinc.neatmaps.*;
import net.andreinc.neatmaps.drafts.OACkooSAMap_draft;
import net.andreinc.neatmaps.drafts.OALinearProbingRadarMap_draft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum MapTypes {

    HashMap(() -> new HashMap<>()),
    OALinearProbingMap(OALinearProbingMap::new),
    OARobinHoodMap(OARobinHoodMap::new),
    OAPyPerturbMap(OAPyPerturbMap::new);

    MapTypes(Supplier<? extends Map> supplier) {
        this.supplier = supplier;
    }

    Supplier<? extends Map> supplier;

    public Supplier<? extends Map> getSupplier() {
        return supplier;
    }
}
