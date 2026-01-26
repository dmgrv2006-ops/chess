import java.util.Scanner;
public class Main{
    private static final String QUIT_COMMAND = "quit";
    private static final String SHOW_COMMAND = "show";
    private static final String MOVE_COMMAND = "move";
    private static final String UNDO_COMMAND = "undo";
    private static final String REDO_COMMAND = "redo";
    private static final String SHOW_MOVES_COMMAND = "showMoves";
    private static final String PIECE_NOT_THERE_MSG = "There is no piece there.";
    private static final String PIECE_NOT_YOURS_MSG = "That piece isn't yours.";
    private static final String MOVE_NOT_LEGAL_MSG = "That move isn't legal.";
    private static final String NO_UNDO_MOVE_MSG = "There no is move to undo.";
    private static final String NO_REDO_MOVE_MSG = "There no is move to redo.";
    private static final String UNDO_MSG = "Move undone";
    private static final String REDO_MSG = "Move redone";
    private static final String PIECE_MOVED_MSG = "Valid move";
    private static final String INVALID_COMMAND_MSG = "Invalid command";
    private static final String ITERATOR_MSG = "%c move %c%d %c%d\n";
    private static final String PROMOTION_COMMAND_MSG =
            "Which piece would you like the pawn to promote to:\nKnight\nBishop\nRook\nQueen\n";
    private static final String INVALID_PIECE_MSG = "Invalid promotion piece";
    private static final String COLS_LETTERS_MSG = "  a b c d e f g h";
    private static final String CHECKMATE_MSG = "Checkmate";
    private static final String STALEMATE_MSG = "Stalemate";
    private static final String KNIGHT_WORD = "knight";
    private static final String BISHOP_WORD = "bishop";
    private static final String ROOK_WORD = "rook";
    private static final String QUEEN_WORD = "queen";
    private static final int EMPTY_NUM = 0;
    private static final int WHITE_PAWN_NUM = 1;
    private static final int WHITE_KNIGHT_NUM = 2;
    private static final int WHITE_BISHOP_NUM = 3;
    private static final int WHITE_ROOK_NUM = 4;
    private static final int WHITE_QUEEN_NUM = 5;
    private static final int WHITE_KING_NUM = 6;
    private static final int BLACK_PAWN_NUM = -1;
    private static final int BLACK_KNIGHT_NUM = -2;
    private static final int BLACK_BISHOP_NUM = -3;
    private static final int BLACK_ROOK_NUM = -4;
    private static final int BLACK_QUEEN_NUM = -5;
    private static final int BLACK_KING_NUM = -6;
    private static final int CHESS_BOARD_ROWS = 8;
    private static final int CHESS_BOARD_COLS = 8;
    private static final char EMPTY = '.';
    private static final char WHITE_PAWN = 'P';
    private static final char WHITE_KNIGHT = 'N';
    private static final char WHITE_BISHOP = 'B';
    private static final char WHITE_ROOK = 'R';
    private static final char WHITE_QUEEN = 'Q';
    private static final char WHITE_KING = 'K';
    private static final char BLACK_PAWN = 'p';
    private static final char BLACK_KNIGHT = 'n';
    private static final char BLACK_BISHOP = 'b';
    private static final char BLACK_ROOK = 'r';
    private static final char BLACK_QUEEN = 'q';
    private static final char BLACK_KING = 'k';
    private static final char MISSING_PIECE = '?';

