package no.westerdals.game


import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class RestApi(
        private val gamecrud: GameRepository,
        private val movescrud: MovesRepository,
        private val requestcrud: GameRequestRepository

) {


    @GetMapping("/gameRequests/{id}")
    fun getGameRequest(@PathVariable id: Long): ResponseEntity<GameRequestEntity> {
        val valium = requestcrud.findOneById(id)
        return ResponseEntity.ok(requestcrud.findOneById(id))
    }


    @PostMapping("/gameRequests/users/{id}")
    fun createGameRequest(@PathVariable id: Long): ResponseEntity<Long> {

        val reQuestid = requestcrud.createRequest(id)

        return ResponseEntity.ok(reQuestid)
    }

    @PatchMapping("/gameRequest/accept/{playerid}/{id}")
    fun acceptGameRequest(@PathVariable playerid: Long, @PathVariable id: Long): ResponseEntity<Long> {
        val requestID = requestcrud.acceptRequest(id, playerid);

        val requstur = requestcrud.findOneById(requestID);

        val valium = gamecrud.createGame(requstur.player1Id, requstur.player2Id!!)
        return ResponseEntity.ok(valium)
    }


    @PostMapping("/gameRequests/{id}")
    fun postMove(@PathVariable id: Long): ResponseEntity<Long> {

        val reQuestid = requestcrud.createRequest(id)

        return ResponseEntity.ok(reQuestid)
    }

    @GetMapping("/game/{id}")
    fun getGamess(@PathVariable id: Long): ResponseEntity<GameEntity> {

        return ResponseEntity.ok(gamecrud.findOneByGameId(id))
    }

    /**
    @PostMapping("/gameRequests/users/{id}")
    fun createGameRequest(@PathVariable id: Long) : Long {

    val reQuestid = requestcrud.createRequest(id)

    return reQuestid
    }
     */


    @PostMapping("/move/{gameid}/{playerid}/{xcoord}/{ycoord}",
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun postMove(@PathVariable gameid: Long, @PathVariable playerid: Long, @PathVariable xcoord: Int, @PathVariable ycoord: Int) : ResponseEntity<Map<String, String>> {

        val foundGame = gamecrud.findOneByGameId(gameid) ?: return ResponseEntity.status(404).body(hashMapOf("error" to "NO GAME FOUND", "moveID" to ""))

        val movelist = movescrud.findAll()
        if (!movelist.any()){
            if (playerid != foundGame.player1Id ) {
                return ResponseEntity.badRequest().body(hashMapOf("error" to "PLAYER 1 MUST MAKE THE FIRST MOVE", "moveID" to ""))
            }
            val moveId = movescrud.createMove(gameid, playerid, xcoord, ycoord)
            return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}"))
        }
        val lastPlayerId = movelist.last().playerId
        if(playerid == lastPlayerId) {
            return ResponseEntity.status(409).build()
        }

        val moveId = movescrud.createMove(gameid, playerid, xcoord, ycoord)

        //movescrud.findOne(moveId)
        return ResponseEntity.ok(hashMapOf("error" to "NONE", "moveID" to "${moveId}"))

    }


    @GetMapping("/game")
    fun getGame(): String {

        return "Gametest"
    }

    @GetMapping("/")
    fun gameRoot(): String {

        return "gameroot"
    }

    @PostMapping("/game")
    fun createGame(@RequestBody gameEntity: GameEntity) {


    }

    @PostMapping("/moves")
    fun posMove(@RequestBody gameMove: MovesEntity) {

    }
}