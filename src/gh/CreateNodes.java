package gh;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by glennhealy on 22/03/2018.
 */
public class CreateNodes {


    final static double
            tree_ending = 0.15,
            Function_1 = 0.2,
            Function_2 = 0.6,
            Function_3 = 0.05,
            Function_4 = 0.15,
            pick_Function[] = {Function_1, Function_2, Function_3, Function_4},
            ending_universal = 0.35,
            ending_Event = 0.4,
            constant_ending = 0.15,
            probability = 0.1,
            pick_probability[] = {ending_universal, ending_Event, constant_ending};

    int node_depth, node_arity = -1;
    String release_node[];
    boolean ending;
    public static int node_Depth_max = GenAlg.node_Depth_max;
    public static int node_Depth_min = GenAlg.node_Depth_min;

    CreateNodes child_node[];

    static Random random = new Random(System.currentTimeMillis());


    public CreateNodes(int node_depth) {
        this.node_depth = node_depth;

    }

    public CreateNodes(int node_depth, int node_arity, boolean ending) {
        this.node_depth = node_depth;
        this.node_arity = node_arity;
        this.ending = ending;
    }

    public void setReleaseNode(int d, int x) {
        if (this.node_arity == 0) {

        }

    }

    public CreateNodes cloneNode() {
        CreateNodes cloneNode = new CreateNodes(this.node_depth, this.node_arity, this.ending);
        cloneNode.release_node = new String[release_node.length];

        for (int i = 0; i < release_node.length; i++) {
            cloneNode.release_node[i] = this.release_node[i];
        }

        if (ending) {
            cloneNode.child_node = null;
        } else {
            cloneNode.child_node = new CreateNodes[this.child_node.length];
            for (int i = 0; i < child_node.length; i++)
                try {
                    cloneNode.child_node[i] = (CreateNodes) this.child_node[i].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }

        return cloneNode;
    }

    public String buildNodes() {
        String built = release_node[0];



        if(this.node_arity == -1)
            System.out.println("Error: arity uninitiated in ExpressionNode");

        // for each child node, recursively compose its arguments
        for(int i = 0; i < this.node_arity; i++)
            built += child_node[i].buildNodes() + release_node[i+1];

        // ensure correct scope
        built = "(" + built + ")";	// ensure correct scope
        return built;
    }

    public void growNodes(int depth, int event) {
        setNode_arity(depth);
        setReleaseNode(depth, event);
    }

    public void setNode_arity(int depth) {

//        child_node = new CreateNodes[this.node_arity];
        if((depth > node_Depth_min && random.nextDouble() < probability) || depth == node_Depth_max){
            // node will be a terminal
            this.node_arity = 0;
            ending = true;
        }else{
            ending = false;

            // pigeon-hole selection
            double pigeon = random.nextDouble();
            for(int i = 0; i < pick_probability.length; i++){
                if((pigeon -= pick_probability[i]) <= 0){
                    this.node_arity = i+1;
                    break;
                }
            }
            if(pigeon > 0){
                System.out.println("Warning: Pigeon-hole overstepped in setArity by "+pigeon);
                this.node_arity = pick_probability.length;
            }
            child_node = new CreateNodes[this.node_arity];
        }

    }


    public void setEnding() {
        release_node = new String[1];
        this.child_node = null;
        double pigeon = random.nextDouble();
        for(int i = 0; i < endings.length; i++){
            if((pigeon -= pick_Function[i]) <= 0){
                release_node[0] = ReleaseNodes[0][i][random.nextInt(ReleaseNodes[0][i].length)];
                break;
            }
        }
        // last hole for pigeon -> ERCs
        if(pigeon > 0){
            if(probability < pigeon) // check that it is not here by mistake
                System.out.println("Warning: Pigeon-hole overstepped in assignTerminal by "+(pigeon - probability));
            // Generate new Ephemeral Random Constant
            release_node[0] = Double.toString(random.nextDouble());
        }
    }

    public void setNodeDepths(int depth) {
        this.node_depth = depth;
        for (int i = 0; i < this.node_arity; i++)
            this.child_node[i].setNodeDepths(depth + 1);
    }

    public CreateNodes getTree(boolean useTerminal) {
        if(useTerminal){
            if(node_arity == 0)
                return this.cloneNode();
            else
                return child_node[random.nextInt(node_arity)].getTree(true);
        }

        else{	// get a non-root, non-terminal node
            int target = random.nextInt(this.lastNode()-1)+1;
            return this.findNode(target);
        }
    }

    public CreateNodes findNode(int target) {
        Random random = new Random();
        if (this.node_depth == target)
            return this;
        else {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i = 0; i < this.node_arity; i++)
                if (child_node[i].lastNode() > target)
                    candidates.add(i);
            int targetBranch = candidates.get(random.nextInt(candidates.size()));
            return child_node[targetBranch].findNode(target);
        }
    }


    public double countNodes() {
        int count = 1;
        for(int i = 0; i < this.node_arity; i++){
            count += this.child_node[i].countNodes();
        }
        return count;
    }

    public int lastNode() {
        int last = this.node_depth;
        for (int i = 0; i < this.node_arity; i++)
            last = Math.max(child_node[i].lastNode(), last);
        return last;
    }

