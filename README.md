# TetrisAI

Tetris environment and AI for playing it.

AI is capable of breaking current [human Guinness record in NES Tetris](https://www.guinnessworldrecords.com/world-records/607869-highest-score-on-nes-tetris-ntsc) 9 out of 10 times.

## AI algorithm:

In each turn, for current tetrimino and the following one, every possible move is simulated and the game field is evaluated using four heuristics:
1. ColumnsSummedHeight - summed height of the columns.
2. NumberOfCompleteLines - number of completed lines.
3. NumberOfHoles - number of 'holes' - empty, inaccessible spaces.
4. ColumnsSummedHeightDifference - summed difference of adjacent columns height.

Some other features was tested as well, such as points of touch - number of points, where tetrimino touches other tetriminoes or walls, total amount of tetriminoes, 
summedHeight adjusted to overall average height. No improvement was observed and selected above four features were considered sufficient.


Each value has a multiplier (weight) (successively a, b, c, d), so the complete equation is as follows:
```python
    field_value = ColumnsSummedHeight * a + NumberOfCompleteLines * b + NumberOfHoles * c + ColumnsSummedHeightDifference * d
```
multipliers values are searched for with genetic algorithm.

In this version of algorithm, simple neural network was introduced to extend learning capabilities. First layer consists of above formula, following layers are fully connected,
and final layer consists of one output neuron. All weights are searched for with genetic algorithm.
Introducing neural network didn't bring expected results, instead, a decrease of efficiency was observed.
Probably, trading-off learning time for more complex network is not optimal for my computing capabilities.
Normalization of inputs and changing activation function did not change results. 


## Genetic algorithm

General purpose of genetic algorithms is to maximize (or minimize) a function - here called a fitness function, through simplified evolutionary mechanisms observed in natural environment, such as natural selection and genetic variation.

In this tetris environment, the goal is to maximize number of cleaned lines, using field evaluation formula from above.
On start, set of vectors is initialized, each vector [a,b,c,d,...,n] has floats (called genes) in range <-1;1>. Those vectors are called genotypes, and
all together make a population.

Natural selection is based on competition - those with best fitness to environment are more likely to survive and reproduce.
Each subject (genotype) will play one tetris game, using the values of its genes substituted in the field evaluation formula. fitness function is simple: +1 point for 1 cleaned line untill gameover.
Now those with better fitness will reproduce. Two subjects from population are randomly selected and the one with better fitness score will join reproduction pool, where randomly selected
subjects will reproduce untill the population will be restored.
Population is now tested again and whole cycle repeats.

Genetic variability is provided by the crossing-over process and mutation.
In this project, crossing-over process refers to creating a new genotype from existing two, by summing corresponding genes weighted towards more fitted parrent and normalizing derived vector. Bias is introduced to avoid multiplying by 0.
```python
    Agent crossingOverGenes(Agent parent1, Agent parent2) {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();
        var newChromosome = new double[NUMBER_OF_GENES];

        for (int i = 0; i < newChromosome.length; i++) {
            newChromosome[i] = parent1Chromosome[i] * (parent1.getFitness() + CROSSING_OVER_BIAS) +
                    parent2Chromosome[i] * (parent2.getFitness() + CROSSING_OVER_BIAS);
        }
        Agent offspring = new Agent(newChromosome);
        offspring.normalizeChromosome();

        return offspring;
    }
```
A mutation is a random addition to a gene value in the range <-0.1; 0.1>, which appears rather rarely (5% in this project).
```python
    void mutate() {
        int mutationIndex = randomGenerator.nextInt(chromosome.length);
        chromosome[mutationIndex] += randomGenerator.nextDouble(-MUTATION_VALUE, MUTATION_VALUE);
        normalizeChromosome();
    }
```

## Results

After few hours of training the AI manages to break current [human Guinness record in NES Tetris](https://www.guinnessworldrecords.com/world-records/607869-highest-score-on-nes-tetris-ntsc) 9 out of 10 times with less than 70,000 tetriminoes. 
Created AI is capable of cleaning over million tetriminoes in one game.

https://user-images.githubusercontent.com/39505866/140616986-10a2e0ac-fdf7-4874-af78-b684310968a9.mp4

## How to use

Clone and build project. Run UIMain class for graphical UI, or HeadlessMain class for fast testing and learning.

### Controls

#### User game mode

- move piece left or right - left and right arrow.
- rotate - up arrow.
- speed up - down arrow.
- drop piece - space

#### Auto and Evolution mode
- slow down - keep down arrow

