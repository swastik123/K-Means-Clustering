import java.util.*;


public class Utility {

    public static double calJaccardDist(String centroid, String tweet) {
        List<String> centroidToken = Arrays.asList(centroid.toLowerCase().split(" "));
        List<String> tweetToken = Arrays.asList(tweet.toLowerCase().split(" "));


        Set<String> union = new HashSet<String>(centroidToken);
        union.addAll(tweetToken);

        Set<String> intersection = new HashSet<String>(centroidToken);
        intersection.retainAll(tweetToken);

        return (double) (1 - (intersection.size() / (double) union.size()));

    }
}
