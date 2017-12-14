package no.westerdals.friendslist

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class FriendslistEntity(

    @get:Id
    var id: Long?,

    @NotBlank
    var userId: Int?,

    @NotBlank
    var friendId: Int?,

    @NotBlank
    var date: String?
)