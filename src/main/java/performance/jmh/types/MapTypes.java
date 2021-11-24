package performance.jmh.types;

import net.andreinc.neatmaps.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum MapTypes {

    HashMap(() -> new HashMap<>()),
    LProbMap(LProbMap::new),
    LProbSOAMap(LProbSOAMap::new),
    LProbBinsMap(LProbBinsMap::new),
    LProbRadarMap(LProbRadarMap::new),
    RobinHoodMap(RobinHoodMap::new),
    PerturbMap(PerturbMap::new);


    MapTypes(Supplier<? extends Map> supplier) {
        this.supplier = supplier;
    }

    Supplier<? extends Map> supplier;

    public Supplier<? extends Map> getSupplier() {
        return supplier;
    }
}
