package performance.jmh;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import performance.jmh.bechmarks.reads.RandomStringsReads;
import performance.jmh.bechmarks.reads.SequencedStringReads;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws RunnerException, IOException {
        InputDataUtils.cleanBenchDataFolder();

        Options options = new OptionsBuilder()
                // Benchmarks to include
                .include(RandomStringsReads.class.getName())
//                .include(SequencedStringReads.class.getName())
                // Configuration
                .timeUnit(TimeUnit.MICROSECONDS)
                .shouldDoGC(true)
                .resultFormat(ResultFormatType.JSON)
                .addProfiler(GCProfiler.class)
                .result("benchmarks.json")
                .build();

        new Runner(options).run();
    }
}
