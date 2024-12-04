public record Position(int line, int column) {
    public Position[] validMoves(Game game) {
        // Define a maximum number of valid moves (maximum 4 for a piece)
        Position[] moves = new Position[4];
        int moveCount = 0;

        // Get the piece at this position
        int piece = game.getBoardPosition(new Position(this.line, this.column));

        // Define move directions based on the piece type
        int[][] directions;
        if (piece == Game.WHITE) {
            // White moves upward (negative line direction)
            directions = new int[][]{{-1, -1}, {-1, 1}, {-2, -2}, {-2, 2}};
        } else if (piece == Game.BLACK) {
            // Black moves downward (positive line direction)
            directions = new int[][]{{1, -1}, {1, 1}, {2, -2}, {2, 2}};
        } else {
            return new Position[0]; // No piece at the position, return empty array
        }

        // Iterate through each potential direction
        for (int[] dir : directions) {
            Position end = new Position(this.line + dir[0], this.column + dir[1]);
            if (game.isValidPosition(end) && game.isValidMove(this, end)) {
                // Add valid move to the array
                moves[moveCount] = end;
                moveCount++;
            }
        }

        // Create a new array with the correct size and copy the valid moves into it
        Position[] validMoves = new Position[moveCount];
        for (int i = 0; i < moveCount; i++) {
            validMoves[i] = moves[i];
        }

        return validMoves;
    }
}
