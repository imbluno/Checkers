public class Game {
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

    public void getEmptyBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(i, j)) {
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
                if(isBlackTile(i,j) && blackPieces > 0) { // se for uma casa preta e ainda houver peças pretas disponíveis, coloca uma peça preta no lugar
                    this.board[i][j] = BLACK;
                    blackPieces--;
                }
            }
        }
        // colocar as peças brancas no tabuleiro
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            for (int j = BOARD_SIZE - 1 ; j >= 0; j--) {
                if(isBlackTile(i,j) && whitePieces > 0) { // se for uma casa branca e ainda houver peças brancas disponíveis, coloca uma peça branca no lugar
                    this.board[i][j] = WHITE;
                    whitePieces--;
                }
            }
        }
    }

    // variável para definir quem joga
    private boolean blackTurn = true;

    private void changeTurn () {
        this.blackTurn = !this.blackTurn;
    }

    public boolean isBlackTile(int line, int column) {
        return (
            line % 2 == 0 // linhas pares
                &&
            column % 2 == 1 // colunas ímpares
            )
                ||
            (
            line % 2 == 1 // linhas ímpares
                &&
            column % 2 == 0 // colunas pares
            );
    }
    public boolean isEmptyTile(int line, int column) {
        return board[line][column] == EMPTY;
    }

    public boolean hasWhitePiece(int line, int column){
        return board[line][column] == WHITE;
    }

    public boolean hasBlackPiece(int line, int column){
        return board[line][column] == BLACK;
    }

    private boolean hasToCapture(int line, int column){
        if(
            board[line][column] == BLACK // peça inicial é preta
                &&
                (
                        board[line - 1][column + 1] == WHITE // existe uma peça branca na diagonal à direita
                            ||
                        board[line - 1][column - 1] == WHITE // existe uma peça branca na diagonal à esquerda
                )
        )
        {
            return true;
        } else if(
                board[line][column] == WHITE // peça inicial é branca
                &&
                (
                        board[line + 1][column + 1] == BLACK // existe uma peça preta na diagonal à direita
                            ||
                        board[line + 1][column - 1] == BLACK // existe uma peça preta na diagonal à esquerda
                )
        )
        {
            return true;
        }
        return false;
    }
    public boolean isValidPosition(int line, int column){
        return line >= 0 && column >= 0 && line < BOARD_SIZE && column < BOARD_SIZE; // verifica se a posição está dentro do tabuleiro
    }
    // brancas só andam na diagonal para cima
    // pretas só andam na diagonal para baixo
    // peças não podem andar na vertical e na horizontal
    // se existir uma peça a capturar, tem que ser capturada obrigatoriamente
    public boolean isValidMove(int startLine, int startColumn, int endLine, int endColumn) {

        if (isValidPosition(startLine, startColumn) && isValidPosition(endLine, endColumn)) {
            if (board[startLine][startColumn] == WHITE) { // verifica se a peça é branca;
                if(startLine - endLine == 1 && Math.abs(startColumn - endColumn) == 1) {
                    return true;
                }

            } else if (board[startLine][startColumn] == BLACK) { // verifica se a peça é preta
                if(startLine + 1 < BOARD_SIZE) {
                    return true;
                }
            }
        }
        return false;
    }

    public void movePiece(int startLine, int startColumn, int endLine, int endColumn) {

    }







}
