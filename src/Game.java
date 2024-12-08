public class Game {

    // o primeiro board é "default", os seguintes recebem parâmetros - board.promptText/board.promptInt para imprimir uma mensagem e receber dados para atribuir a uma variável "String"/int
    private static int boardSize; // tamanho do tabuleiro
    private static int pieceAmount; // número de peças por jogador
    public static final int EMPTY = 0; // valor para quando o espaço está vazio
    public static final int BLACK = 1; // valor para quando existe uma peça preta na posição
    public static final int WHITE = 2; // valor para quando existe uma peça branca na posição

    public int[][] board; // criação de matriz com as posições no tabuleiro de jogo

    public Game() { // construtor default
        this(8,12); // inicia o jogo com um tabuleiro 8x8 com 12 peças
    }

    public Game(int boardSize, int pieceAmount) { // construtor para new game
        Game.boardSize = boardSize; // define o tamanho do tabuleiro
        Game.pieceAmount = pieceAmount; // define o número de peças
        this.board = new int[boardSize][boardSize]; // cria o matriz tabuleiro
    }

    public Game(int boardSize, int pieceAmount, boolean turn, int[][] board) { // construtor para load game
        Game.boardSize = boardSize; // define o tamanho do tabuleiro
        Game.pieceAmount = pieceAmount; // define o número de peças
        this.board = board; // define o tabuleiro vindo do load
        this.blackTurn = turn; // define a vez
    }

    public static int getBoardSize(){
        return boardSize;
    }

    public static int getPieceAmount(){
        return pieceAmount;
    }

    public void getEmptyBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if(isBlackTile(i, j)) {
                    this.board[i][j] = EMPTY;
                }

            }
        }
    }

    public void initializeBoard() {
        // colocar as peças nas posições iniciais
        int blackPieces = pieceAmount;
        int whitePieces = pieceAmount;

        // colocar as peças pretas no tabuleiro
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if(isBlackTile(i,j) && blackPieces > 0) { // se for uma casa preta e ainda houver peças pretas disponíveis, coloca uma peça preta no lugar
                    this.board[i][j] = BLACK;
                    blackPieces--;
                }
            }
        }
        // colocar as peças brancas no tabuleiro
        for (int i = boardSize - 1; i >= 0; i--) {
            for (int j = boardSize - 1; j >= 0; j--) {
                if(isBlackTile(i,j) && whitePieces > 0) { // se for uma casa branca e ainda houver peças brancas disponíveis, coloca uma peça branca no lugar
                    this.board[i][j] = WHITE;
                    whitePieces--;
                }
            }
        }
    }

    // variável para definir quem joga
    private boolean blackTurn = false;

    public boolean isBlackTurn(){
        return this.blackTurn;
    }


    public void changeTurn () {
        this.blackTurn = !this.blackTurn;
    }

    public boolean isBlackTile(int line, int column) {
        return (line % 2 == 0 && column % 2 == 1) || (line % 2 == 1 && column % 2 == 0);
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
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
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
        if (!isValidPosition(startLine, startColumn) || !isValidPosition(endLine, endColumn)) { // verifica se o movimento e as posições são inválidas
            return false; // retorna falso caso sejam
        }
        if (hasCaptureMove()) { // se existir uma captura
            return isCaptureMove(startLine, startColumn, endLine, endColumn); // chama a função que obriga a captura da peça oponente
        }

        // validação se não houver movimento de captura disponível
        int piece = board[startLine][startColumn]; // acede à posição inicial, colocando o valor na variável
        if (piece == WHITE) { // peça branca
            return endLine == startLine - 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn); // posição final é 1 linha acima da inicial e 1 coluna à esquerda/direita, posição final está vazia
        } else if (piece == BLACK) { // peça preta
            return endLine == startLine + 1 && Math.abs(endColumn - startColumn) == 1 && isEmptyTile(endLine, endColumn); // posição final é 1 linha abaixo da inicial e 1 coluna à esquerda/direita, posição final está vazia
        }

        return false;
    }

    // Verificação para se existir movimento de captura
    private boolean isCaptureMove(int startLine, int startColumn, int endLine, int endColumn) {
        int piece = board[startLine][startColumn]; // acede à posição inicial, colocando o valor na variável
        if (piece == WHITE) { // peça branca
            if (endLine == startLine - 2 && Math.abs(endColumn - startColumn) == 2) {
                int opponentLine = (startLine + endLine) / 2; // linha da peça oponente
                int opponentColumn = (startColumn + endColumn) / 2; // coluna da peça oponente
                return hasBlackPiece(opponentLine, opponentColumn) && isEmptyTile(endLine, endColumn);
            }
        } else if (piece == BLACK) { // peça preta
            if (endLine == startLine + 2 && Math.abs(endColumn - startColumn) == 2) {
                int opponentLine = (startLine + endLine) / 2; // linha da peça oponente
                int opponentColumn = (startColumn + endColumn) / 2; // coluna da peça oponente
                return hasWhitePiece(opponentLine, opponentColumn) && isEmptyTile(endLine, endColumn);
            }
        }
        return false;
    }

    public void movePiece(int startLine, int startColumn, int endLine, int endColumn) {
        if (isValidPosition(startLine, startColumn) && isValidPosition(endLine, endColumn) && isValidMove(startLine, startColumn, endLine, endColumn)) {
            // movimento normal
            if (Math.abs(startLine - endLine) == 1 && Math.abs(startColumn - endColumn) == 1) {// diferença de 1 nas linhas e nas colunas - 1 casa na diagonal
                board[endLine][endColumn] = board[startLine][startColumn]; // move a peça jogada
                board[startLine][startColumn] = EMPTY; // substitui a posição inicial por EMPTY (vazio)
            }
            // movimento de captura de peça
            else if (Math.abs(startLine - endLine) == 2 && Math.abs(startColumn - endColumn) == 2) { // diferença de 2 nas linhas e nas colunas - 2 casas na diagonal
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
