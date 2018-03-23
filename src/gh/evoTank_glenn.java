package gh;
import robocode.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


// API help : http://robocode.sourceforge.net/docs/robocode/robocode/JuniorRobot.html

/**
 * RobotTest2 - a robot by (your name here)
 */
public class evoTank_glenn {

    final static
    String PATH = new String("/Users/glennhealy/robocode/robots/gh/evoTank_glenn");
    String PACKAGE = new String("gh");
    String JARS = new String("/Users/glennhealy/robocode/libs/robocode.jar");

    final static int chromos = 5,
            node_Depth_min = GenAlg.node_Depth_min,
            node_Depth_max = GenAlg.node_Depth_max;

    static double crossover_root = 0.3, genome_Change = 0.05, crossover_ending = 0.1, mutate_root_Node = 0.05, mutate_endings = 0.15;

    CreateNodes genomeType[] = new CreateNodes[chromos];

    double moveAmount;
    boolean peek;
    double fitness;
    int partofGen = 0, botID = 0, numNodes;


    public String botName = new String();
    String phenometype[] = new String[chromos];
    String RobotsCode = new String();
    String fileName;


    public void make() {
        for (int i = 0; i < chromos; i++) {
            phenometype[i] = genomeType[i].buildNodes();
            setCode();
        }
    }

    public void setDepth(){
        for(CreateNodes exp : genomeType)
            exp.setNodeDepths(0);
    }


    public evoTank_glenn(int gen, int botID) {
        partofGen = gen;
        this.botID = botID;
        botName = "evoTank_glenn" + partofGen + "_" + this.botID;
        fileName = PACKAGE + "." + botName;
    }

    public void init() {
        for (int i = 0; i < chromos; i++) {
            genomeType[i] = new CreateNodes(0);
            genomeType[i].growNodes(0, 0);
        }
    }

    private void setCode() {
        RobotsCode =
                "package " + PACKAGE + ";" +
                        "\n import robocode.util.Utils;" +
                        "\n import java.awt.Color;\n" +
                        "\n import robocode.*;\n" +
                        "import robocode.ScannedRobotEvent;" +
                        "\n" +
                        "\npublic class " + botName + " extends AdvancedRobot {" +
                        "\n" +
                        "\n	public void run() {" +
                        "\n" +
                        "\n	setColors(Color.red,Color.blue,Color.green);" +
                        "\n moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());" +
                        "\n ahead(moveAmount);" +
                        "\n peek = true;" +
                        "\n while(true) {" +
                        "\n turnGunRight(Double.POSITIVE_INFINITY);" +
                        "\n }" +
                        "\n setAdjustGunForRobotTurn(true);" +
                        "\n" +
                        "\n turnLeft(getHeading() %90);" +
                        "\n" +
                        "\n	}" +
                        "\n	public void onScannedRobot(ScannedRobotEvent e) {" +
                        "\n" +
                        "\n    double life = e.getEnergy();\n" +
                        "\n if (life < .50){" +
                        "\n  fire(" + phenometype[4] + ");" +
                        "\n }" +
                        "\n else{" +
                        "\n turnGunRight(" + phenometype[2] + ")" +
                        "\n fire(\"+ phenometype[4] +\");" +
                        "\n }" +

                        "\n		setAhead(" + phenometype[0] + ");" +
                        "\n" +
                        "\n		setTurnRight(" + phenometype[1] + ");" +
                        "\n" +
                        "\n		setTurnGunRight(" + phenometype[2] + ");" +
                        "\n" +
                        "\n		setTurnRadarRight(" + phenometype[3] + ");" +
                        "\n" +
                        "\n		setFire(" + phenometype[4] + ");" +
                        "\n	}" +
                        "\n}";
    }

