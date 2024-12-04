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
        board = new Board(getTurn(), model.getBoardSize(), model.getBoardSize(), 50);
        board.setIconProvider(this::icon);
        board.addMouseListener(this::click);
        board.setBackgroundProvider(this::background);
        board.addAction("random", this::random);
        board.addAction("new", this::newBoard);
        board.addAction("save", this::saveStatus);
        board.addAction("load", this::loadStatus);
        board.refresh();
    }
    private String getTurn(){
        if (model.isBlackTurn()){
            return "Vez das peças pretas";
        } else {
            return "Vez das peças brancas";
        }
    }

    // Define the icon for each square on the board
    String icon(int line, int column){
        if(model.board[line][column] == Game.EMPTY){
            return null;
        }
        else if(model.board[line][column] == Game.BLACK){
            return "black.png"; // white's turn
        }
        else {
            return "white.png"; // black's turn
        }
    }

    // Define the background color for each square
    Color background(int line, int column){
        if(model.isBlackTile(new Position(line, column))){
            return StandardColor.GRAY;
        }
        return StandardColor.WHITE;
    }

    // Update the turn message on the board
    public void updateTurn() {
        String turnMessage = model.isBlackTurn() ? "Black's Turn" : "White's Turn";
        board.setTitle(turnMessage);
    }

    // Initialize the board with random placement
    private void random() {
        model.initializeBoard();
        updateTurn();
        board.refresh();
    }

    // Create a new empty board
    private void newBoard() {
        model.getEmptyBoard();
        updateTurn();
        board.refresh();
    }

    // Save the current game state (placeholder, not implemented)
    private void saveStatus() {
        // implement logic to save game state
    }

    // Load a previously saved game state (placeholder, not implemented)
    private void loadStatus() {
        // implement logic to load game state
    }

    private boolean control = false;
    Position startPosition;
    // Handle user clicks on the board
    private void click(int line, int column) {
        Position position = new Position(line, column);
        if (!this.control) {
            // Player selects a piece
            if (model.isBlackTurn() && model.board[position.line()][position.column()] == Game.BLACK) { // Black's turn
                this.control = true;
                startPosition = position;
            } else if (!model.isBlackTurn() && model.board[position.line()][position.column()] == Game.WHITE) { // White's turn
                this.control = true;
                startPosition = position;
            }
        } else {
            // Player attempts to move the selected piece
            if (model.isValidMove(startPosition, position)) {
                model.movePiece(startPosition, position); // Move the piece
                model.changeTurn(); // Change turn only if the move was valid
                updateTurn(); // Update the turn message
                this.control = false; // Reset control
            } else {
                this.control = false;
            }
        }
    }
}