    public int firstEnding() {
        if (ending)
            return this.node_depth;
        else {
            int d = node_Depth_max + 1;
            for (CreateNodes exp : child_node)
                d = Math.min(d, exp.firstEnding());
            return d;
        }
    }


    public void swapFor(CreateNodes newNode) {
        this.node_arity = newNode.node_arity;
        this.ending = newNode.ending;

        this.release_node = new String[newNode.release_node.length];
        for (int i = 0; i < this.release_node.length; i++) {
            this.release_node[i] = newNode.release_node[i];
        }

        if (newNode.ending)
            this.child_node = null;
        else {    // deep-copy the rest of the tree
            this.child_node = new CreateNodes[node_arity];
            for (int i = 0; i < newNode.node_arity; i++) {
                this.child_node[i] = new CreateNodes(node_depth + 1);
                this.child_node[i].swapFor(newNode.child_node[i]);
            }
        }
    }

    public void putInto(CreateNodes newNode) {
        Random random = new Random();
        int deepness = newNode.lastNode() - newNode.node_depth;    // max depth of the subtree
        int highestTerm = newNode.firstEnding();
        int adjustedHighestTerm = highestTerm - newNode.node_depth;

        int bottom = Math.max(1, node_Depth_min - deepness);    // offset selection to keep terminals below MIN_DEPTH
        int ceil = node_Depth_max - deepness;    // limit selection to keep terminals within MAX_DEPTH
        int goal = random.nextInt(ceil - bottom + 1) + bottom;
        int lastNode = this.lastNode();

        if (goal + adjustedHighestTerm < node_Depth_min) {
            goal = node_Depth_min - adjustedHighestTerm;
        }

        if (lastNode < goal) // there are no nodes deep enough, should be put on deepest node
            this.insertAt(newNode, lastNode());
        else {    // putInto at a randomly selected node in range
            this.insertAt(newNode, goal);
        }
    }

    public void insertAt(CreateNodes newNode, int target) {
        if(this.node_depth == target)
            this.swapFor(newNode);
        else{
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for(int i = 0; i < this.node_arity; i++)
                if(child_node[i].lastNode() >= target)
                    candidates.add(i);

            int targetBranch = candidates.get(random.nextInt(candidates.size()));	// randomly select a valid branch
            child_node[targetBranch].insertAt(newNode, target);	// go down that branch

        }
    }

    final static String robocode_endings[] =
            {
                    "getEnergy()",
                    "getHeading()",
                    "getHeight()",
                    "getVelocity()",
                    "getWidth()",
                    "getX()",
                    "getY()",
                    "getDistanceRemaining()",
                    "getGunHeadingRadians()",
                    "getGunTurnRemainingRadians()",
                    "getHeadingRadians()",
                    "getRadarHeadingRadians()",
                    "getRadarTurnRemainingRadians()"
            };

    final static String const_funcs[] =
            {
                    "0.001",
                    "Math.random()",
                    "Math.random()*2 - 1",
                    "Math.floor((Math.random()*10))",
                    "Math.PI",

            };

    final static String OnScanned_Event_endings[] =
            {
                    "e.getBearingRadians()",
                    "e.getDistance()",
                    "e.getEnergy()",
                    "e.getHeadingRadians()",
                    "e.getVelocity()"
            };

    final static String[][] endings =
            {
                    robocode_endings,
                    const_funcs,
                    OnScanned_Event_endings
            };

    final static String Release_1[][] =
            {
                    {"Math.abs(", ")"},
                    {"Math.acos(", ")"},
                    {"Math.asin(", ")"},
                    {"Math.cos(", ")"},
                    {"Math.sin(", ")"},
                    {"Math.toDegrees(", ")"},
                    {"Math.toRadians(", ")"},
                    {"", " * -1"}
            };

    final static String Release_2[][] =
            {
                    {"", " - ", ""},
                    {"", " + ", ""},
                    {"", " * ", ""},
                    {"", " / ", ""},
                    {"Math.min(", ", ", ")"},
                    {"Math.max(", ", ", ")"},
            };

    final static String Release_3[][] =
            {
                    {"", " > 0 ? ", " : ", ""}
            };

    final static String Release_4[][] =
            {
                    {"", " > ", " ? ", " : ", ""},
                    {"", " == ", " ? ", " : ", ""},
            };

    // All expressions available to the GP
    final static String[][][] ReleaseNodes =
            {
                    endings,
                    Release_1,
                    Release_2,
                    Release_3,
                    Release_4
            };


    public void mutateEnding() {
        Random random = new Random();
        if (!this.ending)
            this.child_node[random.nextInt(this.node_arity)].mutateEnding();
        else {
            this.setEnding();
        }
    }

    public void mutaterFunction() {
        Random random = new Random();
        if (this.node_depth == 0)
            this.child_node[random.nextInt(this.node_arity)].mutaterFunction();
        else if (this.node_depth == node_Depth_max - 1 || random.nextDouble() < 0.3) {
            CreateNodes newSubTree = new CreateNodes(this.node_depth);
            newSubTree.growNodes(this.node_depth, 0);
            this.swapFor(newSubTree);
        }
    }
}