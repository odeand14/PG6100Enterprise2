package no.westerdals.friendslist

import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class FriendslistEntity(

    @ApiModelProperty("Id of the entry")
    @get:Id
    @get:GeneratedValue
    var id: String,

    @ApiModelProperty("Id of the user")
    var userId: Int?,

    @ApiModelProperty("Id of the friend")
    var friendId: Int?,

    @ApiModelProperty("When the entry was created")
    @NotBlank
    var date: String?
)