package no.westerdals.game

import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
class MovesEntity(


        @ApiModelProperty("The game the move belongs to")
        @get:ManyToOne
        var gameentity: GameEntity? = null,

        @ApiModelProperty("Name of the player doing the move")
        @get:NotNull
        var playerusername: String,

        @ApiModelProperty("x coordinate for placement on the board")
        @get:NotNull
        var x: Int,

        @ApiModelProperty("y coordinate for placement on the board")
        @get:NotNull
        var y: Int,

        @ApiModelProperty("Id of the game move")
        @get:Id
        @get:GeneratedValue
        var id: Long? = null

)