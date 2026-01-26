public class Game {
    private final int CHESS_BOARD_ROWS = 8;
    private final int CHESS_BOARD_COLS = 8;
    private final int WHITE_BACK_ROW = 7;
    private final int WHITE_PAWN_ROW = 6;
    private final int BLACK_BACK_ROW = 0;
    private final int BLACK_PAWN_ROW = 1;
    private final int MAX_MOVES = 256;
    private final int PAWN = 1;
    private final int KNIGHT = 2;
    private final int BISHOP = 3;
    private final int ROOK = 4;
    private final int QUEEN = 5;
    private final int KING = 6;
    private final int EMPTY = 0;
    private final int WHITE_PAWN = 1;
    private final int WHITE_KNIGHT = 2;
    private final int WHITE_BISHOP = 3;
    private final int WHITE_ROOK = 4;
    private final int WHITE_QUEEN = 5;
    private final int WHITE_KING = 6;
    private final int BLACK_PAWN = -1;
    private final int BLACK_KNIGHT = -2;
    private final int BLACK_BISHOP = -3;
    private final int BLACK_ROOK = -4;
    private final int BLACK_QUEEN = -5;
    private final int BLACK_KING = -6;
    private final int PAWN_DOUBLE_MOVE = 2;
    private final int KING_CASTLE_MOVE = 2;
    private final int[][] KNIGHT_MOVESET = {
            {1,2}, {-1,2}, {1,-2}, {-1,-2},
            {2,1}, {2,-1}, {-2,1}, {-2,-1}
    };
    private final int[][] KING_MOVESET = {
        {-1,-1},{-1, 0},{-1, 1},
        { 0,-1},        { 0, 1},
        { 1,-1},{ 1, 0},{ 1, 1}
    };
    private final int[][]  BISHOP_DIRECTIONS = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private final int[][] ROOK_DIRECTIONS = {{1,0},{-1,0},{0,1},{0,-1}};
    private final int[][] QUEEN_DIRECTIONS = {{1,1},{1,-1},{-1,1},{-1,-1},
                                             {1,0},{-1,0},{0,1},{0,-1}};
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private boolean canWhiteSCastle;
    private boolean canBlackSCastle;
    private boolean canWhiteLCastle;
    private boolean canBlackLCastle;
    private int legalMovesSize;
    private int pastMovesSize;
    private int undoMovesSize;
    private int[][] chessBoard;
    private Move[] legalMoves;
    private Move[] pastMoves;
    private Move[] undoMoves;

    public Game(){
        chessBoard = new int[CHESS_BOARD_ROWS][CHESS_BOARD_COLS];
        legalMoves = new Move[MAX_MOVES];
        legalMovesSize = 0;
        pastMoves = new Move[MAX_MOVES];
        pastMovesSize = 0;
        undoMoves = new Move[MAX_MOVES];
        undoMovesSize = 0;
        isWhiteTurn = true;
        isGameOver = false;
        canWhiteSCastle = true;
        canBlackSCastle = true;
        canWhiteLCastle = true;
        canBlackLCastle = true;
    }
    private void fillWhitePieces(){
        chessBoard[WHITE_BACK_ROW][0] = WHITE_ROOK;
        chessBoard[WHITE_BACK_ROW][1] = WHITE_KNIGHT;
        chessBoard[WHITE_BACK_ROW][2] = WHITE_BISHOP;
        chessBoard[WHITE_BACK_ROW][3] = WHITE_QUEEN;
        chessBoard[WHITE_BACK_ROW][4] = WHITE_KING;
        chessBoard[WHITE_BACK_ROW][5] = WHITE_BISHOP;
        chessBoard[WHITE_BACK_ROW][6] = WHITE_KNIGHT;
        chessBoard[WHITE_BACK_ROW][7] = WHITE_ROOK;
        for(int i=0;i<CHESS_BOARD_COLS;i++)
            chessBoard[WHITE_PAWN_ROW][i] = WHITE_PAWN;
    }
    private void fillEmpty(){
        for(int row = 2;row<6;row++){
            for(int col=0;col<CHESS_BOARD_COLS;col++)
                chessBoard[row][col] = EMPTY;
        }
    }
    private void fillBlackPieces(){
        chessBoard[BLACK_BACK_ROW][0] = BLACK_ROOK;
        chessBoard[BLACK_BACK_ROW][1] = BLACK_KNIGHT;
        chessBoard[BLACK_BACK_ROW][2] = BLACK_BISHOP;
        chessBoard[BLACK_BACK_ROW][3] = BLACK_QUEEN;
        chessBoard[BLACK_BACK_ROW][4] = BLACK_KING;
        chessBoard[BLACK_BACK_ROW][5] = BLACK_BISHOP;
        chessBoard[BLACK_BACK_ROW][6] = BLACK_KNIGHT;
        chessBoard[BLACK_BACK_ROW][7] = BLACK_ROOK;
        for(int i=0;i<CHESS_BOARD_COLS;i++)
            chessBoard[BLACK_PAWN_ROW][i] = BLACK_PAWN;
    }
    public void createBoard() {
        fillWhitePieces();
        fillEmpty();
        fillBlackPieces();
    }
    public int[][] getChessBoard() {
        return chessBoard;
    }

    private boolean isInCheck() {
        int king;
        if(isWhiteTurn)
            king = WHITE_KING;
        else
            king = BLACK_KING;
        int kingRow = 0;
        int kingCol= 0;
        boolean found = false;
        for(int row = 0; row < CHESS_BOARD_ROWS && !found; row++) {
            for(int col = 0; col < CHESS_BOARD_COLS && !found; col++) {
                if(chessBoard[row][col] == king) {
                    kingRow = row;
                    kingCol = col;
                    found = true;
                }
            }
        }
       return isAttacked(kingRow,kingCol);
    }
    private boolean isAttacked(int row, int col){
        return  hasPawnAttacks(row, col) || hasKnightAttacks(row, col)
                || hasKingAttacks(row,col) || hasBishopAttacks(row, col)
                || hasRookAttacks(row,col) || hasQueenAttacks(row,col);
    }

    private boolean hasPawnAttacks(int kingRow, int kingCol) {
        int enemyPawn;
        if(isWhiteTurn)
            enemyPawn = BLACK_PAWN;
        else
            enemyPawn = WHITE_PAWN;
        int direction;
        if(isWhiteTurn)
            direction = -1;
        else
            direction = 1;

        if(isWithinBoard(kingRow + direction, kingCol - 1)
                && chessBoard[kingRow + direction][kingCol - 1] == enemyPawn)
            return true;

        return isWithinBoard(kingRow + direction, kingCol + 1)
                && chessBoard[kingRow + direction][kingCol + 1] == enemyPawn;
    }

    private boolean hasKnightAttacks(int kingRow, int kingCol){
        boolean hasCheck = false;
        int i = 0;
        int enemyKnight;
        if(isWhiteTurn)
            enemyKnight = BLACK_KNIGHT;
        else
            enemyKnight = WHITE_KNIGHT;
        do{
            int toRow = kingRow + KNIGHT_MOVESET[i][0];
            int toCol = kingCol + KNIGHT_MOVESET[i][1];
            if(isWithinBoard(toRow, toCol) && chessBoard[toRow][toCol] == enemyKnight)
                hasCheck = true;
            i++;
        } while(i<KNIGHT_MOVESET.length && !hasCheck);
        return hasCheck;
    }
    private boolean hasKingAttacks(int kingRow, int kingCol){
        boolean hasCheck = false;
        int i = 0;
        int enemyKing;
        if(isWhiteTurn)
            enemyKing = BLACK_KING;
        else
            enemyKing = WHITE_KING;
        do{
            int toRow = kingRow + KING_MOVESET[i][0];
            int toCol = kingCol + KING_MOVESET[i][1];
            if(isWithinBoard(toRow, toCol)  && chessBoard[toRow][toCol] == enemyKing)
                hasCheck = true;
            i++;
        }while(i<KING_MOVESET.length && !hasCheck);
        return hasCheck;
    }
    private boolean hasBishopAttacks(int kingRow, int kingCol){
        int enemyBishop;
        if(isWhiteTurn)
            enemyBishop = BLACK_BISHOP;
        else
            enemyBishop = WHITE_BISHOP;
        return checkSlidingPiecesChecks(kingRow,kingCol,BISHOP_DIRECTIONS,enemyBishop);
    }
    private boolean hasRookAttacks(int kingRow, int kingCol){
        int enemyRook;
        if(isWhiteTurn)
            enemyRook = BLACK_ROOK;
        else
            enemyRook = WHITE_ROOK;
        return checkSlidingPiecesChecks(kingRow,kingCol, ROOK_DIRECTIONS,enemyRook);
    }

    private boolean hasQueenAttacks(int kingRow, int kingCol){
        int enemyQueen;
        if(isWhiteTurn)
            enemyQueen = BLACK_QUEEN;
        else
            enemyQueen = WHITE_QUEEN;
        return checkSlidingPiecesChecks(kingRow,kingCol, QUEEN_DIRECTIONS,enemyQueen);
    }

    private boolean checkSlidingPiecesChecks(int kingRow, int kingCol, int[][] directions, int enemyPiece){
        boolean foundCheck = false;
        int i = 0;
        while(i<directions.length && !foundCheck) {
            int j = 1;
            while(isWithinBoard(kingRow + j*directions[i][0], kingCol + j*directions[i][1])){
                int row = kingRow + j*directions[i][0];
                int col = kingCol + j*directions[i][1];
                if(chessBoard[row][col] == enemyPiece) {
                    foundCheck = true;
                    break;
                } else if(!isEmpty(row,col))
                    break;
                j++;

            }
            i++;
        }
        return foundCheck;
    }


    public void checkLegalMoves() {
        legalMovesSize = 0;
        for(int row = 0;row<CHESS_BOARD_ROWS;row++){
            for(int col = 0;col<CHESS_BOARD_COLS;col++){
                if(chessBoard[row][col] == EMPTY)
                    continue;

                if(isPiecesTurn(row, col))
                    checkPiece(row,col);
            }
        }
    }

    private void checkPiece(int row, int col){
        switch (Math.abs(chessBoard[row][col])){
            case (PAWN) -> checkPawnMoves(row,col);
            case (KNIGHT) -> checkKnightMoves(row,col);
            case (BISHOP) -> checkBishopMoves(row,col);
            case (ROOK)->checkRookMoves(row,col);
            case (QUEEN)-> checkQueenMoves(row,col);
            case (KING)-> checkKingMoves(row,col);
        }
    }
    public boolean hasLegalMoves(){
        return legalMovesSize >0;
    }

    private boolean isWithinBoard(int row, int col){
        return row>=0 && col>=0 && row<CHESS_BOARD_ROWS && col<CHESS_BOARD_COLS;
    }
    private boolean isEnemyPiece(int row, int col){
        return (isWhiteTurn && chessBoard[row][col]<0) || (!isWhiteTurn && chessBoard[row][col]>0);
    }

    private boolean isEmpty(int row, int col){
        return chessBoard[row][col] == EMPTY;
    }
    private boolean isOnStartingRank(int row){
        return (row == 6 && isWhiteTurn) || (row == 1 && !isWhiteTurn);
    }
    private boolean canDoubleAdvance(int row,int col, int direction){
        return isWithinBoard(row+direction*PAWN_DOUBLE_MOVE, col)
                && isOnStartingRank(row)
                && isEmpty(row+direction*PAWN_DOUBLE_MOVE, col);
    }
    private boolean wereAnyMovesMade(){
        return pastMovesSize >0;
    }
    private boolean wasLastMoveDoubleAdvance(int row, int col) {
        if(wereAnyMovesMade()) {
            Move lastMove = pastMoves[pastMovesSize - 1];
            return lastMove.getMovingPiece() == PAWN
                    && lastMove.getToRow() == row && lastMove.getToCol() == col
                    && Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == PAWN_DOUBLE_MOVE;
        }
        return false;
    }
    private Move createMove(int fromRow, int fromCol, int toRow, int toCol, int movingPiece){
        Move move = new Move(fromRow,fromCol,toRow,toCol, movingPiece);
        int capturedPiece = chessBoard[toRow][toCol];
        move.setCapturedPiece(capturedPiece);
        if(isEnPassant(movingPiece,fromCol,toCol,capturedPiece))
            move.setEnPassant(true);
        return move;
    }
    private boolean isEnPassant(int movingPiece, int fromCol, int toCol, int capturedPiece){
        return movingPiece == PAWN  && fromCol != toCol && capturedPiece == EMPTY;
    }
    private void checkPawnMoves(int row, int col) {
        int direction;
        if(isWhiteTurn)
            direction = -1;
        else
            direction = 1;
        checkPawnAdvances(row, col, direction);
        checkPawnCaptures(row, col, direction);
        checkEnPassant(row, col, direction);

    }
    private void checkPawnAdvances(int row,int col, int direction){
        if(isWithinBoard(row+direction, col) && isEmpty(row+direction, col)) {
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction, col);
            }else
                addMove(createMove(row,col,row+direction, col, PAWN));
            if (canDoubleAdvance(row, col, direction)) {
                addMove(createMove(row, col, row+direction*PAWN_DOUBLE_MOVE, col,PAWN));
            }
        }
    }
    private void checkPawnCaptures(int row,int col, int direction){
        if(isWithinBoard(row+direction,col+1) && isEnemyPiece(row+direction, col+1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col+1);
            }else
                addMove(createMove(row,col,row+direction,col+1,PAWN));
        if(isWithinBoard(row+direction,col-1) && isEnemyPiece(row+direction, col-1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col-1);
            }else
                addMove(createMove(row,col,row+direction,col-1,PAWN));
    }
    private void checkEnPassant(int row,int col, int direction){
        if(isWithinBoard(row+direction,col+1)
                && wasLastMoveDoubleAdvance(row, col+1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col+1);
            }else
                addMove(createMove(row,col,row+direction,col+1,PAWN));
        if(isWithinBoard(row+direction,col-1)
                && wasLastMoveDoubleAdvance(row, col-1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col-1);
            }else
                addMove(createMove(row,col,row+direction,col-1,PAWN));
    }
    private void addPromotionMoves(int fromRow, int fromCol, int toRow, int toCol){
        int[] promotionPieces = {KNIGHT,BISHOP,ROOK,QUEEN};
        for(int i = 0;i<promotionPieces.length;i++) {
            Move move = createMove(fromRow,fromCol,toRow,toCol,PAWN);
            move.setPromotedPiece(promotionPieces[i]);
            addMove(move);
        }
    }


    private void checkKnightMoves(int fromRow, int fromCol) {
        for(int i = 0; i< KNIGHT_MOVESET.length;i++) {
            int toRow = fromRow + KNIGHT_MOVESET[i][0];
            int toCol = fromCol + KNIGHT_MOVESET[i][1];
            if(isWithinBoard(toRow, toCol)
                    && (isEmpty(toRow,toCol) || isEnemyPiece(toRow,toCol)))
                addMove(createMove(fromRow, fromCol, toRow,toCol, KNIGHT));
        }
    }


    private void checkKingMoves(int fromRow, int fromCol) {
        for(int i = 0; i< KING_MOVESET.length;i++) {
            int toRow = fromRow + KING_MOVESET[i][0];
            int toCol = fromCol + KING_MOVESET[i][1];
            if(isWithinBoard(toRow, toCol)
                    && (isEmpty(toRow,toCol) || isEnemyPiece(toRow,toCol)))
                addMove(createMove(fromRow, fromCol, toRow,toCol, KING));
        }
        if(hasShortCastleConditions(fromRow, fromCol)) {
            Move move = createMove(fromRow, fromCol, fromRow,fromCol+KING_CASTLE_MOVE, KING);
            move.setIsCastle(true);
            addMove(move);
        }
        if(hasLongCastleConditions(fromRow, fromCol)) {
            Move move = createMove(fromRow, fromCol, fromRow,fromCol-KING_CASTLE_MOVE, KING);
            move.setIsCastle(true);
            addMove(move);
        }
    }

    private boolean hasShortCastleConditions(int fromRow, int fromCol) {
        int kingRow;
        boolean hasShortCastleRights;
        if(isWhiteTurn) {
            kingRow = WHITE_BACK_ROW;
            hasShortCastleRights = canWhiteSCastle;
        }
        else {
            kingRow = BLACK_BACK_ROW;
            hasShortCastleRights = canBlackSCastle;
        }

        if(fromCol ==4 && fromRow == kingRow) {
            int i = 0;
            while (hasShortCastleRights && i < 3) {
                if (isAttacked(fromRow, fromCol + i)
                        || (i>0 &&!isEmpty(fromRow, fromCol + i)))
                    hasShortCastleRights = false;
                else
                    i++;
            }
            return hasShortCastleRights;
        }else
            return false;
    }
    private boolean hasLongCastleConditions(int fromRow, int fromCol) {
        int kingRow;
        boolean hasLongCastleRights;
        if(isWhiteTurn) {
            kingRow = WHITE_BACK_ROW;
            hasLongCastleRights = canWhiteLCastle;
        }
        else {
            kingRow = BLACK_BACK_ROW;
            hasLongCastleRights = canBlackLCastle;
        }
        if(fromCol ==4 && fromRow == kingRow) {
            int i = 0;
            while(hasLongCastleRights && i <3){
                if(isAttacked(fromRow,fromCol-i)
                        || (i>0 &&!isEmpty(fromRow, fromCol - i)))
                    hasLongCastleRights = false;
                else
                    i++;
            }
        return hasLongCastleRights;
        }else
            return false;
    }

    private void checkBishopMoves(int fromRow, int fromCol) {
        checkSlidingPiecesMoves(fromRow, fromCol,BISHOP_DIRECTIONS,BISHOP);
    }

    private void checkRookMoves(int fromRow, int fromCol) {
        checkSlidingPiecesMoves(fromRow,fromCol, ROOK_DIRECTIONS,ROOK);
    }
    private void checkQueenMoves(int fromRow, int fromCol) {
        checkSlidingPiecesMoves(fromRow,fromCol, QUEEN_DIRECTIONS,QUEEN);
    }


    private void checkSlidingPiecesMoves(int fromRow, int fromCol, int[][] directions, int piece){
        for(int i = 0; i<directions.length;i++){
            int j = 1;
            while(isWithinBoard(fromRow + j*directions[i][0], fromCol + j*directions[i][1])){
                int toRow = fromRow + j*directions[i][0];
                int toCol = fromCol + j*directions[i][1];
                if(isEmpty(toRow, toCol))
                    addMove(createMove(fromRow, fromCol,toRow, toCol, piece));
                else if(isEnemyPiece(toRow, toCol)) {
                    addMove(createMove(fromRow, fromCol,toRow, toCol, piece));
                    break;
                } else
                    break;
                j++;
            }
        }
    }



    private void addMove(Move move){
        movePiece(move);

        if (!isInCheck()) {
            legalMoves[legalMovesSize++] = move;
        }
        undoMove(move);
    }
    private boolean hasPromotionConditions( int toRow){
        return (isWhiteTurn && toRow == BLACK_BACK_ROW) || (!isWhiteTurn && toRow==WHITE_BACK_ROW);
    }
    public void changeTurn() {
        isWhiteTurn= !isWhiteTurn;
    }

    public boolean isPieceThere(int row, int col) {
        return chessBoard[row][col] !=0;
    }

    public boolean isPiecesTurn(int row, int col) {
        return (isWhiteTurn && chessBoard[row][col] >0)
                || (!isWhiteTurn && chessBoard[row][col] <0);
    }
    public Move getLegalMoves(int fromRow, int fromCol, int toRow, int toCol, int promotionIndex) {
        int movingPiece = Math.abs(chessBoard[fromRow][fromCol]);
        Move move = new Move(fromRow, fromCol, toRow, toCol,movingPiece);
        int i=0;
        int j =-1;
        while(i < legalMovesSize){
            if(legalMoves[i].compareTo(move)==0) {
                j++;
                if(j==promotionIndex)
                    break;
            }
            i++;
        }
        if(i < legalMovesSize)
            return legalMoves[i];
        else return null;
    }

    public void movePiece(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        int piece = chessBoard[fromRow][fromCol];
        int turn;
        if (isWhiteTurn)
            turn = 1;
        else
            turn = -1;
        if (move.isEnPassant()) {
            chessBoard[fromRow][toCol] = EMPTY;
        }
        if (move.isCastle()) {
            int rookFromCol;
            int rookToCol;
            if (toCol - fromCol > 0) {
                rookFromCol = 7;
                rookToCol = 5;
            } else {
                rookFromCol = 0;
                rookToCol = 3;
            }
            chessBoard[toRow][rookFromCol] = EMPTY;
            chessBoard[toRow][rookToCol] = ROOK * turn;

        }


        chessBoard[fromRow][fromCol] = EMPTY;
        if(move.isPromotion()) {
            int promotedPiece = move.getPromotedPiece()*turn;
            chessBoard[toRow][toCol] = promotedPiece;
        }
        else {
            chessBoard[toRow][toCol] = piece;
        }
        move.setSavedCastleRights(0,canWhiteSCastle);
        move.setSavedCastleRights(1,canBlackSCastle);
        move.setSavedCastleRights(2,canWhiteLCastle);
        move.setSavedCastleRights(3,canBlackLCastle);
        removeCastleRights(piece,fromRow,fromCol);
    }


    public void undoMove(Move move){
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        int piece = chessBoard[toRow][toCol];
        canWhiteSCastle = move.getSaveCastleRights(0);
        canBlackSCastle = move.getSaveCastleRights(1);
        canWhiteLCastle = move.getSaveCastleRights(2);
        canBlackLCastle = move.getSaveCastleRights(3);
        if(move.isEnPassant()) {
            int capturedPawn;
            if(isWhiteTurn)
                capturedPawn = BLACK_PAWN;
            else
                capturedPawn = WHITE_PAWN;
            chessBoard[fromRow][toCol] = capturedPawn;
        }
        chessBoard[toRow][toCol] = move.getCapturedPiece();
        if(move.isCastle()){
            int ownRook;
            if(isWhiteTurn)
                ownRook = WHITE_ROOK;
            else
                ownRook = BLACK_ROOK;
            int rookBeforeCastleCol;
            int rookAfterCastleCol;
            if(toCol-fromCol>0) {
                rookBeforeCastleCol = 7;
                rookAfterCastleCol = 5;
            }
            else {
                rookBeforeCastleCol = 0;
                rookAfterCastleCol = 3;
            }
            chessBoard[toRow][rookAfterCastleCol] = EMPTY;
            chessBoard[toRow][rookBeforeCastleCol] = ownRook;
        }
        if(move.isPromotion()) {
            int ownPawn;
            if(isWhiteTurn)
                ownPawn = WHITE_PAWN;
            else
                ownPawn = BLACK_PAWN;
            chessBoard[fromRow][fromCol] = ownPawn;
        }
        else
            chessBoard[fromRow][fromCol] = piece;
    }
    private void removeCastleRights(int piece, int fromRow, int fromCol) {
        if(piece == KING) {
            if(isWhiteTurn) {
                canWhiteLCastle = false;
                canWhiteSCastle = false;
            }else{
                canBlackLCastle = false;
                canBlackSCastle = false;
            }
        }else if(piece == ROOK)
            if(isWhiteTurn && fromRow == WHITE_BACK_ROW) {
                if (fromCol == 0 )
                    canWhiteLCastle = false;
                else if (fromCol == 7)
                    canWhiteSCastle = false;
            }else if(!isWhiteTurn && fromRow == BLACK_BACK_ROW) {
                if (fromCol == 0)
                    canBlackLCastle = false;
                else if (fromCol == 7)
                    canBlackSCastle = false;
            }


    }
    public void saveMove(Move move) {
        pastMoves[pastMovesSize++] = move;
    }
    public void unSaveMove(Move move) {
        undoMoves[undoMovesSize++] = move;
        pastMovesSize--;
    }

    public boolean isCheckCheckmate() {
        return !hasLegalMoves() && isInCheck();
    }

    public boolean isStaleMate() {
        return !hasLegalMoves() && !isInCheck();
    }

    public void setGameOver() {
        isGameOver = true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Move getLastMove() {
        return pastMoves[pastMovesSize-1];
    }
    public Move getLastUndoMove() {
        return undoMoves[--undoMovesSize];
    }
    public boolean hasPastMoves() {
        return pastMovesSize >0;
    }

    public boolean hasRedoMoves() {
        return undoMovesSize>0;
    }

    public void deleteUndoMoves() {
        undoMovesSize = 0;
    }

    public MoveIterator moveIterator() {
        return new MoveIterator(legalMoves, legalMovesSize);
    }
}
