import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneticAlgorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    public int elitismCount;
    private int tournamentSize;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, int tournamentSize) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.tournamentSize = tournamentSize;
    }

    public Population initPopulation(int chromosomeLength) {
        return new Population(populationSize, chromosomeLength);
    }

    public double calculateFitness(Individual member) {
        // inverse of the total route length
        int routeLength = 0;
        int leg = 0;
        City home = new City('X');
        City a = home;
        for (int i=0; i<member.getChromosomeLength(); i++) {
            // calculate distance to the next city in the tour
            City b = member.getGene(i);
            leg = a.distanceFrom(b);
            // TEST System.out.println(a.getID() + " to " + b.getID() + " = " + leg);
            routeLength = routeLength + leg;
            a = b; // move forward one position in the tour
        }
        // don't forget the last leg, to return home
        leg = a.distanceFrom(home);
        routeLength = routeLength + leg;

        // calculate reciprocal, so shorter tours = higher fitness
        double fitness = 1/(double)routeLength;
        // cache for efficiency and return
        member.setFitness(fitness);
        return fitness;
    }

    public void evaluatePopulation(Population p) {
        // average fitness of all individuals
        double totalFitness = 0;
        for (Individual i: p.getEveryone()) {
            totalFitness = totalFitness + calculateFitness(i);
        }
        p.setTotalFitness(totalFitness);
    }

    public boolean isTerminationConditionMet(int currentGeneration, int maxGenerations) {
        // return true once we exceed maxGenerations
        return (currentGeneration > maxGenerations);
    }

    // Roulette selection
    public Individual selectParentRoulette(Population p) {
        // spin the roulette wheel to give a value for 0 to total fitness
        double rouletteWheelPosition = Math.random() * p.getTotalFitness();
        // choose a parent with a chance proportional to the fitness
        double spin = 0.0;
        for (Individual member: p.getEveryone()) {
            spin = spin + member.getFitness();
            if (spin >= rouletteWheelPosition) {
                return member;
            }
        }
        // default is to return the last in the pop
        return p.getIndividual(p.size() - 1);
    }

    // Tournament selection
    public Individual selectParentTournament(Population p) {
        // create the tournament
        Population tournament = new Population(tournamentSize);
        // make a temp copy of the population in an ArrayList
        ArrayList<Individual> tempPop = new ArrayList<>(Arrays.asList(p.getEveryone()));
        // move elements randomly over to the tournament
        Random r = new Random();
        for (int i=0; i<tournamentSize; i++) {
            Individual randomPick = tempPop.remove(r.nextInt(tempPop.size())); // nextInt is exclusive on upper bound
            tournament.setIndividual(i, randomPick);
        }
        // now return the winner
        return tournament.getFittest(0);
    }

    public Population crossover(Population p) {
        // return a new generation using Order Crossover
        Population nextGeneration = new Population(p.size()); // start with an empty pop
        p.sortByFitness();
        // loop over the existing pop
        for (int popIndex=0; popIndex<p.size(); popIndex++) {
            Individual parent1 = p.getIndividual(popIndex);
            // will we crossover?
            if (Math.random() < crossoverRate && popIndex >= elitismCount) {
                // find second parent
                Individual parent2 = selectParentTournament(p);
                // TEST dummy parent to check crossover
                //char[] testChromosome = {'T','S','R','Q','P','O','N','M','L','K','J','I','H','G','F','E','D','C','B','A'};
                //Individual parent2 = new Individual(testChromosome);

                // create a 'blank' child
                Individual offspring = new Individual(parent1.getChromosomeLength());
                for (int i=0; i<offspring.getChromosomeLength(); i++) {
                    offspring.setGene(i, new City('-'));
                }


                //********** new bit for OX *************
                // chose the start and end of the sub-sequence
                Random r = new Random();
                int point1 = r.nextInt(parent1.getChromosomeLength()); // nextInt is exclusive on upper bound
                int point2 = r.nextInt(parent1.getChromosomeLength());
                // if point2 is lower, swap them over
                if (point2 < point1) {
                    int temp = point1;
                    point1 = point2;
                    point2 = temp;
                }

                // copy the sub-sequence from parent1 to offspring
                for (int i=point1; i<=point2; i++) {
                    offspring.setGene(i, parent1.getGene(i));
                }

                // move to the next locus on the offspring chromosome
                int offspringLocus = (point2 + 1) % offspring.getChromosomeLength();
                // loop through parent2's chromosome
                for (int i=0; i<parent2.getChromosomeLength(); i++) {
                    int parent2Locus = (i + point2) % parent2.getChromosomeLength();
                    // copy gene if not already in offspring
                    City gene = parent2.getGene(parent2Locus);
                    if (!offspring.containsGene(gene)){
                        offspring.setGene(offspringLocus, gene);
                        // advance to next free locus
                        offspringLocus = (offspringLocus + 1) % offspring.getChromosomeLength();
                    }
                }
                nextGeneration.setIndividual(popIndex, offspring);
        /* TEST show crossover
        System.out.print("parent1  :");
        for(int i=0; i<parent1.getChromosomeLength(); i++) {
          if (i < point1 || i > point2) {
            System.out.print('-');
          } else {
            System.out.print(parent1.getGene(i));
          }
        }
        System.out.println();
        System.out.println("parent2  :" + parent2.toString());
        System.out.println("offspring:" + offspring.toString());
        System.out.println();
        */
            } else {
                // not chosen for crossover
                nextGeneration.setIndividual(popIndex, parent1);
            }
        }
        return nextGeneration;
    }

    public Population mutate(Population p) {
        // randomly mutate the genes in each individual
        Population mutatedPopulation = new Population(p.size()); // start with an empty pop
        p.sortByFitness();
        for (int popIndex=0; popIndex<p.size(); popIndex++) {
            // pluck the next member from the existing population
            Individual member = p.getIndividual(popIndex);
            // chance to mutate each gene in the chromosome (but not for elites)
            if (popIndex < elitismCount) {
                //System.out.println("Not mutating since fitness=" + member.getFitness());
            }
            for (int locus = 0; locus < member.getChromosomeLength(); locus++) {
                if (Math.random() < mutationRate && popIndex >= elitismCount) {
                    // swap 2 genes at random
                    Random r = new Random();
                    int point1 = r.nextInt(member.getChromosomeLength()); // nextInt is exclusive on upper bound
                    City gene1 = member.getGene(point1);
                    int point2 = r.nextInt(member.getChromosomeLength());
                    City gene2 = member.getGene(point2);
                    member.setGene(point1, gene2);
                    member.setGene(point2, gene1);
                }
            }
            mutatedPopulation.setIndividual(popIndex, member);
        }
        return mutatedPopulation;
    }
}
