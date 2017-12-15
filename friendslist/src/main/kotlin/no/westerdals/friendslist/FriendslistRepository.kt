package no.westerdals.friendslist

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
interface FriendslistEntityRepository : CrudRepository<FriendslistEntity, Long>, FriendslistRepositoryCustom {
    fun findOneById(requestId: Long) : FriendslistEntity?
}

@Transactional
interface FriendslistRepositoryCustom {

    fun createFriend(friendEntity: FriendslistEntity): FriendslistEntity

    fun deleteFriend(id: Int): Int?

    fun existsFriend(id: Int?): Boolean

}
@Repository
@Transactional
class FriendslistEntityRepositoryImpl : FriendslistRepositoryCustom {


    @PersistenceContext
    private lateinit var em: EntityManager

    override fun existsFriend(friendId: Int?): Boolean {
        val exists = em.find(FriendslistEntity::class.java, friendId)

        if(exists != null) {
            return true
        }
        return false
    }


    override fun deleteFriend(friendId: Int): Int? {

        val entity = em.find(FriendslistEntity::class.java, friendId)
                ?: return null

        em.remove(entity)

        return entity.friendId!!


    }

    override fun createFriend(friendEntity: FriendslistEntity): FriendslistEntity {
        em.persist(friendEntity)
        return friendEntity!!
    }

}