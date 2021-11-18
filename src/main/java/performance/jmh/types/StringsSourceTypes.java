package performance.jmh.types;

import net.andreinc.mockneat.types.enums.StringType;
import performance.jmh.InputDataUtils;
import net.andreinc.mockneat.abstraction.MockUnitString;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.andreinc.mockneat.unit.seq.IntSeq.intSeq;
import static net.andreinc.mockneat.unit.text.Strings.strings;
import static performance.jmh.InputDataUtils.DATA_FOLDER;
import static net.andreinc.mockneat.unit.address.Addresses.addresses;
import static net.andreinc.mockneat.unit.misc.Cars.cars;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

public enum StringsSourceTypes {

    KEYS_STRING_1_000(1_000,
            probabilities(String.class)
            .add(0.2, names().full())
            .add(0.2, addresses())
            .add(0.2, words())
            .add(0.2, cars())
            .add(0.2, ints().mapToString())
            .mapToString()),

    KEYS_STRING_10_000(10_000,
            probabilities(String.class)
            .add(0.2, names().full())
            .add(0.2, addresses())
            .add(0.2, words())
            .add(0.2, cars())
            .add(0.2, ints().mapToString())
            .mapToString()),

    KEYS_STRING_100_000(100_000,
            probabilities(String.class)
                    .add(0.2, names().full())
                    .add(0.2, addresses())
                    .add(0.2, words())
                    .add(0.2, cars())
                    .add(0.2, ints().mapToString())
                    .mapToString()),

    KEYS_STRING_1_000_000(1_000_000,
            probabilities(String.class)
            .add(0.2, names().full())
            .add(0.2, addresses())
            .add(0.2, words())
            .add(0.2, cars())
            .add(0.2, ints().mapToString())
            .mapToString()),

    KEYS_STRING_10_000_000(10_000_000,
            probabilities(String.class)
            .add(0.2, names().full())
            .add(0.2, addresses())
            .add(0.2, words())
            .add(0.2, cars())
            .add(0.2, ints().mapToString())
            .mapToString()),

    SEQUENCED_KEYS_1000(1_000,
            intSeq().mapToString()),

    SEQUENCED_KEYS_10_000(10_000,
                        intSeq().mapToString()),

    SEQUENCED_KEYS_100_000(100_000,
                          intSeq().mapToString()),

    SEQUENCED_KEYS_1_000_000(1_000_000,
                           intSeq().mapToString()),

    SEQUENCED_KEYS_10_000_000(10_000_000,
                             intSeq().mapToString()),

    SIX_CHARS_ALPHA_1_000(1_100, strings().size(6).type(StringType.ALPHA_NUMERIC)),

    SIX_CHARS_ALPHA_10_000(10_000, strings().size(6).type(StringType.ALPHA_NUMERIC)),

    SIX_CHARS_ALPHA_100_000(100_000, strings().size(6).type(StringType.ALPHA_NUMERIC)),

    SIX_CHARS_ALPHA_1_000_000(1_000_000, strings().size(6).type(StringType.ALPHA_NUMERIC)),

    SIX_CHARS_ALPHA_10_000_000(10_00_000, strings().size(6).type(StringType.ALPHA_NUMERIC));


    protected int size;
    protected MockUnitString source;

    StringsSourceTypes(int size, MockUnitString source) {
        this.size = size;
        this.source = source;
    }

    public String getFileName() {
        return DATA_FOLDER + this.name() + ".objects";
    }

    public synchronized List<String> getData() throws IOException {
        String fName = getFileName();
        File file = new File(fName);
        if (!file.exists()) {
            source.list(size).serialize(fName);
        }
        return (List<String>) InputDataUtils.readObject(fName);
    }
}

