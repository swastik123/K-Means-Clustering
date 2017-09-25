

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationRunner {

    private static int K = 25;


    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Please pass four parameters as argument: number of clusters, path to initial seed file, " +
                    "path too tweets data file and output file path");

            return;
        }

        // load various parameters
        K = Integer.parseInt(args[0]);
        String initialSeedFile = args[1];
        String tweetDataSetFile = args[2];
        String outputFile = args[3];

        // read json document and initial seed
        KmeansCluster kmeansCluster = new KmeansCluster(tweetDataSetFile, initialSeedFile);
        kmeansCluster.runKmeansAlgorithm(outputFile);
    }

    public static int getK() {
        return K;
    }
}
