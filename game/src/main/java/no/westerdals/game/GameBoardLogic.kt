package no.westerdals.game

import java.util.ArrayList


class GameBoard(val boardSize: Int, val rowsToWin: Int) {




    //just prints the status of the board
    fun printBoard(board: Array<Array<Pieces>>) {
        for (i in 0 until boardSize) {
            val column = board[i]
            for (j in 0 until boardSize) {
                println(column[j])
            }
            println("######")
        }
    }

/** This one is for if i need to check won with some coordinates as i set them. But the other method should work without coordinates.
    /**
     * Checks if game is won by comparing rows columns and diagonals.
     *
     * @return boolean is game won
     */
    fun isGameWon(x: Int, y: Int, board: Array<Array<Pieces>>): Boolean {

        //HORIZONTAL
        if (y + 1 < boardSize && y + 2 < boardSize) {
            if (board[x][y] === board[x][y + 1] && board[x][y] === board[x][y + 2]) {
                return true
            }
        }

        if (y - 1 >= 0 && y - 2 >= 0) {
            if (board[x][y] === board[x][y - 1] && board[x][y] === board[x][y - 2]) {
                return true
            }
        }

        if (y - 1 >= 0 && y + 1 < boardSize) {
            if (board[x][y] === board[x][y - 1] && board[x][y] === board[x][y + 1]) {
                return true
            }
        }


        //HORIZONTAL
        //VERTICAL
        if (x + 1 < boardSize && x + 2 < boardSize) {

            if (board[x][y] === board[x + 1][y] && board[x][y] === board[x + 2][y]) {
                return true
            }
        }

        if (x - 1 >= 0 && x - 2 >= 0) {
            if (board[x][y] === board[x - 1][y] && board[x][y] === board[x - 2][y]) {
                return true
            }
        }

        if (x - 1 >= 0 && x + 1 < boardSize) {
            if (board[x][y] === board[x - 1][y] && board[x][y] === board[x + 1][y]) {
                return true
            }
        }
        //VERTICAL

        //DIAGONAL

        if (x + 1 < boardSize && y - 1 >= 0 && x - 1 >= 0 && y + 1 < boardSize) {
            if (board[x][y] === board[x + 1][y - 1] && board[x][y] === board[x - 1][y + 1]) {
                return true
            }
        }


        if (x + 1 < boardSize && y - 1 >= 0 && x + 2 < boardSize && y - 2 >= 0) {
            if (board[x][y] === board[x + 1][y - 1] && board[x][y] === board[x + 2][y - 2]) {
                return true
            }
        }

        if (y + 1 < boardSize && x - 1 >= 0 && y + 2 < boardSize && x - 2 >= 0) {
            if (board[x][y] === board[x - 1][y + 1] && board[x][y] === board[x - 2][y + 2]) {
                return true
            }
        }
        //DIAGOAL

        //DIAGONAL2

        if (x + 1 < boardSize && y + 1 < boardSize && x - 1 >= 0 && y - 1 >= 0) {
            if (board[x][y] === board[x + 1][y + 1] && board[x][y] === board[x - 1][y - 1]) {
                return true
            }
        }

        if (x + 1 < boardSize && y + 1 < boardSize && x + 2 < boardSize && y + 2 < boardSize) {
            if (board[x][y] === board[x + 1][y + 1] && board[x][y] === board[x + 2][y + 2]) {
                return true
            }
        }

        if (x - 1 >= 0 && y - 1 >= 0 && x - 2 >= 0 && y - 2 >= 0) {
            if (board[x][y] === board[x - 1][y - 1] && board[x][y] === board[x - 2][y - 2]) {
                return true
            }
        }
        //DIAGONAL2

        return false
    }

*/
    /**
     * Checks if a piece has a valid position on the board.
     *
     * @param x     int x coordinate
     * @param y     int y coordinate
     * @param piece enum piece to put on the board
     * @return boolean valid board position
     */
    fun isValidPosition(x: Int, y: Int, piece: Pieces, board: Array<Array<Pieces>>): Boolean {
        return board[x][y] === Pieces.Empty
    }

    /**
     * Sets piece on the board on specific coordinate
     *
     * @param x     int x coordinate
     * @param y     int y coordinate
     * @param piece enum piece to put on the board
     **/
    fun setPiece(x: Int, y: Int, piece: Pieces, board: Array<Array<Pieces>>): Array<Array<Pieces>> {
        board[x][y] = piece
        return board
    }

    /**
    private fun useMove() {
    movesLeft--
    }**/



    //TODO: cleanup
    fun isGameWon2( board: Array<Array<Pieces>>): Boolean {
            var vertical: MutableList<Pieces>
            var horizontal: MutableList<Pieces>

            for (i in 0 until boardSize) {
                val rootPosition = board[i]
                horizontal = ArrayList()
                vertical = ArrayList()
                for (j in 0 until boardSize) {
                    print(rootPosition[j].toString() + " ")

                    val horizontalRootPosition = board[j]
                    horizontal.add(horizontalRootPosition[i])
                    vertical.add(rootPosition[j])
                }
                //Checks if game is won vertically
                if (checkRows(vertical))
                    return true
                //Checks if game is won horizontally
                else if (checkRows(horizontal))
                    return true

                println("\n")
            }

        //checks diagonally
        val diagonal: MutableList<Pieces> = ArrayList()
            val diagonal2: MutableList<Pieces> = ArrayList()

            for (j in 0 until boardSize) {
                diagonal.add(board[j][j])
                diagonal2.add(board[boardSize - (j + 1)][j])
            }

            //yes i know this is redundant use of if, it stays for some bug checking
        //diagonal 1
        if (checkRows(diagonal))
                return true
        //diagonal 2
        else if (checkRows(diagonal2))
                return true
            else
                return false
        }

    //Checks if a row with pieces are a streak
    private fun checkRows(list: List<Pieces>): Boolean {
        var xInARowCounter = 1
        var horizontalTemp: Pieces? = null
        for (piece in list) {

            if (piece === horizontalTemp && piece !== Pieces.Empty) {
                xInARowCounter++
                println(xInARowCounter)
                if (xInARowCounter == rowsToWin)
                    return true
            } else
                xInARowCounter = 1

            horizontalTemp = piece
        }
        return false
    }

}
