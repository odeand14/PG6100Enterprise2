package no.westerdals.game


import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Api(value = "/game", description = "Handling creating game requests and playing a game")

@RestController
class RestApi(
        private val gamecrud: GameRepository,
        private val movescrud: MovesRepository,
        private val requestcrud: GameRequestRepository

) {
/**
    //TODO: ONLY FOR DEBUGGING PURPOSES, REMEMBER TO REMOVE
    @GetMapping("/api/test")
    fun getTest(session: HttpSession): ResponseEntity<String> {
        println(session.attributeNames.toList().joinToString(" "))
        println(session.id)

        val aa = SecurityContextHolder.getContext().authentication

        return ResponseEntity.ok(session.attributeNames.toList().joinToString(" "))
    }*/


    @ApiOperation("Gets a game request by id")
    @GetMapping("/api/gameRequests/{id}")
    fun getGameRequest(
        @ApiParam("Gamerequest id")
        @PathVariable id: Long): ResponseEntity<GameRequestEntity> {
        return ResponseEntity.ok(requestcrud.findOneById(id))
    }


    @ApiOperation("Creates a game request that can be joined by the id returned")
    @PostMapping("/api/gameRequests/user/{playerid}")
    fun createGameRequest(
            @ApiParam("Id of the player making the gamerequest")
            @PathVariable playerid: String): ResponseEntity<Long> {
        //  val aa = SecurityContextHolder.getContext().authentication
        val reQuestid = requestcrud.createRequest(playerid)

        return ResponseEntity.ok(reQuestid)
    }


    @ApiOperation("Patches a game request by requestid and player2id so that a game can be created")
    @PatchMapping("/api/gameRequests/{reqid}/user/{playerid}")
    fun acceptGameRequest(
            @ApiParam("Id of the player accepting the gamerequest.")
            @PathVariable playerid: String,

            @ApiParam("Id of the gamerequest to accept")
            @PathVariable reqid: Long): ResponseEntity<Long> {


        val foundRequest = requestcrud.findOneById(reqid) ?: return ResponseEntity.notFound().build()
        if (foundRequest.player1username == playerid) return ResponseEntity.status(409).build()

        val requestID = requestcrud.acceptRequest(reqid, playerid) ?: return ResponseEntity.notFound().build()

        val requestEntity = requestcrud.findOneById(requestID) ?: return ResponseEntity.notFound().build()

        println("##########" + requestEntity.player1username + requestEntity.player2username)

        val valium = gamecrud.createGame(requestEntity.player1username, requestEntity.player2username!!)
        return ResponseEntity.ok(valium)
    }

    @ApiOperation("Gets a game by id")
    @GetMapping("/api/games/{id}")
    fun getGamess(
            @ApiParam("Id of the game you want")
            @PathVariable id: Long): ResponseEntity<GameEntity> {

        return ResponseEntity.ok(gamecrud.findOneByGameId(id))
    }


    @ApiOperation("Posts a move on the gameboard by gameid x and y coordinates, and userid")
    @PostMapping("/api/move/{gameid}/{xcoord}/{ycoord}/users/{playerusername}",
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun postMove(
            @ApiParam("Username of the user posting a game move")
            @PathVariable playerusername: String,

            @ApiParam("Id of the game to post move to")
            @PathVariable gameid: Long,

            @ApiParam("X coordinate on the board")
            @PathVariable xcoord: Int,

            @ApiParam("Y coordinate on the")
            @PathVariable ycoord: Int): ResponseEntity<Map<String, String>> {

        // val playerusername = playerid


        if (0 > xcoord || xcoord > 2 || ycoord > 2 || ycoord < 0) {
            return ResponseEntity.badRequest().body(hashMapOf("error" to "coordinates out of bound", "moveID" to "", "gameStatus" to "0"))
        }

        val foundGame = gamecrud.findOneByGameId(gameid) ?: return ResponseEntity.status(404).body(hashMapOf("error" to "NO GAME FOUND", "moveID" to "", "gameStatus" to "0"))

        if (foundGame.gameDone != 0) {
            return ResponseEntity.status(406).body(hashMapOf("error" to "GAME IS FINISHED", "moveID" to ""))
        }

        val movelist = foundGame.gameMoves
        if (movelist == null || movelist.size <= 0) {
            if (playerusername != foundGame.player1username) {
                return ResponseEntity.badRequest().body(hashMapOf("error" to "PLAYER 1 MUST MAKE THE FIRST MOVE", "moveID" to "", "gameStatus" to "0"))
            }
            val moveId = movescrud.createMove(gameid, playerusername, xcoord, ycoord)

            println(foundGame.gameMoves)
            return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}", "gameStatus" to "0"))
        }

        //TODO: bad perf
        for (entity in movelist) {
            if ((entity.x == xcoord) && (entity.y == ycoord)) {
                return ResponseEntity.status(409).body(hashMapOf("error" to "Coordinate already placed", "moveID" to "", "gameStatus" to "0"))
            }
        }


        val lastPlayerId = movelist.last().playerusername
        if (playerusername == lastPlayerId) {
            return ResponseEntity.status(409).build()
        }


        val moveId = movescrud.createMove(gameid, playerusername, xcoord, ycoord)


        //val gameWon = GameWon(foundGame.gameMoves.sortedWith(compareBy({ it.id })))
        val gameWon = GameWon(foundGame.gameMoves)
        val winner: Long
        if (movelist.size >= 9 && !gameWon) {
            gamecrud.changeGameStatus(gameid, 3)
            return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}", "gameStatus" to "3"))
        }
        if (gameWon) {
            if (playerusername == foundGame.player1username) {
                gamecrud.changeGameStatus(gameid, 1)
                return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}", "gameStatus" to "1"))
            } else {
                gamecrud.changeGameStatus(gameid, 2)
                return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}", "gameStatus" to "2"))

            }
        }


        //movescrud.findOne(moveId)
        return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}", "gameStatus" to "0"))

    }

    //TODO: make sequense instead to make sure we get the first correctly
    fun GameWon(movelist: List<MovesEntity>): Boolean {

        //TODO: this is the easiest way in kotlin?
        val board1 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);
        val board2 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);
        val board3 = arrayOf<Pieces>(Pieces.Empty, Pieces.Empty, Pieces.Empty);

        val board = arrayOf(board1, board2, board3)

        val gameBoard = GameBoardLogic(3, 3)


        var boolswitch = true;
        for (move in movelist) {
            if (boolswitch) {
                board[move.x][move.y] = Pieces.Cross
                boolswitch = false
            } else {
                board[move.x][move.y] = Pieces.Circle
                boolswitch = true
            }
        }

        return gameBoard.isGameWon2(board)
    }




}