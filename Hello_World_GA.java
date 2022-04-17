 

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;

public class GeneticHelloWorld {
    private static Random random = new Random();

    private static String randomString(final String allowedCharacters, final int length) {
        return IntStream.generate(allowedCharacters::length).limit(length).map(random::nextInt)
                .mapToObj(allowedCharacters::charAt).map(Object::toString).collect(Collectors.joining());
    }

    private static List<String> firstGeneration(final String genes, final int dnaLength, final int count) {
        return Stream.generate(() -> randomString(genes, dnaLength)).limit(count).collect(Collectors.toList());
    }

    private static int fitness(final String testedString, final String perfectString) {
        return IntStream.range(0, min(testedString.length(), perfectString.length()))
                .map(index -> abs(testedString.charAt(index) - perfectString.charAt(index))).sum();
    }

    private static void outputGeneration(final List<String> generation, final String perfectString) {
        generation.stream().sorted(Comparator.comparingInt(string -> fitness(string, perfectString)))
                .peek(string -> System.out.format("%s - ", string)).map(string -> fitness(string, perfectString))
                .forEach(System.out::println);
        System.out.println("----- ----- ----- ----- ----- ----- ----- -----");
    }

    private static List<String> unleashPredators(
            final List<String> generation,
            final String perfectString,
            final int survivors
    ) {
        return generation.stream().sorted(Comparator.comparingInt(string -> fitness(string, perfectString)))
                .limit(survivors).collect(Collectors.toList());
    }

    private static String breed(final String mom, final String dad) {
        return IntStream.range(0, min(mom.length(), dad.length()))
                .mapToObj(index -> random.nextBoolean() ? mom.charAt(index) : dad.charAt(index))
                .map(Object::toString).collect(Collectors.joining());
    }

    private static List<String> repopulate(final List<String> generation, final int offspring) {
        return Stream.generate(() -> breed(
                generation.get(random.nextInt(generation.size())), generation.get(random.nextInt(generation.size()))
        )).limit(offspring).collect(Collectors.toList());
    }

    private static String mutate(final String individual) {
        final int mutationLocation = random.nextInt(individual.length());
        final char newChar = (char) (individual.charAt(mutationLocation) + (random.nextBoolean() ? 1 : -1));
        final StringBuilder builder = new StringBuilder(individual);
        builder.setCharAt(mutationLocation, newChar);
        return builder.toString();
    }

    private static List<String> mutateGeneration(final List<String> generation, final double mutationChance) {
        return generation.stream()
                .map(individual -> random.nextDouble() < mutationChance ? mutate(individual) : individual)
                .collect(Collectors.toList());
    }

    private static final String PERFECT_STRING = "private static";
    private static final int GENERATION_SIZE = 40;
    private static final int SURVIVOR_SIZE = 10;
    private static final double MUTATION_CHANCE = 0.05;
    private static final int GENERATION_COUNT = 1000;

    public static void main(final String... args) {
        List<String> generation = firstGeneration("abcdefghijklmnopqrstuvwxyz", PERFECT_STRING.length(), GENERATION_SIZE);
        outputGeneration(generation, PERFECT_STRING);
        for (int index = 0; index < GENERATION_COUNT; index++) {
            generation = unleashPredators(generation, PERFECT_STRING, SURVIVOR_SIZE);
            generation = repopulate(generation, GENERATION_SIZE);
            generation = mutateGeneration(generation, MUTATION_CHANCE);
            outputGeneration(generation, PERFECT_STRING);
        }
    }
}
