public class Game {

    // o primeiro board é default, os seguintes recebem parâmetros - board.promptText/board.promptInt para imprimir uma mensagem e receber dados para atribuir a uma variavel String/int
    // tamanho do tabuleiro
    private static final int BOARD_SIZE = 8;
    // valor para quando o espaço está vazio
    public static final int EMPTY = 0;
    // valor para quando existe uma peça preta na posição
    public static final int BLACK = 1;
    // valor para quando existe uma peça branca na posição
    public static final int WHITE = 2;
    // número de peças por jogador
    private static final int PIECE_AMOUNT = 12;
    // criação de matriz com as posições no tabuleiro de jogo
    final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    public int getBoardSize(){
        return BOARD_SIZE;
    }

    public int getBoardPosition(Position position) {
        return board[position.line()][position.column()];
    }

    public void getEmptyBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(new Position(i, j))) {
                    this.board[i][j] = EMPTY;
                }

            }
        }
    }

    public void initializeBoard() {
        // colocar as peças nas posições iniciais
        int blackPieces = PIECE_AMOUNT;
        int whitePieces = PIECE_AMOUNT;

        // colocar as peças pretas no tabuleiro
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(new Position(i, j)) && blackPieces > 0) { // se for uma casa preta e ainda houver peças pretas disponíveis, coloca uma peça preta no lugar
                    this.board[i][j] = BLACK;
                    blackPieces--;
                }
            }
        }
        // colocar as peças brancas no tabuleiro
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            for (int j = BOARD_SIZE - 1 ; j >= 0; j--) {
                if(isBlackTile(new Position(i, j)) && whitePieces > 0) { // se for uma casa branca e ainda houver peças brancas disponíveis, coloca uma peça branca no lugar
                    this.board[i][j] = WHITE;
                    whitePieces--;
                }
            }
        }
    }

    // variável para definir quem joga
    private boolean blackTurn = true;

    public boolean isBlackTurn(){
        return this.blackTurn;
    }

    public void changeTurn () {
        this.blackTurn = !this.blackTurn;
    }

    public boolean isBlackTile(Position position) {
        return (position.line() % 2 == 0 && position.column() % 2 == 1)
                ||
                (position.line() % 2 == 1 && position.column() % 2 == 0);
    }
    public boolean isEmptyTile(Position position) {
        return board[position.line()][position.column()] == EMPTY;
    }

    public boolean hasWhitePiece(Position position) {
        return board[position.line()][position.column()] == WHITE;
    }

    public boolean hasBlackPiece(Position position) {
        return board[position.line()][position.column()] == BLACK;
    }

    public boolean isValidPosition(Position position) {
        int line = position.line();
        int column = position.column();
        return line >= 0 && column >= 0 && line < BOARD_SIZE && column < BOARD_SIZE;
    }

    public boolean isValidMove(Position start, Position end) {
        if (isValidPosition(start) && isValidPosition(end)) {
            int piece = board[start.line()][start.column()];
            int deltaLine = end.line() - start.line();
            int deltaColumn = Math.abs(end.column() - start.column());

            if (piece == WHITE) {
                if (deltaLine == -1 && deltaColumn == 1 && isEmptyTile(end)) {
                    return true;
                } else if (deltaLine == -2 && deltaColumn == 2) {
                    Position middle = new Position((start.line() + end.line()) / 2, (start.column() + end.column()) / 2);
                    if (hasBlackPiece(middle) && isEmptyTile(end)) {
                        return true;
                    }
                }
            } else if (piece == BLACK) {
                if (deltaLine == 1 && deltaColumn == 1 && isEmptyTile(end)) {
                    return true;
                } else if (deltaLine == 2 && deltaColumn == 2) {
                    Position middle = new Position((start.line() + end.line()) / 2, (start.column() + end.column()) / 2);
                    if (hasWhitePiece(middle) && isEmptyTile(end)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void movePiece(Position start, Position end) {
        if (isValidMove(start, end)) {
            int deltaLine = Math.abs(start.line() - end.line());
            if (deltaLine == 1) {
                board[end.line()][end.column()] = board[start.line()][start.column()];
                board[start.line()][start.column()] = EMPTY;
            } else if (deltaLine == 2) {
                Position middle = new Position((start.line() + end.line()) / 2, (start.column() + end.column()) / 2);
                board[middle.line()][middle.column()] = EMPTY;
                board[end.line()][end.column()] = board[start.line()][start.column()];
                board[start.line()][start.column()] = EMPTY;
            }
        }
    }

}
