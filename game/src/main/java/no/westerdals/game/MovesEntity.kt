package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Id

class MovesEntity(

        @get:Id
        @get:NotBlank
        var gameId: String?,

        @get:NotBlank
        var x: Int,

        @get:NotBlank
        var y: Int
)