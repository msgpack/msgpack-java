//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.jackson.dataformat.benchmark;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Benchmarker
{
    private final List<Benchmarkable> benchmarkableList = new ArrayList<Benchmarkable>();

    public abstract static class Benchmarkable
    {
        private final String label;

        protected Benchmarkable(String label)
        {
            this.label = label;
        }

        public abstract void run() throws Exception;
    }

    public void addBenchmark(Benchmarkable benchmark)
    {
        benchmarkableList.add(benchmark);
    }

    private static class Tuple<F, S>
    {
        F first;
        S second;

        public Tuple(F first, S second)
        {
            this.first = first;
            this.second = second;
        }
    }

    public void run(int count, int warmupCount)
            throws Exception
    {
        List<Tuple<String, double[]>> benchmarksResults = new ArrayList<Tuple<String, double[]>>(benchmarkableList.size());
        for (Benchmarkable benchmark : benchmarkableList) {
            benchmarksResults.add(new Tuple<String, double[]>(benchmark.label, new double[count]));
        }

        for (int i = 0; i < count + warmupCount; i++) {
            for (int bi = 0; bi < benchmarkableList.size(); bi++) {
                Benchmarkable benchmark = benchmarkableList.get(bi);
                long currentTimeNanos = System.nanoTime();
                benchmark.run();

                if (i >= warmupCount) {
                    benchmarksResults.get(bi).second[i - warmupCount] = (System.nanoTime() - currentTimeNanos) / 1000000.0;
                }
            }
        }

        for (Tuple<String, double[]> benchmarkResult : benchmarksResults) {
            printStat(benchmarkResult.first, benchmarkResult.second);
        }
    }

    private void printStat(String label, double[] origValues)
    {
        double[] values = origValues;
        Arrays.sort(origValues);
        if (origValues.length > 2) {
            values = Arrays.copyOfRange(origValues, 1, origValues.length - 1);
        }
        StandardDeviation standardDeviation = new StandardDeviation();
        System.out.println(label + ":");
        System.out.println(String.format("  mean : %8.3f", StatUtils.mean(values)));
        System.out.println(String.format("  min  : %8.3f", StatUtils.min(values)));
        System.out.println(String.format("  max  : %8.3f", StatUtils.max(values)));
        System.out.println(String.format("  stdev: %8.3f", standardDeviation.evaluate(values)));
        System.out.println("");
    }
}
