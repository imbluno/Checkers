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
        board = new Board(getTurn, model.getBoardSize(), model.getBoardSize(), 50);
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

    private String getTurn = model.isBlackTurn() ? "Vez das peças pretas" : "Vez das peças brancas";
    private void updateTurn() {
        board.setTitle(getTurn);
    }
    private void click(int line, int column){
        if(!(this.control)){
            if (model.isBlackTurn() && model.board[line][column] == Game.BLACK) { // jogada das peças pretas
                this.control = true;
                startLine = line;
                startColumn = column;
            } else if (!model.isBlackTurn() && model.board[line][column] == Game.WHITE) { // jogada das peças brancas
                this.control = true;
                startLine = line;
                startColumn = column;
            }
            }
        else if (this.control){
            model.movePiece(this.startLine, this.startColumn, line, column);
            model.changeTurn();
            updateTurn();
            this.control = false;
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

    void random(){

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
