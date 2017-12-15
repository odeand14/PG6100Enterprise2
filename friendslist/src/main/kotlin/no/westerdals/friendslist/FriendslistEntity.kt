package no.westerdals.friendslist

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class FriendslistEntity(

    @get:Id
    @get:GeneratedValue
    var id: String,

    var userId: Int?,

    var friendId: Int?,

    @NotBlank
    var date: String?
)