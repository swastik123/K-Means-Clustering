import java.util.ArrayList;
import java.util.List;


public class TweetCluster {

    private List<Tweet> lstTweets;
    private Tweet centroid;
    private Long oldCentroidTweetID;

    public TweetCluster() {
        lstTweets = new ArrayList<>();
    }

    public List<Tweet> getLstTweets() {
        return lstTweets;
    }

    public Tweet getCentroid() {
        return centroid;
    }

    public void setCentroid(Tweet centroid) {
        this.centroid = centroid;
    }

    public Long getOldCentroidTweetID() {
        return oldCentroidTweetID;
    }

    public void setOldCentroidTweetID(Long oldCentroidTweetID) {
        this.oldCentroidTweetID = oldCentroidTweetID;
    }
}
