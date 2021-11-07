package performance.jmh.model;

import net.andreinc.neatmaps.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum MapTypes {

    HashMap(() -> new HashMap<>()),
    OALinearProbingMap(() -> new OALinearProbingMap<>()),
    OARobinHoodMap(()->new OARobinHoodMap()),
    OAPYPerturbMap(() -> new OAPyPerturbMap()),
    OACkooSAMap(()->new OACkooSAMap()),
    OAHopscotchMap(()->new OAHopscotchMap_draft());

    MapTypes(Supplier<? extends Map> supplier) {
        this.supplier = supplier;
    }

    Supplier<? extends Map> supplier;

    public Supplier<? extends Map> getSupplier() {
        return supplier;
    }
}
