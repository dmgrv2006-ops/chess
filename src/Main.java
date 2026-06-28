import java.util.Scanner;
public class Main {
    private static final String QUIT_COMMAND = "quit";
    private static final String SHOW_COMMAND = "show";
    private static final String MOVE_COMMAND = "move";
    private static final String SHOW_MOVES_COMMAND = "showMoves";
    private static final String PIECE_NOT_THERE_MSG = "There is no piece there.";
    private static final String PIECE_NOT_YOURS_MSG = "That piece isn't yours.";
    private static final String MOVE_NOT_LEGAL_MSG = "That move isn't legal.";
    private static final String PIECE_MOVED_MSG = "Valid move";
    private static final String INVALID_COMMAND_MSG = "Invalid command";
    private static final String ITERATOR_MSG = "%c move %c%d %c%d\n";
    private static final String PROMOTION_COMMAND_MSG =
            "Which piece would you like the pawn to promote to:\nKnight\nBishop\nRook\nQueen\n";
    private static final String INVALID_PIECE_MSG = "Invalid promotion piece";
    private static final String COLS_LETTERS_MSG = "  a b c d e f g h";
    private static final String CHECKMATE_MSG = "Checkmate";
    private static final String STALEMATE_MSG = "Stalemate";
    private static final String FIFTY_DRAW_MSG = "50-move draw";
    private static final String MATERIAL_DRAW_MSG = "Insufficient material draw";
    private static final String THREEFOLD_DRAW_MSG = "threefold repetition draw";
    private static final String CHOSE_SIDE_MSG = "White or black?";
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
    private static void movePiece(Scanner in, Game chess, ChessAI AI){
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
            if(!chess.isMoveLegal(fromRow, fromCol, toRow, toCol))
                System.out.println(MOVE_NOT_LEGAL_MSG);
            else {
                int piece = chess.getPiece(fromRow,fromCol);
                Move move = new Move(fromRow, fromCol, toRow, toCol,piece);
                if(Math.abs(piece) == Game.KING && Math.abs(fromCol - toCol)>1 )
                    move.setIsCastle(true);
                if(Math.abs(piece) == Game.PAWN && (toRow == 0  || toRow == 7 )) {
                    int promotedPiece = promotePiece(in);
                    move.setPromotedPiece(promotedPiece);
                }
                if(Math.abs(piece)== Game.PAWN && fromRow != toRow
                        && chess.getPiece(toRow,toCol) == Game.EMPTY)
                    move.setEnPassant(true);

                chess.saveMove(move);
                chess.movePiece(move);
                chess.changeTurn();
                chess.generateLegalMoves();
                System.out.println(PIECE_MOVED_MSG);
                checkForGameEndings(chess);
                if(!chess.isGameOver()) {
                    Move aiMove = AI.getBestPlay();
                    chess.movePiece(aiMove);
                    chess.saveMove(aiMove);
                    chess.changeTurn();
                    chess.generateLegalMoves();
                    checkForGameEndings(chess);
                }
                printChessBoard(chess);
            }
        }

    }

    private static void checkForGameEndings(Game chess) {
        if(chess.isCheckCheckmate()) {
            System.out.println(CHECKMATE_MSG);
            chess.setGameOver();
        }
        else if(chess.isStaleMate()) {
            System.out.println(STALEMATE_MSG);
            chess.setGameOver();
        }
        else if(chess.is50MoveDraw()){
            System.out.println(FIFTY_DRAW_MSG);
            chess.setGameOver();
        }else if(chess.isInsufficientMaterialDraw()) {
            System.out.println(MATERIAL_DRAW_MSG);
            chess.setGameOver();
        }else if(chess.isThreefoldRepetitionDraw()){
            System.out.println(THREEFOLD_DRAW_MSG);
            chess.setGameOver();
        }
    }

    private static int promotePiece(Scanner in){
        System.out.print(PROMOTION_COMMAND_MSG);
        int promotedPiece = -1;
        do {
            String promotedToPiece = in.next().toLowerCase().trim();
            in.nextLine();
            switch (promotedToPiece) {
                case (QUEEN_WORD) -> promotedPiece = WHITE_QUEEN_NUM;
                case (ROOK_WORD) -> promotedPiece = WHITE_ROOK_NUM;
                case (KNIGHT_WORD) -> promotedPiece = WHITE_KNIGHT_NUM;
                case (BISHOP_WORD) -> promotedPiece = WHITE_BISHOP_NUM;
                default -> System.out.println(INVALID_PIECE_MSG);
            }
        }while(promotedPiece==-1);
        return promotedPiece;
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

    private static void commandInterpreter(String command, Game chess, Scanner in, ChessAI AI){
        switch(command){
            case(SHOW_COMMAND)-> printChessBoard(chess);
            case(MOVE_COMMAND) -> movePiece(in, chess,AI);
            case(SHOW_MOVES_COMMAND) -> showAllLegalMoves(chess);
            default -> invalidCommand(in);
        }

    }
    private static void invalidCommand(Scanner in){
        in.nextLine();
        System.out.println(INVALID_COMMAND_MSG);
    }
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println(CHOSE_SIDE_MSG);
        String side = in.next().toLowerCase();
        in.nextLine();
        Game chess = new Game(side);
        ChessAI AI = new ChessAI(chess);
        chess.createBoard();
        chess.generateLegalMoves();
        if(chess.isAiWhite && !chess.isGameOver()) {
            Move aiMove = AI.getBestPlay();
            chess.movePiece(aiMove);
            chess.saveMove(aiMove);
            chess.changeTurn();
            chess.generateLegalMoves();
            checkForGameEndings(chess);
        }
        printChessBoard(chess);
        String command;
        do{
            command = in.next();
            commandInterpreter(command, chess, in,AI);
        }while (!command.equals(QUIT_COMMAND) && !chess.isGameOver());
        in.close();
    }
}
