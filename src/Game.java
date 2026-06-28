import java.util.Random;
public class Game {
    public static final String BlACK = "black";
    public static final int CHESS_BOARD_ROWS = 8;
    public static final int CHESS_BOARD_COLS = 8;
    private static final int WHITE_BACK_ROW = 7;
    private static final int WHITE_PAWN_ROW = 6;
    private static final int BLACK_BACK_ROW = 0;
    private static final int BLACK_PAWN_ROW = 1;
    private static final int MAX_MOVES = 256;
    private static final int MAX_SEARCHED_MOVES = 131072;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    public static final int EMPTY = 0;
    public static final int WHITE_PAWN = 1;
    public static final int WHITE_KNIGHT = 2;
    public static final int WHITE_BISHOP = 3;
    public static final int WHITE_ROOK = 4;
    public static final int WHITE_QUEEN = 5;
    public static final int WHITE_KING = 6;
    public static final int BLACK_PAWN = -1;
    public static final int BLACK_KNIGHT = -2;
    public static final int BLACK_BISHOP = -3;
    public static final int BLACK_ROOK = -4;
    public static final int BLACK_QUEEN = -5;
    public static final int BLACK_KING = -6;
    private static final int PAWN_DOUBLE_MOVE = 2;
    private static final int KING_CASTLE_MOVE = 2;
    private static final int[][] KNIGHT_MOVESET = {
            {1,2}, {-1,2}, {1,-2}, {-1,-2},
            {2,1}, {2,-1}, {-2,1}, {-2,-1}
    };
    private final int[][] KING_MOVESET = {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1},        { 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
    };
    private static final int[][]  BISHOP_DIRECTIONS = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private static final int[][] ROOK_DIRECTIONS = {{1,0},{-1,0},{0,1},{0,-1}};
    private static final int[][] QUEEN_DIRECTIONS = {{1,1},{1,-1},{-1,1},{-1,-1},
            {1,0},{-1,0},{0,1},{0,-1}};
    public boolean isAiWhite;
    public boolean isWhiteTurn;
    private boolean isGameOver;
    private boolean canWhiteSCastle;
    private boolean canBlackSCastle;
    private boolean canWhiteLCastle;
    private boolean canBlackLCastle;
    private int legalMovesSize;
    private int pastMovesSize;
    private int captureMovesSize;
    private final int[][] chessBoard;
    private final Move[] legalMoves;
    private final Move[] pastMoves;
    private final Move[] captureMoves;
    private int[] whiteKingPos;
    private int[] blackKingPos;

