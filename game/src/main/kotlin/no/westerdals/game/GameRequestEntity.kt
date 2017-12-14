package no.westerdals.game

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany


@Entity
class GameRequestEntity(
        @get:NotNull
        var player1Id: Long,

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,


        var player2Id: Long? = null,

        var gameId: Long? = null
)