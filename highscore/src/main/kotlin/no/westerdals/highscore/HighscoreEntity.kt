package no.westerdals.highscore


// Created by Andreas Ødegaard on 10.12.2017.
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class HighscoreEntity(

        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:NotBlank
        var score1: Int?,

        @get:NotBlank
        var score2: Int?,

        @get:NotBlank
        var user1: String?,

        @get:NotBlank
        var user2: String?
)