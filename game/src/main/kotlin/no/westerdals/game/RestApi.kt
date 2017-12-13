package no.westerdals.game


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController



@RestController
class RestApi(
        private val crud: GameRepository
) {
    @GetMapping("/markleif")
    fun getMarkleif(): String {


        return "Det gikk leif!"
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
    fun createGame(@RequestBody gameEntity: GameEntity){

        

    }

    @PostMapping("/moves")
    fun posMove(@RequestBody gameMove: MovesEntity){

    }
}