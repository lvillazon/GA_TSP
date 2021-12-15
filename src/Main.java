import java.util.ArrayList;

public class Main {
    // "main" class
    public static int maxGenerations = 3000;

    public static String nearestNeighbourRoute() {
        // make a tour by choosing the nearest unvisted city each time
        ArrayList<City> unvisited = new ArrayList<>();
        for (int i=1; i<City.validCodes.length()-1; i++) {
            char code = City.validCodes.toCharArray()[i];
            unvisited.add(new City(code));
        }
        char[] route = new char[unvisited.size()];
        City currentCity = new City('X'); // start point
        int i=0;
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
        route[i] = nearestCity.getID();
        i++;
        unvisited.remove(nearestCity);
        currentCity = nearestCity;
    }
    return String.valueOf(route);
  }

    public static void runGA(GeneticAlgorithm ga, int xpos, int ypos) {
        // initialise the population
        Population population = ga.initPopulation(20);
        // Evaluate initial pop
        ga.evaluatePopulation(population);
        int generation = 1;

        // initialise graph for results
        Map map = new Map("Genetic algorithm",400, 300, xpos, ypos);

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
            map.setRoute(fittest.toString(), ga.getRouteLength(fittest));

            // crossover
            population = ga.crossover(population);
            // mutate
            population = ga.mutate(population);
            // revaluate
            ga.evaluatePopulation(population);
            generation++;
        }
    }

    public static void main(String[] args) {

        // Create genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(200, 0.05, .5, 5, 10);

        runGA(ga, 0,0);
        runGA(ga, 400,0);
        runGA(ga, 800,0);

        // try nearest neighbour method
        Map map2 = new Map("Nearest neighbour",400, 300, 0, 350);
        //map2.setRoute("ABCDEFGHIJKLMNOPQRST");
        // create an individual with this route, to check the length
        String route = nearestNeighbourRoute();
        Individual checkLength = new Individual(route);
        map2.setRoute(nearestNeighbourRoute(), ga.getRouteLength(checkLength));

    }
}
