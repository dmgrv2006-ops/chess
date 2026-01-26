public class Move {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private int movingPiece;
    private int capturedPiece;
    private int promotedPiece;
    private boolean isPromotion;
    private boolean isEnPassant;
    private boolean isCastle;
    private boolean[] savedCastleRights;

    public Move(int fromRow, int fromCol, int toRow, int toCol, int movingPiece){
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movingPiece = movingPiece;
        capturedPiece=0;
        savedCastleRights = new boolean[4];
        isEnPassant = false;
        isPromotion = false;
        isCastle = false;
    }
    public void setCapturedPiece(int capturedPiece){
        this.capturedPiece = capturedPiece;
    }
    public void setEnPassant(boolean enPassant){
        this.isEnPassant = enPassant;
    }
    public void setIsCastle(boolean isCastle){
        this.isCastle = isCastle;
    }
    public void setPromotedPiece(int promotedPiece){
        isPromotion = true;
        this.promotedPiece = promotedPiece;
    }
    public void setSavedCastleRights(int index, boolean canCastle){
        savedCastleRights[index] = canCastle;
    }

    public int compareTo(Move move){
        if(fromRow != move.getFromRow())
            return fromRow- move.getFromRow();
        if(fromCol != move.getFromCol())
            return fromCol- move.getFromCol();
        if(toRow != move.getToRow())
            return toRow- move.getToRow();
        return toCol- move.getToCol();
    }
    public int getFromRow(){return fromRow;}
    public int getFromCol(){return fromCol;}
    public int getToRow(){return toRow;}
    public int getToCol(){return toCol;}
    public int getCapturedPiece(){return capturedPiece;}
    public int getMovingPiece(){return movingPiece;}
    public int getPromotedPiece() {return promotedPiece;}
    public boolean getSaveCastleRights(int i) { return savedCastleRights[i];
    }
    public boolean isEnPassant(){return isEnPassant;}
    public boolean isCastle(){return isCastle;}
    public boolean isPromotion(){
        return isPromotion;
    }
}
