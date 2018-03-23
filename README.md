# EvolutionaryComputing-Robocode
An evolutionary Algorithm using Genetic programming to Evolve Robocode Tanks

## Introduction
The purpose of this report is to detail the steps taken to design, develop and test an evolutionary algorithm and genetic programming techniques when applied against a simulated robot battle.
For this assignment, we develop a basic Robocode robot (tank) and then apply a series of evolutionary and genetic programming techniques against it to ideally generate a better solution than the original.
Purpose/goals

The goal of the project was to see if we could apply a series of genetic programming techniques to Robocode to allow us to visualise/witness evolutionary techniques/behaviours happening in real-time on a micro-level. Evolutionary and genetic algorithms are used to solve problems that may not be able to be solved my traditional analytical and more traditional forms of machine learning, such as Neural networks etc.
My goals with this project where to combine as many potentially interesting and industry supported & proven genetic programming techniques as possible. This was for several reasons, the first being, no single algorithm has been proven to work for all problems, however, a combination of many techniques has been shown to improve final solutions in many instances.
The second was to potentially find a new way of applying these concepts and possibly develop a more robust final solution.
Design and concepts

When first attempting to carry out this task, the first stage was to develop the initial Robot and it’s base framework of behaviours that would be modified by the genetic algorithm.
For this, the framework settled on for the final design, was the robot used in the week 5 Robocode tournament, as with some slight modifying after the tournament, this proved to be able to defeat the ninja and walls robots along with the Spy.
The design of the base robot is pretty simple, it has an a couple of initial variables that are there to aid with altering the robots behaviour later on:

From here, the run() method is called. This is where all the behaviour takes place. When robot is initialised by robocode, the run method is called and starts the initial behaviour.

The rest of the behaviour is based on event/function calls. When a specific event is triggered, the method is called and the behaviour contained within is ran, for example, the OnScannedRobot event controls what happens and what the tank does when it spots another robot. Here, the robot checks it’s own energy and then turns to the opponent robot and fires at it:
 
The rest of the events are developed in a similar way to the onScannedRobot and contain their own behaviour for their own event triggers.
Designing the Program & Evolutionary Algorithm
From here, the goal was to develop the basic algorithm and get the battles running in outside of Robocode’s own application system. For this, the Robocode/libs folder was added to the Java IDE into the external library folder and the Robocode battle arena and engine are loaded into the program:

This allows the battle simulator behaviour to be called without the need to run the battles in the actual battle arena. It also allows for the robocode battle events to be called against the rest of the program.
Inside the battle simulator class is where the Fitness parameters are pulled down from the end of the battle:

For the fitness, I am making use of Co-evolution strategy, to divide the problem up by the length of the bot array (number of population) and assigning that to the fitnesses values to assign each robot to the same fitness value upon starting.

From here, I set about modifying the robot file so that it was able to be handled and manipulated by the program and could compile correctly.
To do this, I decided that a good way of handling things, was to modify the class to have a basic set of parameters that get set by the probram and then pass these parameters to a String that gets written to a file by the file writer.
The first part of this is to have the file writing locations correct, and then write this to the file:

Then, I initiate an instance of the object (the robot) and set the basic parameters:


This allows me to then write this to a .java file and compile the bots:

## The Algorithm & Evolution

The Algorithm and genetic evolution methodology comprise of several stages, the first of these, are the mutation functions which modify the robots after each generation using a Decision Tree node operation, which assigns a two-dimensional tree of nodes, each representing the Genomes of the individual robot within a population.
The population, also has an overall genome type, which is based on the tree nodes being passed into an array of nodes and then having the depth of each node checked and the terminus of the leaf nodes set as the release node. This node is then used to assign the genetic marker of the specific population.
Figure 11 - Create and setting terminus node
 
This, allows for the variation in the population to be calculated and the arity of this variance gets set. Once the node_arity is initialised, the Crossover operation creates the variance in the generic operators by selecting one of the three possible genetic markers:

This then puts the genetic type into the root node for the population and then carries out the mutation of the individual:

The mutatorFunction mutates the genetic ending of the nodes for the individual and then swaps out the subtree’s nodes. Which calls the mutateEnding method, which carries out a random mutation on child node of the initial population.
This then evolves the population, by calling the evolvepop method and intimates the tournament selection based on the genetic mutations that the individuals have had to their genomes.



Before writing the results of the last generation evolution to a .log file and initiating a new pool:

### Evaluation of the Algorithm & Model

When trying to design the program, I knew I wanted to make use of tournament selction, due to the way in which robocode is designed to score the robots based on simple parameters and the winner of each out is the one who survives the longest while also dealing the most damage possible.
This meant that I was able to run a tournament on the population and then take the winner of that tournament and then create a child from that and mutate the next population using decision tree nodes and Crossover to carry out the mutation. As this would in theory provide a consistently good result of fitness, as it is a brute force method of population evolution.
Tournament selection can also be improved by increasing the population size and thus, weaker individuals have a smaller chance of getting mutated and evolved into the next generation, and thus introducing a poor genetic operator into the genome space. Which could be carried on and passed onto a descendant of the population into the next generation.
### Crossover

Crossover is a well established method for carrying out chromosome alteration/variance between generations and populations. Each member of the population gets a varied set of gnomes and this allows them to compared using tournament selection and then mutated. This is because the nature of tournament selection means that the winning genome space will be replicated onto the child/decendant node.

### Decision Tree classification method for Genomes
I decided to use decision tree’s and nodes for the classification of the genomes because they allow for spanning and traversal of the tree before swapping out nodes of the tree. This means that I can carry out the mutation of a node within the tree and place that nodes genome type into the population and then generate the population and evolve with this new gnome type.

