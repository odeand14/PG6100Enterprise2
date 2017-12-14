package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import javax.persistence.*


@Entity
class GameEntity(
        @get:NotBlank
        var player1username: String,

        @get:NotBlank
        var player2username: String,

        @get:Id
        @get:GeneratedValue
        var gameId: Long? = null,

        //  0 is still going, 1 is player1 won,
        // 2 is player two won and 3 is draw.
        //
        @get:NotNull
        var gameDone: Int = 0,

        @get:OneToMany(
                fetch = FetchType.LAZY,
                mappedBy = "gameentity",
                cascade = arrayOf(CascadeType.ALL)
        )
        var gameMoves: MutableList<MovesEntity> = mutableListOf()
)