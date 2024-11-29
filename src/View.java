import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {

    Game model;
    Board board;

    View(Game model){
        this.model = model;
        board = new Board("Teste", model.getBoardSize(), model.getBoardSize(), 50);
        board.setIconProvider(this::icon);
        board.addMouseListener((line, column) -> click(line, column));
        board.setBackgroundProvider(this::background);
        board.addAction("random", this::random);
        board.addAction("new", this::newBoard);
        board.addAction("save", this::saveStatus);
        board.addAction("load", this::loadStatus);
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
    private int startLine = 0;
    private int startColumn = 0;
    private void click(int line, int column){
        if(!control){
            if(model.getValidMoves(line, column)){
                control = true;
                startLine = line;
                startColumn = column;
            }
        } else if (control){
            model.movePiece(startLine, startColumn, line, column);
            control = false;
        }
    }

    Color background(int line, int column){
        if(model.isBlackTile(line, column)){
            return StandardColor.BLACK;
        } else if (!model.isBlackTile(line, column)){
            return StandardColor.WHITE;
        } else if (model.getValidMoves(line, column)){
            return StandardColor.AQUA;
        } else if (!model.isBlackTile(line, column)){
            return StandardColor.RED;
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