    private static void printChessBoard(Game chess){
        int[][] chessBoard = chess.getChessBoard();
        for(int row = 0; row<CHESS_BOARD_ROWS;row++){
            System.out.print(CHESS_BOARD_ROWS-row + " ");
            for(int col = 0;col<CHESS_BOARD_COLS;col++){
                System.out.printf("%c ", getPieceCharacter(chessBoard[row][col]));
            }
            System.out.println();
        }
        System.out.println(COLS_LETTERS_MSG);
    }
    private static char getPieceCharacter(int chessPieceNum) {
        return switch (chessPieceNum) {
            case (EMPTY_NUM) -> EMPTY;
            case (WHITE_PAWN_NUM) -> WHITE_PAWN;
            case (WHITE_KNIGHT_NUM) -> WHITE_KNIGHT;
            case (WHITE_BISHOP_NUM) -> WHITE_BISHOP;
            case (WHITE_ROOK_NUM) -> WHITE_ROOK;
            case (WHITE_QUEEN_NUM) -> WHITE_QUEEN;
            case (WHITE_KING_NUM) -> WHITE_KING;
            case (BLACK_PAWN_NUM) -> BLACK_PAWN;
            case (BLACK_KNIGHT_NUM) -> BLACK_KNIGHT;
            case (BLACK_BISHOP_NUM) -> BLACK_BISHOP;
            case (BLACK_ROOK_NUM) -> BLACK_ROOK;
            case (BLACK_QUEEN_NUM) -> BLACK_QUEEN;
            case (BLACK_KING_NUM) -> BLACK_KING;
            default -> MISSING_PIECE;
        };
    }
    private static void movePiece(Scanner in, Game chess){
        char[] fromSquare = in.next().toCharArray();
        char[] toSquare = in.next().toCharArray();

        int fromCol = fromSquare[0] - 'a';
        int fromRow = 7-(fromSquare[1] - '1');

        int toCol = toSquare[0] - 'a';
        int toRow =7 -(toSquare[1] - '1');
        if(!chess.isPieceThere(fromRow,fromCol))
            System.out.println(PIECE_NOT_THERE_MSG);
        else if(!chess.isPiecesTurn(fromRow, fromCol))
            System.out.println(PIECE_NOT_YOURS_MSG);
        else {
            int index = 0;
            Move move = chess.getLegalMoves(fromRow, fromCol, toRow, toCol, index);
            if (move == null)
                System.out.println(MOVE_NOT_LEGAL_MSG);
            else {
                    if(move.isPromotion()) {
                        index = promotePiece(in);
                        move = chess.getLegalMoves(fromRow, fromCol, toRow, toCol, index);
                    }
                chess.deleteUndoMoves();
                chess.saveMove(move);
                chess.movePiece(move);
                System.out.println(PIECE_MOVED_MSG);
                printChessBoard(chess);
                chess.changeTurn();
                chess.checkLegalMoves();
                if(chess.isCheckCheckmate()) {
                    System.out.println(CHECKMATE_MSG);
                    chess.setGameOver();
                }
                else if(chess.isStaleMate()) {
                    System.out.println(STALEMATE_MSG);
                    chess.setGameOver();
                }
            }
        }
    }
    private static int promotePiece(Scanner in){
        System.out.print(PROMOTION_COMMAND_MSG);
        int promotedPiece = -1;
        int knight = 0;
        int bishop = 1;
        int rook = 2;
        int queen = 3;
        do {
            String promotedToPiece = in.next().toLowerCase().trim();
            in.nextLine();
            switch (promotedToPiece) {
                case (QUEEN_WORD) -> promotedPiece = queen;
                case (ROOK_WORD) -> promotedPiece = rook;
                case (KNIGHT_WORD) -> promotedPiece = knight;
                case (BISHOP_WORD) -> promotedPiece = bishop;
                default -> System.out.println(INVALID_PIECE_MSG);
            }
        }while(promotedPiece==-1);
        return promotedPiece;
    }
    private static void undoMove(Game chess){
        if(chess.hasPastMoves()) {
            Move lastMove = chess.getLastMove();
            chess.unSaveMove(lastMove);
            chess.undoMove(lastMove);
            System.out.println(UNDO_MSG);
            printChessBoard(chess);
            chess.changeTurn();
            chess.checkLegalMoves();
        }else
            System.out.println(NO_UNDO_MOVE_MSG);
    }
    private static void redoMove(Game chess){
        if(chess.hasRedoMoves()) {
            Move lastUndoMove = chess.getLastUndoMove();
            chess.saveMove(lastUndoMove);
            chess.movePiece(lastUndoMove);
            System.out.println(REDO_MSG);
            printChessBoard(chess);
            chess.changeTurn();
            chess.checkLegalMoves();
        }else
            System.out.println(NO_REDO_MOVE_MSG);
    }
    private static void showAllLegalMoves(Game chess){
        if(chess.hasLegalMoves()) {
            MoveIterator it = chess.moveIterator();
            while (it.hasNext()) {
                Move move = it.next();
                System.out.printf(ITERATOR_MSG,getPieceCharacter(move.getMovingPiece()),
                        'a'+move.getFromCol(),8-move.getFromRow(),
                        'a'+move.getToCol(),8-move.getToRow());
            }
        }
    }
    private static void commandInterpreter(String command, Game chess, Scanner in){
        switch(command){
            case(SHOW_COMMAND)-> printChessBoard(chess);
            case(MOVE_COMMAND) -> movePiece(in, chess);
            case(UNDO_COMMAND) -> undoMove(chess);
            case(REDO_COMMAND) -> redoMove(chess);
            case(SHOW_MOVES_COMMAND) -> showAllLegalMoves(chess);
            default -> invalidCommand(in);
        }

    }
    private static void invalidCommand(Scanner in){
        in.nextLine();
        System.out.println(INVALID_COMMAND_MSG);
    }
    public static void main(String[] args){
        Game chess = new Game();
        chess.createBoard();
        chess.checkLegalMoves();
        Scanner in = new Scanner(System.in);
        String command;
        do{
            command = in.next();
            commandInterpreter(command, chess, in);
        }while (!command.equals(QUIT_COMMAND) && !chess.isGameOver());
    }
}
