package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany


@Entity
class GameEntity(
        @get:NotNull
        var player1Id: Long,

        @get:NotNull
        var player2Id: Long,

        @get:Id
        @get:GeneratedValue
        var gameId: Long? = null,

        //  0 is still going, 1 is player1 won,
        // 2 is player two won and 3 is draw.
        //
        @get:NotNull
        var gameDone: Int = 0,

        //TODO: mappedby
        @OneToMany(mappedBy = "gameEntity")
        var gameMoves: ArrayList<MovesEntity>? = null
)