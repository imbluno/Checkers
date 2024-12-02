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

    public int getBoardPosition(int line , int column){
        return 0;
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

    public boolean isBlackTurn(){
        return this.blackTurn;
    }

    public void changeTurn () {
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

    public boolean isValidPosition(int line, int column){
        return line >= 0 && column >= 0 && line < BOARD_SIZE && column < BOARD_SIZE; // verifica se a posição está dentro do tabuleiro
    }

    // peças não podem andar na vertical e na horizontal
    // se existir uma peça a capturar, tem que ser capturada obrigatoriamente
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

    public boolean isValidMove(int startLine, int startColumn, int endLine, int endColumn) {
        if (isValidPosition(startLine, startColumn) && isValidPosition(endLine, endColumn)) { // valida as posições
            int piece = board[startLine][startColumn];
            if (piece == WHITE) {  // brancas só andam na diagonal para cima
                if (endLine == startLine - 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn)) { // verifica que a posição final é 1 diagonal acima
                    return true;
                }
                // condição para as brancas capturarem
                else if (endLine == startLine - 2 && Math.abs(endColumn - startColumn) == 2) { // verifica que a posição final é 2 diagonais acima
                    int middleLine = (startLine + endLine) / 2; // divisão inteira das linhas para encontrar a linha no meio
                    int middleColumn = (startColumn + endColumn) / 2; // divisão inteira das colunas para encontrar a coluna no meio
                    if (hasBlackPiece(middleLine, middleColumn) && isEmptyTile(endLine, endColumn)) { // verifica que existe uma peça oponente na posição do meio e uma posição vazia na diagonal seguinte
                        return true;
                    }
                }
            } else if (piece == BLACK) {  // pretas só andam na diagonal para baixo
                if (endLine == startLine + 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn)) { // verifica que a posição final é 1 diagonal abaixo
                    return true;
                }
                // condição para as pretas capturarem
                else if (endLine == startLine + 2 && Math.abs(endColumn - startColumn) == 2) { // verifica que a posição final é 2 diagonais abaixo
                    int middleLine = (startLine + endLine) / 2; // divisão inteira das linhas para encontrar a linha no meio
                    int middleColumn = (startColumn + endColumn) / 2; // divisão inteira das colunas para encontrar a coluna no meio
                    if (hasWhitePiece(middleLine, middleColumn) && isEmptyTile(endLine, endColumn)) { // verifica que existe uma peça oponente na posição do meio e uma posição vazia na diagonal seguinte
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void movePiece(int startLine, int startColumn, int endLine, int endColumn) {
        if (isValidPosition(startLine, startColumn) && isValidPosition(endLine, endColumn) && isValidMove(startLine, startColumn, endLine, endColumn)) {
            // movimento normal
            if (Math.abs(startLine - endLine) == 1 && Math.abs(startColumn - endColumn) == 1) {
                board[endLine][endColumn] = board[startLine][startColumn]; // move a peça jogada
                board[startLine][startColumn] = EMPTY; // substitui a posição inicial por EMPTY (vazio)
            }
            // movimento de captura de peça
            else if (Math.abs(startLine - endLine) == 2 && Math.abs(startColumn - endColumn) == 2  && hasToCapture(startLine, startColumn)) {
                int middleLine = (startLine + endLine) / 2;
                int middleColumn = (startColumn + endColumn) / 2;
                // atualizar o tabuleiro, retirando a peça capturada
                board[middleLine][middleColumn] = EMPTY; // retira a peça capturada
                board[endLine][endColumn] = board[startLine][startColumn]; // move a peça jogada
                board[startLine][startColumn] = EMPTY;// substitui a posição inicial por EMPTY (vazio)
            }
        }
    }

}
