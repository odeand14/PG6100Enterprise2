package no.westerdals.highscore


// Created by Andreas Ødegaard on 10.12.2017.
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class HighscoreEntity(

        @get:Id
        @get:GeneratedValue
        @ApiModelProperty("The id of the highscore")
        var id: Long? = null,

        @ApiModelProperty("The score of the first player")
        var score1: Int?,

        @ApiModelProperty("The score of the second player")
        var score2: Int?,

        @ApiModelProperty("The name of the first player")
        @get:NotBlank
        var user1: String?,

        @ApiModelProperty("The name of the second player")
        @get:NotBlank
        var user2: String?
)