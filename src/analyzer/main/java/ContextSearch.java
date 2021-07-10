public class ContextSearch {
    private SearchAlgorithm algorithm;

    public void setMethod(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int find(byte[] text, byte[] pattern) {
        return this.algorithm.search(text, pattern);
    }
}