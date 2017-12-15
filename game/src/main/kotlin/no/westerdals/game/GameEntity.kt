package no.westerdals.game

import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import javax.persistence.*


@Entity
class GameEntity(

        @ApiModelProperty("Player 1 username of a game")
        @get:NotBlank
        var player1username: String,

        @ApiModelProperty("Player 2 username of a game")
        @get:NotBlank
        var player2username: String,


        @ApiModelProperty("The id if the game")
        @get:Id
        @get:GeneratedValue
        var gameId: Long? = null,


        //  0 is still going, 1 is player1 won,
        // 2 is player two won and 3 is draw.
        //
        @ApiModelProperty("Status of the game, 0 - still ongoing, 1 - player 1 won, 2 - player 2 won")
        @get:NotNull
        var gameDone: Int = 0,


        @ApiModelProperty("List over moves done by the players")
        @get:OneToMany(
                fetch = FetchType.LAZY,
                mappedBy = "gameentity",
                cascade = arrayOf(CascadeType.ALL)
        )
        var gameMoves: MutableList<MovesEntity> = mutableListOf()
)