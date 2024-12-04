public class Game {

    // o primeiro board é "default", os seguintes recebem parâmetros - board.promptText/board.promptInt para imprimir uma mensagem e receber dados para atribuir a uma variável "String"/int
    private static final int BOARD_SIZE = 8; // tamanho do tabuleiro
    public static final int EMPTY = 0; // valor para quando o espaço está vazio
    public static final int BLACK = 1; // valor para quando existe uma peça preta na posição
    public static final int WHITE = 2; // valor para quando existe uma peça branca na posição
    private static final int PIECE_AMOUNT = 12; // número de peças por jogador
    final int[][] board = new int[BOARD_SIZE][BOARD_SIZE]; // criação de matriz com as posições no tabuleiro de jogo

    public static int getBoardSize(){
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

    public boolean isBlackTurn(){
        return this.blackTurn;
    }

    public void changeTurn () {
        this.blackTurn = !this.blackTurn;
    }

    public boolean isBlackTile(int line, int column) {
        return (line % 2 == 0 // linhas pares
                && column % 2 == 1 // colunas ímpares
            )
                ||
            (line % 2 == 1 // linhas ímpares
                && column % 2 == 0 // colunas pares
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

    public static boolean isValidPosition(int line, int column){
        return line >= 0 && column >= 0 && line < getBoardSize() && column < getBoardSize(); // verifica se a posição está dentro do tabuleiro
    }
    public boolean hasCaptureMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if ((isBlackTurn() && hasBlackPiece(i, j)) || (!isBlackTurn() && hasWhitePiece(i, j))) {
                    if (canCapture(i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canCapture(int line, int column) {
        int piece = board[line][column];

        if (piece == WHITE) {
            // brancas capturam para cima, logo subtrai-se 2 linhas
            return (isValidPosition(line - 2, column + 2) && // valida se a posição final é válida
                    hasBlackPiece(line - 1, column + 1) // valida que existe uma peça preta a capturar no meio das posições
                    && isEmptyTile(line - 2, column + 2)) // valida que a posição final está vazia
                    ||
                    (isValidPosition(line - 2, column - 2) && // valida se a posição final é válida
                    hasBlackPiece(line - 1, column - 1) // valida que existe uma peça preta a capturar no meio das posições
                    && isEmptyTile(line - 2, column - 2)); // valida que a posição final está vazia
        } else if (piece == BLACK) {
            // pretas capturam para baixo, logo adiciona-se 2 linhas
            return (isValidPosition(line + 2, column + 2) && // valida se a posição final é válida
                    hasWhitePiece(line + 1, column + 1) // valida que existe uma peça branca a capturar no meio das posições
                    && isEmptyTile(line + 2, column + 2)) // valida que a posição final está vazia
                    ||
                    (isValidPosition(line + 2, column - 2) && // valida se a posição final é válida
                    hasWhitePiece(line + 1, column - 1) // valida que existe uma peça branca a capturar no meio das posições
                    && isEmptyTile(line + 2, column - 2)); // valida que a posição final está vazia
        }

        return false;
    }

    public boolean isValidMove(int startLine, int startColumn, int endLine, int endColumn) {
        if (!isValidPosition(startLine, startColumn) || !isValidPosition(endLine, endColumn)) { // Verifica se o movimento e as posições são válidas
            return false;
        }
        if (hasCaptureMove()) {
            return isCaptureMove(startLine, startColumn, endLine, endColumn); // Obriga a captura da peça oponente
        }

        // Validação se não houver movimento de captura disponível
        int piece = board[startLine][startColumn];
        if (piece == WHITE) {
            return endLine == startLine - 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn);
        } else if (piece == BLACK) {
            return endLine == startLine + 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn);
        }

        return false;
    }

    // Verificação para se existir movimento de captura
    private boolean isCaptureMove(int startLine, int startColumn, int endLine, int endColumn) {
        int piece = board[startLine][startColumn];
        if (piece == WHITE) {
            if (endLine == startLine - 2 && Math.abs(endColumn - startColumn) == 2) {
                int middleLine = (startLine + endLine) / 2; // linha da peça oponente
                int middleColumn = (startColumn + endColumn) / 2; // coluna da peça oponente
                return hasBlackPiece(middleLine, middleColumn) && isEmptyTile(endLine, endColumn);
            }
        } else if (piece == BLACK) {
            if (endLine == startLine + 2 && Math.abs(endColumn - startColumn) == 2) {
                int middleLine = (startLine + endLine) / 2; // linha da peça oponente
                int middleColumn = (startColumn + endColumn) / 2; // coluna da peça oponente
                return hasWhitePiece(middleLine, middleColumn) && isEmptyTile(endLine, endColumn);
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
            else if (Math.abs(startLine - endLine) == 2 && Math.abs(startColumn - endColumn) == 2) {
                int middleLine = (startLine + endLine) / 2;
                int middleColumn = (startColumn + endColumn) / 2;
                // atualizar o tabuleiro, retirando a peça capturada
                board[middleLine][middleColumn] = EMPTY; // retira a peça capturada
                board[endLine][endColumn] = board[startLine][startColumn]; // move a peça jogada
                board[startLine][startColumn] = EMPTY;// substitui a posição inicial por EMPTY (vazio)

            }
        }
        changeTurn();
    }

}
