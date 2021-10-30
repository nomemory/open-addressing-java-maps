import net.andreinc.neatmaps.OaMapPY;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.andreinc.mockneat.unit.seq.IntSeq.intSeq;
import static net.andreinc.mockneat.unit.types.Ints.ints;

public class OaMapTest {
    @Test
    public void testStrings() {
        var seq = intSeq().increment(1).mapToString();
        var rnd = ints().range(0, 1_000);
        Map<String, String> oaMap = new OaMapPY<>();
//        Map<String, String> oaMap = new HashMap<>();
        String s;
        for(int i = 0; i < 10_000_000; i++) {
            s = seq.get();
            oaMap.put(s, s);
        }

//        for(int i = 0; i < 1_000_000; i++) {
//            s = rnd.get() + "";
//            Assertions.assertEquals(s, oaMap.get(s));
//        }
    }
}
