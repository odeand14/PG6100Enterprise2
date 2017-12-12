package no.westerdals.highscore


// Created by Andreas Ødegaard on 10.12.2017.
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class HighscoreEntity(

        @get:Id
        @get:NotBlank
        var scoreId: String?,

        @get:NotBlank
        var score: String?,

        @get:NotBlank
        var winner: String?,

        @get:NotBlank
        var looser: String?
)