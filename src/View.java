import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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

    public void sendMessage(String message){
        board.showMessage(message);
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
            // move a peça
            if (model.isValidMove(startLine, startColumn, line, column)) { // valida o movimento
                model.movePiece(startLine, startColumn, line, column); // move a peça
                updateTurn(); // passa a vez ao oponente
                this.control = false; // reseta a variável de controlo
            }
        }
    }

    private Color background(int line, int column){
        return model.isBlackTile(line, column) ? StandardColor.BLACK : StandardColor.WHITE; // valida se a casa de jogo é preta e coloca a cor correta em ambos os casos
    }

    void newBoard(){
        int userBoardSize = board.promptInt("Introduz o tamanho do tabuleiro:");
        if (userBoardSize <= 0){
            board.promptText("Tamanho inválido, introduz novamente.");
            return;
        }

        int userPieceAmount = board.promptInt("Introduz o número de peças para cada jogador:");
        if (userPieceAmount <= 0){
            board.promptText("Número de peças inválido, introduz novamente.");
            return;
        }

        View gui = new View(new Game(userBoardSize,userPieceAmount)); // cria uma view do jogo
        gui.start(); // inicia o jogo
    }

    public void saveStatus() {
        try (FileWriter writer = new FileWriter("game_status.txt")) { //
            writer.write(Game.getBoardSize() + "\n"); // escreve o tamanho do tabuleiro na primeira linha
            for (int i = 0; i < Game.getBoardSize(); i++) {
                for (int j = 0; j < Game.getBoardSize(); j++) {
                    // Save the board values
                    if (model.isEmptyTile(i, j)) {
                        writer.write("0 ");  // posição vazia
                    } else if (model.hasBlackPiece(i, j)) {
                        writer.write("1 ");  // peça preta
                    } else if (model.hasWhitePiece(i, j)) {
                        writer.write("2 ");  // peça branca
                    } else {
                        writer.write("_ ");  // casa branca (sempre sem peça)
                    }
                }
                writer.write("\n");
            }
            sendMessage("Jogo guardado.");
        } catch (IOException e) {
            sendMessage("Erro ao guardar: " + e.getMessage());
        }
    }

    public void loadStatus() {
        File file = new File("game_status.txt");
        if (!file.exists()) {
            sendMessage("Não foi encontrado nenhum jogo.");
            return;
        }
        Game newGame = null;
        try (Scanner scanner = new Scanner(new File(String.valueOf(file)))) {
            if (!scanner.hasNextInt()) { // verifica se a primeira linha do ficheiro tem o tamanho do tabuleiro
                sendMessage("Ficheiro inválido.");
                return;
            }


            int loadedBoardSize = scanner.nextInt(); // define o tamanho do tabuleiro através do valor na primeira linha
            scanner.nextLine(); // move para a linha seguinte

            newGame = new Game(loadedBoardSize, 0); // cria um jogo sem peças, com o tamanho definido na primeira linha

            for (int i = 0; i < loadedBoardSize; i++) {
                for (int j = 0; j < loadedBoardSize; j++) {
                    String cell = scanner.next();  // Read the cell value as a string

                    if (cell.equals("0")) {
                        newGame.board[i][j] = Game.EMPTY;  // casa preta vazia
                    } else if (cell.equals("1")) {
                        newGame.board[i][j] = Game.BLACK;  // peça preta
                    } else if (cell.equals("2")) {
                        newGame.board[i][j] = Game.WHITE;  // peça branca
                    } else if (cell.equals("_")) {
                        newGame.board[i][j] = Game.EMPTY;  // casa branca
                    } else {
                        sendMessage("Dados inválidos.");
                        return;
                    }
                }
            }
            sendMessage("Jogo carregado.");

        } catch (IOException e) {
            sendMessage("Erro: " + e.getMessage());
        } catch (Exception e) {
            sendMessage("Erro inesperado.");
        }

        View gui = new View(newGame);
        gui.start();
    }



    public void random() {
        int[][] validMoves = new int[Game.getBoardSize() * Game.getBoardSize() / 2][4]; // matriz com os movimentos válidos, dependendo do tamanho do tabuleiro
        int validMoveCount = 0;

        for (int startLine = 0; startLine < Game.getBoardSize(); startLine++) { // percorre o tabuleiro à procura de jogadas
            for (int startColumn = 0; startColumn < Game.getBoardSize(); startColumn++) {
                if ((model.isBlackTurn() && model.board[startLine][startColumn] == Game.BLACK) || // valida a peça e o jogador
                        (!model.isBlackTurn() && model.board[startLine][startColumn] == Game.WHITE)) {

                    for (int endLine = 0; endLine < Game.getBoardSize(); endLine++) { // percorre o tabuleiro à procura de posições finais
                        for (int endColumn = 0; endColumn < Game.getBoardSize(); endColumn++) {
                            if (model.isValidMove(startLine, startColumn, endLine, endColumn)) { // valida que a jogada é possível
                                // guarda a jogada numa matriz
                                validMoves[validMoveCount][0] = startLine;
                                validMoves[validMoveCount][1] = startColumn;
                                validMoves[validMoveCount][2] = endLine;
                                validMoves[validMoveCount][3] = endColumn;
                                validMoveCount++;
                            }
                        }
                    }
                }
            }
        }

        // escolhe uma jogada aleatoriamente
        if (validMoveCount > 0) {
            int randomIndex = (int) (Math.random() * validMoveCount);
            int startLine = validMoves[randomIndex][0];
            int startColumn = validMoves[randomIndex][1];
            int endLine = validMoves[randomIndex][2];
            int endColumn = validMoves[randomIndex][3];

            // move a peça
            model.movePiece(startLine, startColumn, endLine, endColumn);
            updateTurn(); // passa a vez
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
