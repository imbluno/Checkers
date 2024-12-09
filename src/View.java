import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class View {

    static Game model;
    Board board;

    private int startLine = 0;
    private int startColumn = 0;


    View(Game model){
        View.model = model;
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
        if (line >= 0 && line < Game.getBoardSize() && column >= 0 && column < Game.getBoardSize()){ // valida que só serão preenchidas casas dentro do tabuleiro, prevenindo erros
            if(model.board[line][column] == Game.WHITE){
                return "white.png";
            } else if(model.board[line][column] == Game.BLACK){
                return "black.png";
            }
        }
            return null;
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
        checkEndGame(); // verifica se o jogo acabou antes de mover a peça
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
                checkEndGame(); // verifica se o jogo acabou
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
            writer.write(Game.getPieceAmount() + "\n"); // escreve o número de peças iniciais
            int currentPlayerTurn = model.isBlackTurn() ? 0 : 1; // 0 vez das pretas, 1 vez das brancas
            writer.write(currentPlayerTurn + "\n"); // guarda a vez do jogador que jogaria a seguir
            for (int i = 0; i < Game.getBoardSize(); i++) {
                for (int j = 0; j < Game.getBoardSize(); j++) {
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

        try (Scanner scanner = new Scanner(file)) {
            int loadedBoardSize = scanner.nextInt(); // 1.ª linha - tamanho do tabuleiro
            scanner.nextLine();

            int loadedPieceAmount = scanner.nextInt(); // 2.ª linha - número de peças inicial
            scanner.nextLine();

            int loadedTurnInt = scanner.nextInt(); // 3.ª linha - vez do jogador, 0 para pretas e 1 para brancas
            scanner.nextLine();
            boolean loadedTurn = loadedTurnInt == 0;

            int[][] loadedBoard = new int[loadedBoardSize][loadedBoardSize]; // matriz para guardar os valores das peças no tabuleiro

            for (int i = 0; i < loadedBoardSize; i++) {
                for (int j = 0; j < loadedBoardSize; j++) {
                    if (scanner.hasNextInt()) {
                        loadedBoard[i][j] = scanner.nextInt(); // lê cada posição
                    }
                }
            }

            newGame = new Game(loadedBoardSize, loadedPieceAmount, loadedTurn, loadedBoard); // cria um jogo para ser chamado pelo construtor especial para load

            sendMessage("Jogo carregado.");
        } catch (IOException e) {
            sendMessage("Erro ao carregar: " + e.getMessage());
        } catch (Exception e) {
            sendMessage("Erro inesperado ao carregar.");
        }
        if (newGame != null) {
            new View(newGame); // cria uma view com o tabuleiro carregado
            board.open(); // abre o jogo numa nova janela
        }
    }

    public void random() {
        checkEndGame(); // verifica se o jogo acabou antes de mover a peça
        int[][] validMoves = new int[Game.getBoardSize() * Game.getBoardSize() / 2][4]; // matriz com os movimentos válidos, dependendo do tamanho do tabuleiro
        int validMoveCount = 0;

        for (int startLine = 0; startLine < Game.getBoardSize(); startLine++) { // percorre o tabuleiro à procura de jogadas
            for (int startColumn = 0; startColumn < Game.getBoardSize(); startColumn++) {
                if ((model.isBlackTurn() && model.board[startLine][startColumn] == Game.BLACK) || // valida a peça e o jogador
                        (!model.isBlackTurn() && model.board[startLine][startColumn] == Game.WHITE)) {
                    for (int endLine = 0; endLine < Game.getBoardSize(); endLine++) { // percorre o tabuleiro à procura de posições finais
                        for (int endColumn = 0; endColumn < Game.getBoardSize(); endColumn++) {
                            if (model.isValidMove(startLine, startColumn, endLine, endColumn)) { // valida que a jogada é possível
                                // guarda a jogada numa matriz, na linha validMoveCount
                                validMoves[validMoveCount][0] = startLine;
                                validMoves[validMoveCount][1] = startColumn;
                                validMoves[validMoveCount][2] = endLine;
                                validMoves[validMoveCount][3] = endColumn;
                                validMoveCount++; // incrementa a variável indicando que existe mais uma jogada válida
                            }
                        }
                    }
                }
            }
        }

        if (validMoveCount > 0) {  // escolhe uma jogada aleatoriamente se existir uma jogada válida
            int randomIndex = (int) (Math.random() * validMoveCount);
            // acede à posição randomIndex do matriz, colocando cada uma das posições da linha numa variável, para passar à função de movimento
            int startLine = validMoves[randomIndex][0];
            int startColumn = validMoves[randomIndex][1];
            int endLine = validMoves[randomIndex][2];
            int endColumn = validMoves[randomIndex][3];
            model.movePiece(startLine, startColumn, endLine, endColumn);// move a peça
            updateTurn(); // passa a vez

        }
    }

    public void checkEndGame() {
        int[] counts = model.countPieces(); // chama a função que retorna a contagem das peças de cada jogador
        if (counts[0] == 0) sendMessage("Peças brancas venceram!"); // se não houver peças pretas - posição 0 do array são peças pretas
        else if (counts[1] == 0) sendMessage("Peças pretas venceram!"); // se não houver peças brancas - posição 1 do array são peças brancas
        else if (!model.hasValidMoves()) { // se não houver mais movimentos
            if (counts[0] > counts[1]) sendMessage("Peças pretas venceram!"); // se houver mais peças pretas
            else if (counts[1] > counts[0]) sendMessage("Peças brancas venceram!"); // se houver mais peças brancas
            else sendMessage("Empate!"); // se forem em igual número
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
