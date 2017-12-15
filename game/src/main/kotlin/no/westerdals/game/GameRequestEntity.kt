package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
class GameRequestEntity(
        @get:NotBlank
        var player1username: String,

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,


        var player2username: String? = null,

        var gameId: Long? = null
)