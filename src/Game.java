public class Game {
    // tamanho do tabuleiro
    private static final int BOARD_SIZE = 8;
    // valor para quando o espaço está vazio
    private static final int EMPTY = 0;
    // valor para quando existe uma peça preta na posição
    private static final int BLACK = 1;
    // valor para quando existe uma peça branca na posição
    private static final int WHITE = 2;
    // número de peças por jogador
    private static final int PIECE_AMOUNT = 12;
    // criação de matriz de posições, em que cada posição vai ter as coordenadas da própria posição e o status da mesma (EMPTY, BLACK, WHITE)
    private final Position[][] board = new Position[BOARD_SIZE][BOARD_SIZE];

    private void getEmptyBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = new Position(i, j, EMPTY);
            }
        }
    }

    private void initializeBoard() {
        // colocar as peças nas posições iniciais
        int blackPieces = PIECE_AMOUNT;
        int whitePieces = PIECE_AMOUNT;
        // colocar as peças pretas no tabuleiro
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(this.board[i][j])) {
                    this.board[i][j] = new Position(i, j, BLACK);
                }
            }
        }
        // colocar as peças brancas no tabuleiro
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(isBlackTile(this.board[i][j])) {
                    this.board[i][j] = new Position(i, j, WHITE);
                }
            }
        }
    }

    // variável para definir quem joga
    private boolean blackTurn = true;

    private void changeTurn () {
        this.blackTurn = !this.blackTurn;
    }

    private boolean isBlackTile(Position position) {
        if (
                (
                        position.line() % 2 == 0 // linhas pares
                                &&
                                position.column() % 2 == 1 // colunas ímpares
                ) ||
                        (
                                position.line() % 2 == 1 // linhas impares
                                        &&
                                        position.column() % 2 == 0 // colunas pares
                        )
        ) {
            return true;
        }
        return false;
    }
    private boolean isEmptyTile(Position position){
        return position.status() == EMPTY;
    }

    private boolean hasWhitePiece(Position position){
        return position.status() == WHITE;
    }

    private boolean hasBlackPiece(Position position){
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
