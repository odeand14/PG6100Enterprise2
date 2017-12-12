package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Repository
interface GameRepository : CrudRepository<GameEntity, String>


@RestController
class RestApi(
        private val crud: GameRepository
) {

    @GetMapping("/game")
    fun getGame(): String {

        return "Gametest"
    }


    @PostMapping("/moves")
    fun posMove(@RequestBody gameMove: MovesEntity){

    }
}