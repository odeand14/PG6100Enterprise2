package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class GameEntity(


        @get:Id
        @get:NotBlank
        var gameId: String?,

        @get:NotBlank
        var player1Id: String?,
        @get:NotBlank

        var player2Id: String?
)