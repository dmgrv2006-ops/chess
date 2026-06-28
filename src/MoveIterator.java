public class MoveIterator {
    private Move[] legalMoves;
    private int size;
    private int nextIndex;

    public MoveIterator(Move[] legalMoves,int size){
        this.legalMoves = legalMoves;
        this.size = size;
        nextIndex = 0;
    }
    public boolean hasNext(){
        return size>nextIndex;
    }
    public Move next(){
        return legalMoves[nextIndex++];
    }
}
