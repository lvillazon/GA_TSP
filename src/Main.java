import java.util.ArrayList;

public class Main {
    // "main" class
    public static int maxGenerations = 3000;

/*
  public static String nearestNeighbourRoute() {
    // make a tour by choosing the nearest unvisted city each time
    String;
    ArrayList<City> unvisited = new ArrayList<>();
    for (char code: City.validCodes.toCharArray()) {
      unvisited.add(new City(code));
    }
    char[] route = new char[City.validCodes.length()];
    City currentCity = unvisited.remove(0);
    System.out.println("current:" + currentCity.getID());
    while (unvisited.size() >0) {
        // find the closest unvisited city to this one
        int shortestDistance = 999; // arbitrarily high
        City nearestCity = currentCity;
        for (City c : unvisited) {
            int distance = currentCity.distanceFrom(c);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestCity = c;
            }
        }
        System.out.print(nearestCity.getID());
        route.add(nearestCity.getID());
        unvisited.remove(nearestCity);
        currentCity = nearestCity;
    }

  }
*/

    public static void main(String[] args) {

        // Create genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(200, 0.05, .5, 5, 10);

        // test fitness calculation
        //System.out.println("Tour fitness:");
        //System.out.println(ga.calculateFitness(test));

        // initialise the population
        Population population = ga.initPopulation(20);
        // Evaluate initial pop
        ga.evaluatePopulation(population);
        int generation = 1;

        // initialise graph for results
        Map map = new Map("Genetic algorithm",500, 400);

        // start evolving
        Individual currentBest = population.getFittest(0);
        Individual fittest = population.getFittest(0);
        while (ga.isTerminationConditionMet(generation, maxGenerations) == false) {
            // show best solution so far
            fittest = population.getFittest(0);
            System.out.print("gen " + generation);
            System.out.print(" ave fitness: " + population.getPopulationFitness());
            System.out.print(" Best so far: (" + fittest.getFitness() + "): ");
            System.out.println(fittest.toString());
            map.setRoute(fittest.toString());

            // crossover
            population = ga.crossover(population);
            // mutate
            population = ga.mutate(population);
            // revaluate
            ga.evaluatePopulation(population);
            generation++;
        }
        // TODO graph results


        // try nearest neighbour method
        //Map map2 = new Map("Nearest neighbour",500, 400);
        //map2.setRoute("ABCDEFGHIJKLMNOPQRST");
        //nearestNeighbourRoute();

    }
}
