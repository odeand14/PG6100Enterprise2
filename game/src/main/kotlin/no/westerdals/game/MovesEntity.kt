package no.westerdals.game

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
class MovesEntity(


        @get:NotNull
        var gameId: Long,

        @get:NotNull
        var playerId: Long,

        @get:NotNull
        var x: Int,

        @get:NotNull
        var y: Int,

        @get:Id
        @get:GeneratedValue
        var id: Long? = null


)