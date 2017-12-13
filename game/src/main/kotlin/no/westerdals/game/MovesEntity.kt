package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import javax.persistence.Id

class MovesEntity(

        @get:Id
        @get:NotNull
        var gameId: Long,

        @get:NotNull
        var x: Int,

        @get:NotNull
        var y: Int,

        @get:NotNull
        var playerId: Long
)