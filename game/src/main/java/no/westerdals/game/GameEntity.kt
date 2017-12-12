package no.westerdals.game

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class GameEntity(


        @get:Id
        @get:NotBlank
        var gameId: String?,

        @get:NotBlank
        var player1Id: String?,

        @get:NotBlank
        var player2Id: String?,


        //TODO: mappedby
        @OneToMany(mappedBy = "GameEntity")
        var gameMoves: ArrayList<MovesEntity>
)