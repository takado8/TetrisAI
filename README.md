# TetrisAI

Tetris environment and AI for playing it.

This is a second, upgraded version, capable of breaking current [human Guinness record in NES Tetris](https://www.guinnessworldrecords.com/world-records/607869-highest-score-on-nes-tetris-ntsc) 9 out of 10 times.
###### [*Check out previous version*](https://github.com/takado8/Tetris)

## AI algorithm:

In each turn, for current tetrimino and the following one, every possible move is simulated and the game field is evaluated using four heuristics:
1. ColumnsSummedHeight - summed height of the columns.
2. NumberOfCompleteLines - number of completed lines.
3. NumberOfHoles - number of 'holes' - empty inaccessible spaces.
4. ColumnsSummedHeightDifference - summed difference of adjacent columns height.

Some other features was tested as well, such as points of touch - number of points, where tetrimino touches other tetriminoes or walls, total amount of tetriminoes, 
summedHeight adjusted to overall average height. No improvement was observed and selected above four features were considered sufficient.


each value has a multiplier (weight) (successively a, b, c, d), so the complete equation is as follows:
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

General purpose of genetic algorithms is to maximize (or minimize) a function - here called fitness function, through simplified evolutionary mechanisms observed in natural environment, such as natural selection and genetic variation.

In this tetris environment, a goal is to maximize number of cleaned lines, using field evaluation formula from above.
On start, set of vectors is initialized, each vector [a,b,c,d,...,n] has floats (called genes) in range <-1;1>. Those vectors are called genotypes, and
all together make a population.

Natural selection is based on competition - those with best fitness to environment are more likely to survive and reproduce.
Each subject (genotype) will play one tetris game, using the values of its genes substituted in the field evaluation formula. fitness function is simple: +1 point for 1 cleaned line untill gameover.
Now those with better fitness will reproduce. Two subjects from population are randomly selected and the one with better fitness score will join reproduction pool, where randomly selected
subjects will reproduce untill the population will be restored.
Population is now tested again and whole cycle repeats.

Genetic variability is provided by the crossing-over process and mutation.
In this project, crossing-over process refers to creating a new genotype from existing two, by summing corresponding genes weighted towards more fitted parrent and normalizing derived vector.
```python
    AI crossing_over(AI a, AI b)
    {
        AI c = new AI();
        for (int i = 0; i < a.genotype.Count; i++)
        {
            c.Add(a[i] * a.fitness + b[i] * b.fitness);
        }
        c.normalize();
        return c;
    }
```
A mutation is a random addition to a gene value in the range <-0.1; 0.1>, which appears rather rarely (5% in this project).
```python
    AI mutate(AI c)
    {
        if (Rand.NextDouble() < mutation_rate)
        {
            c[Rand.Next(c.genotype.Count)] += Rand.NextDouble(-0.2, 0.2);
            c.normalize();
        }
        return c;
    }
```

## Results

After several hours the following set of genes has been found:
```python
    a = -0.798752914564018
    b = 0.522287506868767
    c = -0.24921408023878
    d = -0.164626498034284
```
Created AI manage to clean over 2000 lines in one game.

![img1](.gif)

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

