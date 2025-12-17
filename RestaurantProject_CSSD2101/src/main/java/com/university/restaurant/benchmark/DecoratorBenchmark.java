package com.university.restaurant.benchmark;

import com.university.restaurant.model.menu.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark to measure decorator pattern overhead.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class DecoratorBenchmark {

    private MenuItem simpleItem;
    private MenuItem comboItem;

    @Setup
    public void setup() {
        // Simple item
        simpleItem = new Entree(
            "entree-1", 
            "Burger", 
            "Classic burger", 
            9.99, 
            DietaryType.REGULAR, 
            List.of("beef", "bun"), 
            10
        );

        // Combo with multiple items (decorator pattern)
        List<MenuItem> items = new ArrayList<>();
        items.add(new Entree("e1", "Burger", "desc", 9.99, DietaryType.REGULAR, List.of("beef"), 10));
        items.add(new Drink("d1", "Coke", "desc", 2.99, false));
        items.add(new Dessert("de1", "Ice Cream", "desc", 3.99, DietaryType.REGULAR, List.of()));
        
        comboItem = new Combo("combo-1", "Meal Deal", "Burger + Drink + Dessert", items, 10.0);
    }

    @Benchmark
    public double simpleItemPrice() {
        return simpleItem.calculatePrice();
    }

    @Benchmark
    public double comboItemPrice() {
        return comboItem.calculatePrice();
    }

    @Benchmark
    public boolean simpleItemKitchenCheck() {
        return simpleItem.requiresKitchenPrep();
    }

    @Benchmark
    public boolean comboItemKitchenCheck() {
        return comboItem.requiresKitchenPrep();
    }

    // Need to add Combo constructor that takes items
    private static class Combo extends MenuItem {
        private final List<MenuItem> items;
        private final double discountPercent;

        Combo(String id, String name, String description, List<MenuItem> items, double discountPercent) {
            super(id, name, description, 0.0, MenuCategory.COMBO, DietaryType.REGULAR);
            this.items = new ArrayList<>(items);
            this.discountPercent = discountPercent;
        }

        @Override
        public double calculatePrice() {
            double total = items.stream().mapToDouble(MenuItem::calculatePrice).sum();
            return total * (1.0 - discountPercent / 100.0);
        }

        @Override
        public boolean requiresKitchenPrep() {
            return items.stream().anyMatch(MenuItem::requiresKitchenPrep);
        }

        @Override
        List<String> getRequiredIngredients() {
            return items.stream()
                    .flatMap(item -> item.getRequiredIngredients().stream())
                    .distinct()
                    .toList();
        }

        @Override
        public MenuItem copyWithPrice(double newPrice) {
            throw new UnsupportedOperationException("Cannot set price on Combo");
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DecoratorBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
