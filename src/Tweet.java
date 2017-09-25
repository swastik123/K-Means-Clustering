
public class Tweet {

    private long tweetID;
    private String tweet;

    public Tweet() {

    }

    public Tweet(long tweetID, String tweet) {
        this.tweetID = tweetID;
        this.tweet = tweet;
    }

    public long getTweetID() {
        return tweetID;
    }

    public void setTweetID(long tweetID) {
        this.tweetID = tweetID;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
