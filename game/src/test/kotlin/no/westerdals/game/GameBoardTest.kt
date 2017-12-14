package no.westerdals.game

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class GameBoardTest {

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
    }

    @Test
    fun printBoard() {
        val board1 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);

        val board = arrayOf(board1, board2, board3);


        val gameBoard = GameBoardLogic(3, 3)


        //TODO: this looping cant be right.
        for (i in board.indices) {
            val column = board[i]
            for (j in column.indices) {
                println(board[i][j])
                assertEquals(Pieces.Empty, board[i][j])
            }
            println("######")
        }

    }



    @Test
    fun testWonGameDiagonal(){
        val board1 = arrayOf<Pieces>(Pieces.Circle, Pieces.Empty, Pieces.Empty);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Circle, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Circle);

        val board = arrayOf(board1, board2, board3);

        val gameBoard = GameBoardLogic(3,3);

        assertTrue(gameBoard.isGameWon2(board));
    }
    @Test
    fun testWonGameDiagonal2(){
        val board1 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Circle);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Circle, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Circle, Pieces.Empty, Pieces.Circle);

        val board = arrayOf(board1, board2, board3);

        val gameBoard = GameBoardLogic(3,3);

        assertTrue(gameBoard.isGameWon2(board));
    }

    @Test
    fun testWonGameVertical(){
        val board1 = arrayOf<Pieces>(Pieces.Cross, Pieces.Empty, Pieces.Circle);
        val board2 = arrayOf<Pieces>(Pieces.Circle, Pieces.Cross, Pieces.Circle);
        val board3 = arrayOf<Pieces>(Pieces.Circle, Pieces.Empty, Pieces.Circle);

        val board = arrayOf(board1, board2, board3);

        val gameBoard = GameBoardLogic(3,3);

        assertTrue(gameBoard.isGameWon2(board));
    }

    @Test
    fun testWonGameHorizontal(){
        val board1 = arrayOf<Pieces>(Pieces.Circle, Pieces.Circle, Pieces.Circle);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Cross, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Cross, Pieces.Empty, Pieces.Cross);

        val board = arrayOf(board1, board2, board3);

        val gameBoard = GameBoardLogic(3,3);

        assertTrue(gameBoard.isGameWon2(board));
    }

    @Test
    fun testNotWonGame(){
        val board1 = arrayOf<Pieces>(Pieces.Cross, Pieces.Empty, Pieces.Empty);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Circle, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Circle);

        val board = arrayOf(board1, board2, board3);

        val gameBoard = GameBoardLogic(3,3);

        assertFalse(gameBoard.isGameWon2(board));
    }

}