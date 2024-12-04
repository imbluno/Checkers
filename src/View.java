import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {

    Game model;
    Board board;

    private int startLine = 0;
    private int startColumn = 0;


    View(Game model){
        this.model = model;
        board = new Board(getTurn(), Game.getBoardSize(), Game.getBoardSize(), 50);
        board.setIconProvider(this::icon);
        board.addMouseListener(this::click);
        board.setBackgroundProvider(this::background);
        board.addAction("random", this::random);
        board.addAction("new", this::newBoard);
        board.addAction("save", this::saveStatus);
        board.addAction("load", this::loadStatus);
        board.refresh();
    }

    String icon(int line, int column){
        if(model.board[line][column] == Game.WHITE){
            return "white.png";
        } else if(model.board[line][column] == Game.BLACK){
            return "black.png";
        } else {
            return null;
        }
    }
    private boolean control = false;

    public void updateTurn() {
        board.setTitle(getTurn()); // imprime no título a vez do jogador
    }
    private String getTurn(){
        if (model.isBlackTurn()){
            return "Vez das peças pretas";
        } else {
            return "Vez das peças brancas";
        }
    }

    private void click(int line, int column) {
        // valida se existe peça do jogador e se é a vez dele
        if (!this.control) {
            if ((model.isBlackTurn() && model.board[line][column] == Game.BLACK) || // se for uma peça preta na vez das peças pretas
                    (!model.isBlackTurn() && model.board[line][column] == Game.WHITE)) { // se for uma peça branca na vez das peças brancas
                this.control = true; // coloca a variável de controlo a "true" para permitir que o próximo click faça o movimento
                startLine = line; // guarda a linha inicial na variável para usar no movimento seguinte
                startColumn = column; // guarda a coluna inicial na variável para usar no movimento seguinte
            }
        } else {
            // se o click for na mesma peça, não faz nada
            if (startLine == line && startColumn == column) {
                return;
            }

            // se o click for numa peça do jogador, guarda a posição
            if ((model.isBlackTurn() && model.board[line][column] == Game.BLACK) ||
                    (!model.isBlackTurn() && model.board[line][column] == Game.WHITE)) {
                startLine = line;
                startColumn = column;
                return; // permite selecionar uma nova peça
            }

            // obriga a captura, caso exista
            if (model.hasCaptureMove()) {
                if (model.isValidMove(startLine, startColumn, line, column)) { // valida o movimento
                    model.movePiece(startLine, startColumn, line, column); // move a peça
                    updateTurn(); // passa a vez ao oponente
                    this.control = false; // reseta a variável de controlo
                }
            } else {
                // tenta mover normalmente, caso não exista captura
                if (model.isValidMove(startLine, startColumn, line, column)) { // valida o movimento
                    model.movePiece(startLine, startColumn, line, column); // move a peça
                    updateTurn(); // passa a vez ao oponente
                    this.control = false; // reseta a variável de controlo
                }
            }
        }
    }

    private Color background(int line, int column){
        if(model.isBlackTile(line, column)){
            return StandardColor.BLACK;
        } else if (!model.isBlackTile(line, column)){
            return StandardColor.WHITE;
        }
        return null;
    }

    void newBoard(){
        View gui = new View(new Game());
        gui.start();
    }

    void saveStatus(){

    }

    void loadStatus(){

    }

    public void random(){
        // A maximum of 32 possible valid moves on a 8x8 board (12 pieces per player)
        int[][] validMoves = new int[32][4]; // each move is stored as [startLine, startColumn, endLine, endColumn]
        int validMoveCount = 0;

        // Loop through the entire board to find all valid moves for the current player's pieces
        for (int startLine = 0; startLine < Game.getBoardSize(); startLine++) {
            for (int startColumn = 0; startColumn < Game.getBoardSize(); startColumn++) {
                // Check if the current piece belongs to the current player
                if ((model.isBlackTurn() && model.board[startLine][startColumn] == Game.BLACK) ||
                        (!model.isBlackTurn() && model.board[startLine][startColumn] == Game.WHITE)) {

                    // Try all possible moves for the current piece
                    for (int endLine = 0; endLine < Game.getBoardSize(); endLine++) {
                        for (int endColumn = 0; endColumn < Game.getBoardSize(); endColumn++) {
                            // Check if the move is valid
                            if (model.isValidMove(startLine, startColumn, endLine, endColumn)) {
                                // Store the valid move in the array
                                validMoves[validMoveCount][0] = startLine;
                                validMoves[validMoveCount][1] = startColumn;
                                validMoves[validMoveCount][2] = endLine;
                                validMoves[validMoveCount][3] = endColumn;
                                validMoveCount++; // Increment valid move counter
                            }
                        }
                    }
                }
            }
        }

        // If there are valid moves, select one randomly
        if (validMoveCount > 0) {
            // Get a random index from 0 to validMoveCount - 1
            int randomIndex = (int) (Math.random() * validMoveCount); // Select random index between 0 and validMoveCount - 1

            // Get the random valid move
            int startLine = validMoves[randomIndex][0];
            int startColumn = validMoves[randomIndex][1];
            int endLine = validMoves[randomIndex][2];
            int endColumn = validMoves[randomIndex][3];

            // Move the piece and update the turn
            model.movePiece(startLine, startColumn, endLine, endColumn);
            updateTurn(); // Pass the turn to the opponent
        }
    }

    void start(){
        board.open();
        model.getEmptyBoard();
        model.initializeBoard();
    }

    public static void main(String[] args) {
        View gui = new View(new Game());
        gui.start();
    }
}
