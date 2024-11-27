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
    // criação de matriz de posições, em que cada posição vai ter as coordenadas da própria posição e o status da mesma (EMPTY, BLACK, WHITE) ficando do tipo Position[line][col] = new Position (line, col, status)
    private final Position[][] board = new Position[BOARD_SIZE][BOARD_SIZE];

    private void getEmptyBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = new Position(i, j, EMPTY);
            }
        }
    }

    public int getBoardSize(){
        return BOARD_SIZE;
    }

    public Position getBoardPosition(int line, int column){
        return board[line][column];
    }

    public Coordinates[] getDiagonals(Position pos){
        if (pos.status() == WHITE && pos.line() - 1 >= 0 && pos.column() - 1 < 0){ // se for uma peça branca, existir espaço no tabuleiro em cima e não existir espaço à esquerda
            return new Coordinates[]{new Coordinates(pos.line() - 1, pos.column() + 1)};
        } else if (pos.status() == WHITE && pos.line() - 1 >= 0 && pos.column() + 1 > BOARD_SIZE){ // se for uma peça branca, existir espaço no tabuleiro em cima e não existir espaço à direita
            return new Coordinates[]{new Coordinates(pos.line() - 1, pos.column() - 1)};
        } else if (pos.status() == WHITE && pos.line() - 1 >= 0){ // se for uma peça branca, existir espaço no tabuleiro em cima e existir espaço de ambos os lados
            return new Coordinates[]{new Coordinates(pos.line() - 1, pos.column() - 1), new Coordinates(pos.line() - 1, pos.column() + 1)};
        } else if (pos.status() == BLACK && pos.line() + 1 < BOARD_SIZE && pos.column() - 1 < 0){ // se for uma peça preta, existir espaço no tabuleiro abaixo e não existir espaço à esquerda
            return new Coordinates[]{new Coordinates(pos.line() + 1, pos.column() + 1)};
        } else if (pos.status() == BLACK && pos.line() + 1 < BOARD_SIZE && pos.column() + 1 > BOARD_SIZE){ // se for uma peça preta, existir espaço no tabuleiro abaixo e não existir espaço à direita
            return new Coordinates[]{new Coordinates(pos.line() + 1, pos.column() - 1)};
        } else if (pos.status() == BLACK && pos.line() + 1 < BOARD_SIZE){ // se for uma peça preta, existir espaço no tabuleiro abaixo e existir espaço de ambos os lados
            return new Coordinates[]{new Coordinates(pos.line() + 1, pos.column() - 1), new Coordinates(pos.line() + 1, pos.column() + 1)};
        } else
            return null;

    }

    private void initializeBoard() {
        // colocar as peças nas posições iniciais
        int blackPieces = PIECE_AMOUNT;
        int whitePieces = PIECE_AMOUNT;

        // colocar as peças pretas no tabuleiro
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(this.board[i][j]) && blackPieces > 0) { // se for uma casa preta e ainda houver peças pretas disponíveis, coloca uma peça preta no lugar
                    this.board[i][j] = new Position(i, j, BLACK);
                    blackPieces--;
                }
            }
        }
        // colocar as peças brancas no tabuleiro
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(this.board[i][j]) && whitePieces > 0) { // se for uma casa branca e ainda houver peças brancas disponíveis, coloca uma peça branca no lugar
                    this.board[i][j] = new Position(i, j, WHITE);
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

    public boolean isBlackTile(Position position) {
        return (
            position.line() % 2 == 0 // linhas pares
                &&
            position.column() % 2 == 1 // colunas ímpares
            ) ||
            (
            position.line() % 2 == 1 // linhas ímpares
                &&
            position.column() % 2 == 0 // colunas pares
            );
    }
    public boolean isEmptyTile(Position position){
        return position.status() == EMPTY;
    }

    public boolean hasWhitePiece(Position position){
        return position.status() == WHITE;
    }

    public boolean hasBlackPiece(Position position){
        return position.status() == BLACK;
    }

    private boolean hasToCapture(Position start, Position end){
        Position whiteLeftDiag = new Position(start.line() - 1, start.column()- 1, WHITE);
        Position whiteRightDiag = new Position(start.line() - 1, start.column() + 1, WHITE);
        Position blackLeftDiag = new Position(start.line() + 1, start.column() - 1, BLACK);
        Position blackRightDiag = new Position(start.line() + 1, start.column() + 1, BLACK);

        if(
            start.status() == BLACK // peça inicial é preta
                &&
                (
                        end.status() == whiteRightDiag.status() // existe uma peça branca na diagonal à direita
                            ||
                        end.status() == whiteLeftDiag.status() // existe uma peça branca na diagonal à esquerda
                )
        )
        {
            return true;
        } else if(
            start.status() == WHITE // peça inicial é branca
                &&
                (
                        end.status() == blackRightDiag.status() // existe uma peça preta na diagonal à direita
                            ||
                        end.status() == blackLeftDiag.status() // existe uma peça preta na diagonal à esquerda
                )
        )
        {
            return true;
        }
        return false;
    }



    private boolean isValidMove(Position start, Position end){
        boolean diagMove = Math.abs(start.column() - end.column()) == 1; // diferença de 1 coluna - movimento na diagonal
        boolean captureMoveLeft = start.column() - end.column() == 2;
        boolean captureMoveRight = start.column() - end.column() == -2;
        if (
            // validar movimentos das peças brancas - brancas só movem para cima
                (
                        hasWhitePiece(start) // verifica se é peça branca
                            &&
                        isEmptyTile(end) // posição de destino está vazia
                            &&
                        diagMove
                            &&
                        end.line() - start.line() == 1 // linha maior 1 unidade que linha inicial, movimento sempre para cima
                ) ||
            // validar movimentos das peças pretas - pretas só movem para baixo
                (
                        hasBlackPiece(start) // verifica se é peça preta
                            &&
                        isEmptyTile(end) // posição de destino está vazia
                            &&
                        diagMove
                            &&
                        end.line() - start.line() == -1 // linha menor 1 unidade que linha inicial, movimento sempre para baixo
                )

        ) {
            return true;
        } else
            return false;
    }







}
