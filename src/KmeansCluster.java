import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class KmeansCluster {
    private List<TweetCluster> lstCluster;
    private Map<Long, Tweet> tweetDataset;


    public KmeansCluster(String tweetDataSetPath, String initialSeedFilePath) {
        tweetDataset = new HashMap<>();
        lstCluster = new ArrayList<>();
        readJson(tweetDataSetPath);
        readInitialSeeds(initialSeedFilePath);
    }

    private void readJson(String tweetDataSetPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(tweetDataSetPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            Tweet tweet = null;
            while ((line = bufferedReader.readLine()) != null) {
                tweet = new Tweet();
                String[] text = line.split("\"text\":");
                String[] tweetId = line.split("\"id\":");
                tweet.setTweet(text[1].substring(1, text[1].indexOf(',')).replaceAll("\"", "").replaceAll("[.!:,'\\''/']", ""));
                tweet.setTweetID(Long.parseLong(tweetId[1].substring(1, tweetId[1].indexOf(',')).replaceAll("\"", "").replaceAll("[.!:,'\\''/']", "")));
                tweetDataset.put(tweet.getTweetID(), tweet);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readInitialSeeds(String initialSeedFilePath) {
        try {
            FileInputStream fin = new FileInputStream(initialSeedFilePath);
            BufferedReader input = new BufferedReader(new InputStreamReader(fin));
            String line;
            while ((line = input.readLine()) != null) {
                Long initialSeed = line.charAt(line.length() - 1) == ',' ? Long.parseLong(line.substring(0, line.length() - 1)) :
                        Long.parseLong(line.substring(0, line.length()));
                if (tweetDataset.containsKey(initialSeed) && lstCluster.size() < ApplicationRunner.getK()) {
                    TweetCluster tweetCluster = new TweetCluster();
                    tweetCluster.setCentroid(tweetDataset.get(initialSeed));
                    lstCluster.add(tweetCluster);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void runKmeansAlgorithm(String outputfile) {
        int maxIterations = 50;
        int iterator = 0;

        while (iterator < maxIterations) {
            // cluster tweets
            clusterTweets();
            // update the centroid
            updateCentroids();
            // find the changes
            int change = 0;
            for (TweetCluster tweetCluster : lstCluster) {
                if (tweetCluster.getOldCentroidTweetID() != tweetCluster.getCentroid().getTweetID()) {
                    change++;
                    break;
                }
            }

            if (change == 0)
                break;

            iterator++;
        }

        double sse = findSSE();
        printCluster(outputfile, sse);
        System.out.println("Squared Error is: " + sse);
    }

    private void clusterTweets() {
        clearClusters();
        double[][] clusterTweetMatrix = new double[lstCluster.size()][tweetDataset.size()];
        ArrayList<Tweet> tweets = getTweets();
        // construct distance matrix
        for (int clusterRow = 0; clusterRow < clusterTweetMatrix.length; clusterRow++) {
            for (int tweetCol = 0; tweetCol < clusterTweetMatrix[0].length; tweetCol++) {
                clusterTweetMatrix[clusterRow][tweetCol] =
                        Utility.calJaccardDist(lstCluster.get(clusterRow).getCentroid().getTweet(),
                                tweets.get(tweetCol).getTweet());
            }
        }
        // extract the shortest cluster for each tweet and add it to that cluster
        for (int tweetCol = 0; tweetCol < clusterTweetMatrix[0].length; tweetCol++) {
            Double min = Double.MAX_VALUE;
            int index = 0;
            for (int clusterRow = 0; clusterRow < clusterTweetMatrix.length; clusterRow++) {
                if (clusterTweetMatrix[clusterRow][tweetCol] < min) {
                    min = clusterTweetMatrix[clusterRow][tweetCol];
                    index = clusterRow;
                }

            }
            lstCluster.get(index).getLstTweets().add(tweets.get(tweetCol));
        }
    }

    private void updateCentroids() {
        for (TweetCluster tweetCluster : lstCluster) {
            int index = 0;
            double min = Double.MAX_VALUE;
            for (int outerTweet = 0; outerTweet < tweetCluster.getLstTweets().size(); outerTweet++) {
                double distance = 0;
                for (int innerTweet = 0; innerTweet < tweetCluster.getLstTweets().size(); innerTweet++) {
                    distance += Utility.calJaccardDist(tweetCluster.getLstTweets().get(outerTweet).getTweet(),
                            tweetCluster.getLstTweets().get(innerTweet).getTweet());
                }

                if (distance < min) {
                    min = distance;
                    index = outerTweet;
                }
            }
            tweetCluster.setCentroid(tweetCluster.getLstTweets().get(index));
        }

    }

    private ArrayList<Tweet> getTweets() {
        ArrayList<Tweet> result = new ArrayList<>();

        for (Map.Entry<Long, Tweet> entry : tweetDataset.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    private void clearClusters() {

        for (TweetCluster tweetCluster : lstCluster) {
            tweetCluster.getLstTweets().clear();
            tweetCluster.setOldCentroidTweetID(tweetCluster.getCentroid().getTweetID());
        }
    }

    public double findSSE() {
        double sse = 0;
        for (TweetCluster tweetCluster : lstCluster) {
            double distance = 0;
            double d = 0;
            for (int k = 0; k < tweetCluster.getLstTweets().size(); k++) {
                d = Utility.calJaccardDist(tweetCluster.getCentroid().getTweet(),
                        tweetCluster.getLstTweets().get(k).getTweet());
                distance += (d * d);
            }
            sse += distance;
        }
        return sse;
    }

    public void printCluster(String outputfilepath, double sse) {

        try (PrintWriter out = new PrintWriter(new FileWriter(outputfilepath, false))) {
            out.println("Cluster No" + "\t" + "Tweet Id");
            for (int i = 0; i < lstCluster.size(); i++) {
                out.print(i + 1 + "\t");
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < lstCluster.get(i).getLstTweets().size(); j++) {
                    sb.append(lstCluster.get(i).getLstTweets().get(j).getTweetID() + ",");
                }
                out.println(sb.toString());
            }
            out.println("The Sqaured Error is: " + sse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
