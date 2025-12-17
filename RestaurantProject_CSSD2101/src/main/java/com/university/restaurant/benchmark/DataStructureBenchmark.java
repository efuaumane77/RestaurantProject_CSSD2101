package com.university.restaurant.benchmark;

import com.university.restaurant.model.menu.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark comparing Trie vs HashMap for menu item lookups.
 * 
 * Run with: mvn clean test-compile exec:java -Dexec.classpathScope=test 
 *           -Dexec.mainClass=org.openjdk.jmh.Main 
 *           -Dexec.args="DataStructureBenchmark"
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class DataStructureBenchmark {

    private Map<String, MenuItem> hashMap;
    private SimpleTrie trie;
    private List<String> testKeys;
    private Random random;

    @Setup
    public void setup() {
        hashMap = new HashMap<>();
        trie = new SimpleTrie();
        testKeys = new ArrayList<>();
        random = new Random(42);

        // Generate 1000 menu items
        for (int i = 0; i < 1000; i++) {
            String key = "item_" + i;
            MenuItem item = new Drink(key, "Drink " + i, "Description", 5.99, false);
            
            hashMap.put(key, item);
            trie.insert(key, item);
            testKeys.add(key);
        }
    }

    @Benchmark
    public MenuItem hashMapLookup() {
        String key = testKeys.get(random.nextInt(testKeys.size()));
        return hashMap.get(key);
    }

    @Benchmark
    public MenuItem trieLookup() {
        String key = testKeys.get(random.nextInt(testKeys.size()));
        return trie.search(key);
    }

    @Benchmark
    public void hashMapInsert() {
        String key = "new_item_" + random.nextInt(10000);
        MenuItem item = new Drink(key, "New Drink", "Description", 6.99, false);
        hashMap.put(key, item);
    }

    @Benchmark
    public void trieInsert() {
        String key = "new_item_" + random.nextInt(10000);
        MenuItem item = new Drink(key, "New Drink", "Description", 6.99, false);
        trie.insert(key, item);
    }

    // Simple Trie implementation for benchmarking
    private static class SimpleTrie {
        private final TrieNode root = new TrieNode();

        void insert(String key, MenuItem item) {
            TrieNode node = root;
            for (char c : key.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
            }
            node.item = item;
        }

        MenuItem search(String key) {
            TrieNode node = root;
            for (char c : key.toCharArray()) {
                node = node.children.get(c);
                if (node == null) return null;
            }
            return node.item;
        }

        private static class TrieNode {
            Map<Character, TrieNode> children = new HashMap<>();
            MenuItem item;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DataStructureBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
