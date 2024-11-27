import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {

    Game model;
    Board board;

    View(Game model){
        this.model = model;
        board = new Board("Teste", model.getBoardSize(), model.getBoardSize(), 80);
        board.setIconProvider(this::icon);
        board.addMouseListener(this::click);
        board.setBackgroundProvider(this::background);
        board.addAction("random", this::random);
        board.addAction("new", this::newBoard);
        board.addAction("save", this::saveStatus);
        board.addAction("load", this::loadStatus);
    }

    String icon(int line, int column){
        Position compare = model.getBoardPosition(line, column);
        if(compare.status() == Game.WHITE){
            return "white.png";
        } else if(compare.status() == Game.BLACK){
            return "black.png";
        } else {
            return null;
        }
    }

    void click(int line, int column){
        Position compare = model.getBoardPosition(line, column);

    }

    void movement(Position move){

    }

    Color background(int line, int column){

    }

    void newBoard(){

    }

    void saveStatus(){

    }

    void loadStatus(){

    }

    void random(){

    }

    void start(){

    }




    public static void main(String[] args) {

    }
}
