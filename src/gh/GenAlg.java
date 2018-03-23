package gh;

/**
 * Created by glennhealy on 22/03/2018.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;



public class GenAlg {

    final static int
            population = 50,
            max_gen = 100,
            node_Depth_max = 2,
            node_Depth_min = 7,
            numRounds = 5,
            Tournament_Size = 5,
            Handicap = 20;

    public static double
            CROSSOVER = 0.85,
            REPLICATION = 0.05,
            ARCHITECTURE = 0.0,
            MUTATION = 0.1,
            allFitnesses[] = new double[population],
            allAvgFitnesses[] = new double[max_gen],
            totalFitness,
            avNumNodes[] = new double[max_gen],
            avCountNodes,
            avgFitness;



    final static String[] opponents = { "sitting_duck" };
    static String bots[] = new String[population];
    static int genNumber = 0, bestGen;
    static Random rand;

    static evoTank_glenn
            thePool[] = new evoTank_glenn[population],
            newPool[] = new evoTank_glenn[population],
            members[] = new evoTank_glenn[max_gen],
            currentBest;


    public static void main(String args[]) {

        rand = new Random(System.currentTimeMillis());

        currentBest = new evoTank_glenn(1, 0);
        currentBest.fitness = 0;
        System.out.println("Initializing population");

        initialisepool();
        makepool();

        while(genNumber < max_gen){

            for(int i = 0; i < population; i++)
                bots[i] = thePool[i].fileName;

            setScoreforGroup(opponents);

            totalFitness = 0;
            avgFitness = 0;
            bestGen = 0;
            avCountNodes = 0;

            for(int i=0; i< population; i++){
                totalFitness += (thePool[i].fitness = allFitnesses[i]);
                if(thePool[i].fitness > thePool[bestGen].fitness) bestGen = i;
                    avCountNodes += thePool[i].countNodes();
            }

            avNumNodes[genNumber] = (avCountNodes /= population);

            avgFitness = totalFitness/population;
            allAvgFitnesses[genNumber] = avgFitness;

            // store the bestGen for current gen
            members[genNumber] = thePool[bestGen];
            if(thePool[bestGen].fitness > currentBest.fitness) currentBest = thePool[bestGen];

            System.out.println("\nROUND " + genNumber
                    + "\nAvg. Fitness:\t" + avgFitness + "\t Avg # of nodes: "+avNumNodes[genNumber]
                    + "\nBest In Round:\t" + members[genNumber].botName +" - "+ members[genNumber].fitness
                    + "\t# nodes " + members[genNumber].numNodes
                    + "\nBest So Far:\t" + currentBest.botName +" - "+ currentBest.fitness +"\n");

            WriteData(genNumber, avgFitness, thePool[bestGen].fitness, avCountNodes, thePool[bestGen].numNodes, thePool[bestGen].fileName);

            //if(++genNumber == MAX_GENS) break;
            genNumber++;
            System.out.println("In breeding stage");


            // set newPool as thePool, clear newPool
            thePool = newPool;
            newPool = new evoTank_glenn[population];
            makepool();


        }

    }

    private static void evolvepop(){
        Random random = new Random();
        newPool[0] = members[genNumber-1].evolve(genNumber, 0);
        newPool[1] = currentBest.evolve(genNumber, 1);


        double geneticOporator;
        int newPop = 2;

        while(newPop < population){
            geneticOporator = random.nextDouble();
            if((geneticOporator -= CROSSOVER) <= 0){
                int p1 = tournamentSelect();
                int p2 = tournamentSelect();

                //System.out.println("Crossing over bots " +p1+ " & " +p2+" -> " +newPop);

                newPool[newPop] = thePool[p1].CrossOver(thePool[p2], genNumber, newPop);
                //newPool[newPop] = pool[tournamentSelect()].crossover(pool[tournamentSelect()], genCount+1, newPop);
            }else if((geneticOporator -= CROSSOVER) <= 0){
                //System.out.println("Mutating bot");
                newPool[newPop] = thePool[tournamentSelect()].mutate(genNumber, newPop);
            }else{
                //System.out.println("Replicating Bot");
                newPool[newPop] = thePool[tournamentSelect()].evolve(genNumber, newPop);
            }
            newPop++;
        }

    }


    public static void WriteData(int round, double fit, double fitWin, double node, int BestNode, String bestBot) {
        FileWriter output;

        try {
            output = new FileWriter(evoTank_glenn.PATH+"/round_data.log", true);
            output.write(round+ " " + fit + fitWin + node + BestNode);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void setScoreforGroup(String[] group) {
        RunRoboSim map = new RunRoboSim();
        allFitnesses = map.runGroupings(bots, group, numRounds);
    }

    private static void initialisepool(){
        for(int i = 0; i < population; i++){
            thePool[i] = new evoTank_glenn(0, i);
            thePool[i].init();
        }
    }

    private static void makepool(){
        System.out.println("making the population");
        for(evoTank_glenn bot : thePool){
            bot.make();
            bot.BuildRobot();
        }
    }





    public static int tournamentSelect(){
        int size = Tournament_Size;
        int subPool[] = new int[size];
        for(int i = 0; i < size; i++)
            subPool[i] = rand.nextInt(population);
        int best = subPool[0];
        for(int i = 1; i < size; i++)
            if(thePool[subPool[i]].fitness > thePool[best].fitness) best = subPool[i];
        return best;
    }





}
