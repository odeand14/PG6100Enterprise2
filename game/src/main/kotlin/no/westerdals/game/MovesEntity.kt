package no.westerdals.game

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
class MovesEntity(


        @get:ManyToOne
        var gameentity: GameEntity? = null,

        @get:NotNull
        var playerusername: String,

        @get:NotNull
        var x: Int,

        @get:NotNull
        var y: Int,

        @get:Id
        @get:GeneratedValue
        var id: Long? = null




)