    String BuildRobot() {
        try {
            FileWriter fstream = new FileWriter(PATH + "/" + botName + ".java");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(RobotsCode);
            out.close();
        } catch (Exception e) {
            System.err.println("error, " + e.getMessage());
        }

        // Compile code
        try {
            RunCode("javac -cp " + JARS + " " + PATH + "/" + botName + ".java");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (PATH + "\\" + botName + ".class");
    }

    private void RunCode(String s) {
        try {
            Process p = Runtime.getRuntime().exec(s);
            if (p.exitValue() != 0) {
                System.out.println("system exited with code " + p.exitValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public evoTank_glenn CrossOver(evoTank_glenn parent2, int genID, int botID){
        evoTank_glenn child_Bot = new evoTank_glenn(genID, botID);

        for (int i = 0; i < chromos; i++) {
            child_Bot.genomeType[i] = this.genomeType[i].cloneNode();

        }
         int chromo1 = new Random().nextInt(chromos);
         int chromo2 = new Random().nextInt(chromos);

        while (chromo1 == chromo2) chromo1 = new Random().nextInt(chromos);

        Random rand = new Random();
        if (rand.nextDouble() < crossover_root) {
            if (rand.nextDouble() < genome_Change) {
                child_Bot.genomeType[chromo2].swapFor(parent2.genomeType[chromo1]);
            }
            else
                child_Bot.genomeType[chromo2].swapFor(parent2.genomeType[chromo2]);

        }
        else {
            boolean ending1 = (rand.nextDouble() < crossover_ending) ? true : false;
            boolean ending2 = (rand.nextDouble() < crossover_ending) ? true : false;

            child_Bot.genomeType[chromo2].putInto(parent2.genomeType[chromo2].getTree(ending1));
            child_Bot.genomeType[chromo1].putInto(parent2.genomeType[chromo1].getTree(ending2));


        }

        return child_Bot;
    }

    public int countNodes() {
        this.numNodes = 0;
        for (int i = 0; i < genomeType.length; i++)
            numNodes += genomeType[i].countNodes();
        return numNodes;
    }

    public evoTank_glenn evolve(int genID, int botID) {
        Random random = new Random();
        evoTank_glenn decendant = new evoTank_glenn(genID, botID);

        for (int i = 0; i < chromos; i++) {
            decendant.genomeType[i] = new CreateNodes(0);
            decendant.genomeType[i].swapFor(this.genomeType[i]);
        }


        decendant.setDepth();
        decendant.numNodes = this.numNodes;
        return decendant;
    }

    public evoTank_glenn mutate(int genID, int botID){
        Random random = new Random();
        evoTank_glenn decendant = new evoTank_glenn(genID, botID);

        for(int i = 0; i < chromos; i++){
            decendant.genomeType[i] = this.genomeType[i].cloneNode();
        }

        int m = random.nextInt(chromos);

        if(random.nextDouble() < mutate_root_Node){	// mutate entire chromosome
            decendant.genomeType[m] = new CreateNodes(0);
            decendant.genomeType[m].growNodes(0, 0);
        }

        else if(random.nextDouble() < mutate_endings){
            decendant.genomeType[m].mutateEnding();
        }else{
            decendant.genomeType[m].mutaterFunction();
        }
        decendant.setDepth();
        decendant.countNodes();
        return decendant;
    }







/*

    public void run() {

        // Initialization of the robot should be put here
        fire(3);
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        peek = false;
        turnLeft(getHeading() %90);
        ahead(moveAmount);
        peek = true;
        fire(1);
        turnGunRight(90);
        turnRight(90);


        // Robot main loop
        while(true) {
            // Replace the next 4 lines with any behavior you would like
            peek = true;
            fire(1);
            ahead(moveAmount);
            peek = false;
            turnRight(90);
            turnGunRight(180);
            fire(3);

        }
    }


    // * onScannedRobot: What to do when you see another robot



    public void onScannedRobot(ScannedRobotEvent e) {
        // Replace the next line with any behavior you would like
        double life = e.getEnergy();

        if (life < .50){
            fire(3);
        }
        else{
            turnGunRight(90);
            turnLeft(90);
            fire(1);
        }

    }


    // * onHitByBullet: What to do when you're hit by a bullet



    public void onHitByBullet(HitByBulletEvent b) {
        // Replace the next line with any behavior you would like
        back(10);
    }

    public void onBulletMissed(BulletMissedEvent m) {
        turnRadarLeft(90);
    }



    // * onHitWall: What to do when you hit a wall



    public void onHitWall() {
        back(20);
    }

    public void onBattleEnded(BattleEndedEvent e){
        double fitness = e.getResults().getScore();

    }

    */


}
