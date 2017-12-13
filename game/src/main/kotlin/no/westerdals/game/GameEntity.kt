package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class GameEntity(


        @get:Id
        @get:NotNull
        @get:GeneratedValue
        var gameId: Long? = null,

        @get:NotNull
        var player1Id: Long,

        @get:NotNull
        var player2Id: Long,


        //TODO: mappedby
        @OneToMany(mappedBy = "GameEntity")
        var gameMoves: ArrayList<MovesEntity>? = null
)