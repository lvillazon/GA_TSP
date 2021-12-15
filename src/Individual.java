import java.util.Random;

public class Individual {
    City[] chromosome;
    double fitness;

    public Individual(City[] chromosome) {
        this.chromosome = chromosome;
        fitness = -1;
    }

    public Individual(String route) {
        // allow construction from a string of city codes
        chromosome = new City[route.length()];
        for (int i=0; i<route.toCharArray().length; i++) {
            chromosome[i] = new City(route.toCharArray()[i]);
        }
        fitness = -1;
    }

    public Individual(int chromosomeLength) {
        // initialise with random genes
        chromosome = new City[chromosomeLength];
        for (int i=0; i<chromosomeLength; i++) {
            City c = new City(City.validCodes.charAt(i+1));
            chromosome[i] = c;
        }
        randomiseChromosome();
    }

    public City getGene(int offset) {
        return chromosome[offset];
    }

    public void setGene(int offset, City value) {
        chromosome[offset] = value;
    }

    public City[] getChromosome() {
        return chromosome;
    }

    public int getChromosomeLength() {
        return chromosome.length;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double newValue) {
        fitness = newValue;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for(City gene: chromosome) {
            output.append(gene.getID());
        }
        return output.toString();
    }

    public boolean containsGene(City c) {
        for (int i=0; i<getChromosomeLength(); i++) {
            if(getGene(i) == c) {
                return true;
            }
        }
        return false;
    }

    private void randomiseChromosome() {
        for (int i=0; i<getChromosomeLength(); i++) {
            // swap 2 genes at random
            Random r = new Random();
            int point1 = r.nextInt(getChromosomeLength()); // nextInt is exclusive on upper bound
            City gene1 = getGene(point1);
            int point2 = r.nextInt(getChromosomeLength());
            City gene2 = getGene(point2);
            setGene(point1, gene2);
            setGene(point2, gene1);
        }
    }

}
