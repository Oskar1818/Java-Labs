import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import static java.lang.System.out;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test methods uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)

public class Neighbours extends Application {

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }  // Constructor, used to initialize


    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used directly in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;

        // TODO -- call functions
        world = getSatisfaction(world,threshold);
    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST, see below!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.4, 0.4, 0.2};
        // Number of locations (places) in world (must be a square)
        int nLocations = 900;   // Should also try 90 000

        // TODO -- call functions
        Actor[] array = generateDistribution(nLocations, dist[0], dist[1], dist[2]);
        fisherYates(array);
        //world = toMatrix(array);
        world = nullPadding(toMatrix(array));

        // Should be last
        fixScreenSize(nLocations);
    }

    // TODO Many methods here, break down of init() and updateWorld()

    Actor[] generateDistribution(int nLocations, double RED, double BLUE, double NONE) {
        Actor[] array = new Actor[nLocations];
        int i = 0;
        while (i < (int) StrictMath.round(RED * nLocations)) {
            array[i] = new Actor(Color.RED);
            i++;
        }
        while (i < (int) StrictMath.round(BLUE * nLocations) + (int) StrictMath.round(RED * nLocations)) {
            array[i] = new Actor(Color.BLUE);
            i++;
        }
        return array;
    }

    private void fisherYates(Actor[] array) {
        int n = array.length;
        Random r = new Random();
        for (int i = 0; i < array.length; i++) {
            int rv = i + r.nextInt(n - i);
            Actor re = array[rv];
            array[rv] = array[i];
            array[i] = re;
        }
    }

    Actor[][] toMatrix(Actor[] array) {
        int row = (int) Math.sqrt(array.length);
        int col;
        col = row;
        Actor[][] matrix = new Actor[row][col];
        int i = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                matrix[r][c] = array[i];
                i++;
            }
        }
        return matrix;
    }

    Actor[][] nullPadding (Actor[][] matrix) {
        int l1 = matrix.length;
        Actor[][] newMatrix = new Actor[l1+2][l1+2]; int l2 = newMatrix.length;
        for (int r = 1; r <= (l2-2); r++) {
            for (int c = 1; c <= (l2-2); c++) {
                newMatrix[r][c] = matrix[r-1][c-1];
            }
        }
        return newMatrix;
    }

    Actor[][] subMatrix(Actor[][] world, int r, int c) {
        Actor[][] submatrix = {{world[r-1][c-1],world[r-1][c],world[r-1][c+1]},
                {world[r][c-1],  world[r][c],  world[r][c+1]},
                {world[r+1][c-1],world[r+1][c],world[r+1][c+1]}};
        return submatrix;
    }

    boolean isSatisfied (Actor[][] world, int row, int col, double threshold) {
        Actor[][] sub = subMatrix(world, row, col);
        int l = sub.length; int count = -1; // Don't want to count myself.
        int total = 0;
        for (int r = 0; r < l; r++) {
            for (int c = 0; c < l; c++){
                if (sub[r][c] != null) {
                    total++;
                    if (sub[r][c] == sub[1][1]) { // doesn't work. Not comparing colour :(
                        count++;
                    }
                }
            }
        }
        if (count / total >= threshold) {
            return true;
        } else {
            return false;
        }
    }

    Actor[][] getSatisfaction(Actor[][] world, double threshold) {
        int size = world.length;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (world[r][c] != null) {
                    if (!isSatisfied(world, r, c, threshold)) {
                        world[r][c].isSatisfied = false;
                    } else {
                        world[r][c].isSatisfied = true;
                    }
                }
            }
        }
        return world;
    }

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO Method to change format of data, generate random etc.

    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, new Actor(Color.BLUE), null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));   // This is a single test
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));

        // TODO  More tests here. Implement and test one method at the time
        // TODO Always keep all tests! Easy to rerun if something happens

        out.println(Arrays.toString(generateDistribution(10, 0.25, 0.25, 0.5)));

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = (double) 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
