public record Position(int line, int column) {
    public static boolean isValid(int line, int column){
        return line >= 0 && column >= 0 && line < Game.getBoardSize() && column < Game.getBoardSize(); // verifica se a posição está dentro do tabuleiro
    }
}
