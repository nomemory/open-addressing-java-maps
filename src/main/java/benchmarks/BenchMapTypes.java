package benchmarks;

import net.andreinc.neatmaps.OALinearProbingMap;
import net.andreinc.neatmaps.OAPyPerturbMap;
import net.andreinc.neatmaps.OARobinHoodMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum BenchMapTypes {

    HashMap(() -> new HashMap<>()),
    OALinearProbingMap(() -> new OALinearProbingMap<>()),
    OARobinHoodMap(()->new OARobinHoodMap()),
    OAPYPeturbMap(() -> new OAPyPerturbMap());

    BenchMapTypes(Supplier<? extends Map> supplier) {
        this.supplier = supplier;
    }

    Supplier<? extends Map> supplier;

    public Supplier<? extends Map> getSupplier() {
        return supplier;
    }
}