    private final long[][] zobristTable;
    private long blackToMove;
    private final long[] zobristValues;
    private int zobristValuesSize;
    private long currentZobristHash;
    public Game(String playerSide){
        chessBoard = new int[CHESS_BOARD_ROWS][CHESS_BOARD_COLS];
        legalMoves = new Move[MAX_MOVES];
        legalMovesSize = 0;
        pastMoves = new Move[MAX_SEARCHED_MOVES];
        pastMovesSize = 0;
        captureMoves = new Move[MAX_MOVES];
        captureMovesSize = 0;
        isWhiteTurn = true;
        isGameOver = false;
        canWhiteSCastle = true;
        canBlackSCastle = true;
        canWhiteLCastle = true;
        canBlackLCastle = true;
        isAiWhite = playerSide.equals(BlACK);
        whiteKingPos = new int[]{WHITE_BACK_ROW,4};
        blackKingPos = new int[]{BLACK_BACK_ROW,4};

        Random r= new Random();
        zobristTable = new long[64][12];
        for(int i = 0;i<64;i++)
            for(int j = 0;j<12;j++)
                zobristTable[i][j] = r.nextLong();
        blackToMove = r.nextLong();
        zobristValues = new long[131072];
        currentZobristHash = getZobristHashValue();
        zobristValues[0] = currentZobristHash;
        zobristValuesSize = 1;
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
        int kingRow;
        int kingCol;
        if(isWhiteTurn) {
            kingRow = whiteKingPos[0];
            kingCol = whiteKingPos[1];
        }
        else {
            kingRow = blackKingPos[0];
            kingCol = blackKingPos[1];
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


    public void generateLegalMoves() {
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
    public int  getLegalMovesSize(){
        return legalMovesSize;
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
            return Math.abs(lastMove.getMovingPiece()) == PAWN
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
        return Math.abs(movingPiece) == PAWN  && fromCol != toCol && capturedPiece == EMPTY;
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
                addMove(createMove(row,col,row+direction, col, chessBoard[row][col]));
            if (canDoubleAdvance(row, col, direction)) {
                addMove(createMove(row, col, row+direction*PAWN_DOUBLE_MOVE, col,chessBoard[row][col]));
            }
        }
    }
    private void checkPawnCaptures(int row,int col, int direction){
        if(isWithinBoard(row+direction,col+1) && isEnemyPiece(row+direction, col+1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col+1);
            }else
                addMove(createMove(row,col,row+direction,col+1,chessBoard[row][col]));
        if(isWithinBoard(row+direction,col-1) && isEnemyPiece(row+direction, col-1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col-1);
            }else
                addMove(createMove(row,col,row+direction,col-1,chessBoard[row][col]));
    }
    private void checkEnPassant(int row,int col, int direction){
        if(isWithinBoard(row+direction,col+1)
                && wasLastMoveDoubleAdvance(row, col+1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col+1);
            }else
                addMove(createMove(row,col,row+direction,col+1,chessBoard[row][col]));
        if(isWithinBoard(row+direction,col-1)
                && wasLastMoveDoubleAdvance(row, col-1))
            if(hasPromotionConditions(row+direction)) {
                addPromotionMoves(row,col,row+direction,col-1);
            }else
                addMove(createMove(row,col,row+direction,col-1,chessBoard[row][col]));
    }
    private void addPromotionMoves(int fromRow, int fromCol, int toRow, int toCol){
        int[] promotionPieces = {KNIGHT,BISHOP,ROOK,QUEEN};
        for (int promotionPiece : promotionPieces) {
            Move move = createMove(fromRow, fromCol, toRow, toCol, chessBoard[fromRow][fromCol]);
            move.setPromotedPiece(promotionPiece);
            addMove(move);
        }
    }


    private void checkKnightMoves(int fromRow, int fromCol) {
        for (int[] moves : KNIGHT_MOVESET) {
            int toRow = fromRow + moves[0];
            int toCol = fromCol + moves[1];
            if (isWithinBoard(toRow, toCol)
                    && (isEmpty(toRow, toCol) || isEnemyPiece(toRow, toCol)))
                addMove(createMove(fromRow, fromCol, toRow, toCol,chessBoard[fromRow][fromCol]));
        }
    }


    private void checkKingMoves(int fromRow, int fromCol) {
        for (int[] moves : KING_MOVESET) {
            int toRow = fromRow + moves[0];
            int toCol = fromCol + moves[1];
            if (isWithinBoard(toRow, toCol)
                    && (isEmpty(toRow, toCol) || isEnemyPiece(toRow, toCol)))
                addMove(createMove(fromRow, fromCol, toRow, toCol, chessBoard[fromRow][fromCol]));
        }
        if(hasShortCastleConditions(fromRow, fromCol)) {
            Move move = createMove(fromRow, fromCol, fromRow,fromCol+KING_CASTLE_MOVE, chessBoard[fromRow][fromCol]);
            move.setIsCastle(true);
            addMove(move);
        }
        if(hasLongCastleConditions(fromRow, fromCol)) {
            Move move = createMove(fromRow, fromCol, fromRow,fromCol-KING_CASTLE_MOVE, chessBoard[fromRow][fromCol]);
            move.setIsCastle(true);
            addMove(move);
        }
    }

    private boolean hasShortCastleConditions(int fromRow, int fromCol) {
        int kingRow;
        int rook;
        boolean hasShortCastleRights;
        if(isWhiteTurn) {
            rook = WHITE_ROOK;
            kingRow = WHITE_BACK_ROW;
            hasShortCastleRights = canWhiteSCastle;
        }
        else {
            rook = BLACK_ROOK;
            kingRow = BLACK_BACK_ROW;
            hasShortCastleRights = canBlackSCastle;
        }
        if(chessBoard[kingRow][7]!=rook)
            return false;
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
        int rook;
        boolean hasLongCastleRights;
        if(isWhiteTurn) {
            rook = WHITE_ROOK;
            kingRow = WHITE_BACK_ROW;
            hasLongCastleRights = canWhiteLCastle;
        }
        else {
            rook = BLACK_ROOK;
            kingRow = BLACK_BACK_ROW;
            hasLongCastleRights = canBlackLCastle;
        }
        if(chessBoard[kingRow][0]!=rook)
            return false;
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
        checkSlidingPiecesMoves(fromRow, fromCol,BISHOP_DIRECTIONS,chessBoard[fromRow][fromCol]);
    }

    private void checkRookMoves(int fromRow, int fromCol) {
        checkSlidingPiecesMoves(fromRow,fromCol, ROOK_DIRECTIONS,chessBoard[fromRow][fromCol]);
    }
    private void checkQueenMoves(int fromRow, int fromCol) {
        checkSlidingPiecesMoves(fromRow,fromCol, QUEEN_DIRECTIONS,chessBoard[fromRow][fromCol]);
    }


    private void checkSlidingPiecesMoves(int fromRow, int fromCol, int[][] directions, int piece){
        for (int[] direction : directions) {
            int j = 1;
            while (isWithinBoard(fromRow + j * direction[0], fromCol + j * direction[1])) {
                int toRow = fromRow + j * direction[0];
                int toCol = fromCol + j * direction[1];
                if (isEmpty(toRow, toCol))
                    addMove(createMove(fromRow, fromCol, toRow, toCol, piece));
                else if (isEnemyPiece(toRow, toCol)) {
                    addMove(createMove(fromRow, fromCol, toRow, toCol, piece));
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
        return (isWhiteTurn && toRow == BLACK_BACK_ROW) || (!isWhiteTurn && toRow == WHITE_BACK_ROW);
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
    public boolean isMoveLegal(int fromRow, int fromCol, int toRow, int toCol) {
        int movingPiece = Math.abs(chessBoard[fromRow][fromCol]);
        Move move = new Move(fromRow, fromCol, toRow, toCol,movingPiece);
        int i=0;
        while(i < legalMovesSize && (legalMoves[i].compareTo(move)!=0)){
            i++;
        }
        return i<legalMovesSize;
    }

    public void movePiece(Move move) {

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        int piece = move.getMovingPiece();
        int turn;
        if (isWhiteTurn)
            turn = 1;
        else
            turn = -1;
        if(Math.abs(piece) == KING) {
            if(isWhiteTurn)
                whiteKingPos = new int[]{toRow, toCol};
            else
                blackKingPos = new int[]{toRow, toCol};
        }
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
        int piece = move.getMovingPiece();
        canWhiteSCastle = move.getSaveCastleRights(0);
        canBlackSCastle = move.getSaveCastleRights(1);
        canWhiteLCastle = move.getSaveCastleRights(2);
        canBlackLCastle = move.getSaveCastleRights(3);
        if(Math.abs(move.getMovingPiece()) == KING) {
            if(isWhiteTurn)
                whiteKingPos = new int[]{fromRow, fromCol};
            else
                blackKingPos = new int[]{fromRow, fromCol};
        }
        if(move.isEnPassant()) {
            int capturedPawn = move.getMovingPiece() == WHITE_PAWN ? BLACK_PAWN : WHITE_PAWN;
            chessBoard[fromRow][toCol] = capturedPawn;
        }
        chessBoard[toRow][toCol] = move.getCapturedPiece();
        if(move.isCastle()){
            int ownRook = move.getMovingPiece() == WHITE_KING ? WHITE_ROOK : BLACK_ROOK;
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
            int ownPawn = move.getMovingPiece();
            chessBoard[fromRow][fromCol] = ownPawn;
        }
        else
            chessBoard[fromRow][fromCol] = piece;
    }
    private void removeCastleRights(int piece, int fromRow, int fromCol) {
        int absPiece = Math.abs(piece);
        if(absPiece == KING) {
            if(isWhiteTurn) {
                canWhiteLCastle = false;
                canWhiteSCastle = false;
            }else{
                canBlackLCastle = false;
                canBlackSCastle = false;
            }
        }else if(absPiece == ROOK)
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
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        int piece = move.getMovingPiece();
        pastMoves[pastMovesSize++] = move;
        currentZobristHash ^= zobristTable[fromRow*8+fromCol][pieceToIndex(piece)];

        if(move.getCapturedPiece() != EMPTY){
            currentZobristHash ^= zobristTable[toRow*8+toCol][pieceToIndex(move.getCapturedPiece())];
        }
        currentZobristHash ^= zobristTable[toRow*8+toCol][pieceToIndex(piece)];
        currentZobristHash^= blackToMove;
        zobristValues[zobristValuesSize++] = currentZobristHash;
    }
    public void unSaveMove() {
        pastMoves[pastMovesSize] = null;
        pastMovesSize--;
        currentZobristHash = zobristValues[zobristValuesSize-1];
        zobristValuesSize--;
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

    public boolean is50MoveDraw(){
        int totalMoves = 100;
        if(pastMovesSize >totalMoves){
            int i = pastMovesSize - 1;
            while (i>pastMovesSize-totalMoves && pastMoves[i].getMovingPiece() != PAWN &&
                    pastMoves[i].getCapturedPiece() == EMPTY){i--;}
            return i == pastMovesSize-totalMoves;
        }else
            return false;
    }

    public boolean isInsufficientMaterialDraw() {
        int pieceCount = 0;
        int whiteKnights = 0;
        int blackKnights = 0;
        int whiteBishops = 0;
        int blackBishops = 0;
        boolean hasOtherPieces = false;
        for (int row = 0; row < CHESS_BOARD_ROWS; row++) {
            for (int col = 0; col < CHESS_BOARD_COLS; col++) {
                if (chessBoard[row][col] != EMPTY &&
                        chessBoard[row][col] != WHITE_KING && chessBoard[row][col] != BLACK_KING) {
                    pieceCount++;
                    switch (chessBoard[row][col]) {
                        case WHITE_KNIGHT -> whiteKnights++;
                        case BLACK_KNIGHT -> blackKnights++;
                        case WHITE_BISHOP -> whiteBishops++;
                        case BLACK_BISHOP -> blackBishops++;
                        default -> hasOtherPieces = true;
                    }
                }
            }
        }
        return (pieceCount < 2 && !hasOtherPieces)
                || isOpposingKnightAndBishopDraw(whiteKnights, blackKnights,whiteBishops,blackBishops, pieceCount)
                || isTwoKnightsDraw(whiteKnights, blackKnights, pieceCount);
    }

    private boolean isTwoKnightsDraw(int whiteKnights, int blackKnights, int pieceCount) {
        if(pieceCount == 2)
            return whiteKnights == 2 || blackKnights == 2;
        return false;
    }

    private boolean isOpposingKnightAndBishopDraw(int whiteKnights,int blackKnights, int whiteBishops,
                                                  int blackBishops,int pieceCount) {
        if(pieceCount ==2)
            return (whiteKnights == 1 && blackBishops == 1) || (blackKnights == 1 && whiteBishops == 1);
        else
            return false;
    }
    public boolean isThreefoldRepetitionDraw(){
        int repetitionNum = 0;
        for(int i = 0;i<zobristValuesSize;i++){
            if(zobristValues[i] == currentZobristHash)
                repetitionNum++;
        }
        return repetitionNum >= 3;
    }

    private long getZobristHashValue() {
        long h = 0;
        if(!isWhiteTurn)
            h = h ^ blackToMove;
        for(int i = 0;i<CHESS_BOARD_COLS;i++){
            for(int j = 0;j<CHESS_BOARD_COLS;j++)
                if(chessBoard[i][j]!=EMPTY){
                    h = h ^ zobristTable[i*8+j][pieceToIndex(chessBoard[i][j])];
                }
        }
        return h;
    }
    private int pieceToIndex(int piece) {
        int k;
        if(piece>0)
            k = piece - 1;
        else
            k = Math.abs(piece) + 5;
        return k;
    }

    public int getPiece(int row,int col){
        return chessBoard[row][col];
    }
    public Move[] getLegalMoves(){
        Move[] legalMovesCopy = new Move[legalMovesSize];
        for(int i = 0;i<legalMovesSize;i++){
            legalMovesCopy[i] = legalMoves[i];
        }
        return legalMovesCopy;
    }

    public boolean isDraw() {
        return isStaleMate() || is50MoveDraw() || isInsufficientMaterialDraw()
                || isThreefoldRepetitionDraw();
    }

    public boolean isAiTurn() {
        return isAiWhite == isWhiteTurn;
    }

    public boolean isAiWhite() {
        return isAiWhite;
    }

    public void orderLegalMoves() {
        for(int i = 1;i<legalMovesSize;i++){
            Move key = legalMoves[i];
            int captureValue = getMoveValue(key);
            int j = i-1;
            while(j>=0 && getMoveValue(legalMoves[j])> captureValue ){
                legalMoves[j+1] =legalMoves[j];
                j = j-1;
            }
            legalMoves[j+1] = key;
        }
    }

    private int getMoveValue(Move move) {
        if(move.getCapturedPiece() == EMPTY)
            return 0;
        return Math.abs(move.getCapturedPiece()) - Math.abs(move.getMovingPiece());
    }

    public void generateCaptureMoves() {
        captureMovesSize = 0;
        for(int row = 0;row<CHESS_BOARD_ROWS;row++){
            for(int col = 0;col<CHESS_BOARD_COLS;col++){
                if(chessBoard[row][col] == EMPTY)
                    continue;

                if(isPiecesTurn(row, col))
                    checkPieceCaptures(row,col);
            }
        }

    }

    private void checkPieceCaptures(int row, int col) {
        int direction = isWhiteTurn ? -1:1;
        switch (Math.abs(chessBoard[row][col])){
            case (PAWN) -> addPawnCaptures(row,col,direction);
            case (KNIGHT) -> addKnightCaptures(row,col);
            case (BISHOP) -> addBishopCaptures(row,col);
            case (ROOK)-> addRookCaptures(row,col);
            case (QUEEN)-> addQueenCaptures(row,col);
            case (KING)-> addKingCaptures(row,col);
        }
    }

    private void addPawnCaptures(int row, int col, int direction) {
        if(isWithinBoard(row+direction,col+1) && isEnemyPiece(row+direction, col+1))
            if(hasPromotionConditions(row+direction)) {
                addCapturePromotionMoves(row,col,row+direction,col+1);
            }else
                addCaptureMove(createMove(row,col,row+direction,col+1,chessBoard[row][col]));
        if(isWithinBoard(row+direction,col-1) && isEnemyPiece(row+direction, col-1))
            if(hasPromotionConditions(row+direction)) {
                addCapturePromotionMoves(row,col,row+direction,col-1);
            }else
                addCaptureMove(createMove(row,col,row+direction,col-1,chessBoard[row][col]));
    }


    private void addKnightCaptures(int row, int col) {
        for (int[] moves : KNIGHT_MOVESET) {
            int toRow = row + moves[0];
            int toCol = col + moves[1];
            if (isWithinBoard(toRow, toCol)
                    && isEnemyPiece(toRow, toCol))
                addCaptureMove(createMove(row, col, toRow, toCol,chessBoard[row][col]));
        }
    }
    private void addBishopCaptures(int row, int col) {
        checkSlidingPiecesCaptures(row, col,BISHOP_DIRECTIONS,chessBoard[row][col]);
    }

    private void addRookCaptures(int row, int col) {
        checkSlidingPiecesCaptures(row,col, ROOK_DIRECTIONS,chessBoard[row][col]);
    }
    private void addQueenCaptures(int row, int col) {
        checkSlidingPiecesCaptures(row,col, QUEEN_DIRECTIONS,chessBoard[row][col]);
    }
    private void addKingCaptures(int row, int col) {
        for (int[] moves : KING_MOVESET) {
            int toRow = row + moves[0];
            int toCol = col + moves[1];
            if (isWithinBoard(toRow, toCol)
                    && (isEmpty(toRow, toCol) || isEnemyPiece(toRow, toCol)))
                addCaptureMove(createMove(row, col, toRow, toCol, chessBoard[row][col]));
        }
    }
    private void checkSlidingPiecesCaptures(int fromRow, int fromCol, int[][] directions, int piece) {
        for (int[] direction : directions) {
            int j = 1;
            while (isWithinBoard(fromRow + j * direction[0], fromCol + j * direction[1])) {
                int toRow = fromRow + j * direction[0];
                int toCol = fromCol + j * direction[1];
                if (isEmpty(toRow, toCol))
                    j++;
                else {
                    if (isEnemyPiece(toRow, toCol)) {
                        addCaptureMove(createMove(fromRow, fromCol, toRow, toCol, piece));
                    }
                    break;
                }

            }
        }
    }
    private void addCaptureMove(Move move) {
        movePiece(move);

        if (!isInCheck()) {
            captureMoves[captureMovesSize++] = move;
        }
        undoMove(move);
    }
    private void addCapturePromotionMoves(int fromRow, int fromCol, int toRow, int toCol) {
        int[] promotionPieces = {KNIGHT,BISHOP,ROOK,QUEEN};
        for (int promotionPiece : promotionPieces) {
            Move move = createMove(fromRow, fromCol, toRow, toCol, chessBoard[fromRow][fromCol]);
            move.setPromotedPiece(promotionPiece);
            addCaptureMove(move);
        }
    }
    public void orderCaptureMoves() {
        for(int i = 1;i<captureMovesSize;i++){
            Move key = captureMoves[i];
            int captureValue = getMoveValue(captureMoves[i]);
            int j = i-1;
            while(j>=0 && getMoveValue(captureMoves[j])> captureValue ){
                captureMoves[j+1] =captureMoves[j];
                j = j-1;
            }
            captureMoves[j+1] = key;
        }
    }

    public boolean hasCaptureMoves() {
        return captureMovesSize > 0;
    }

    public Move[] getCaptureMoves() {
        Move[] captureMovesCopy = new Move[captureMovesSize];
        for(int i = 0;i<captureMovesSize;i++){
            captureMovesCopy[i] = captureMoves[i];
        }
        return captureMovesCopy;
    }

    public int getCaptureMovesSize() {
        return captureMovesSize;
    }
    public MoveIterator moveIterator() {
        return new MoveIterator(legalMoves, legalMovesSize);
    }
}
