package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
interface GameRequestRepository : CrudRepository<GameRequestEntity, Long>, GameRequestRepositoryCustom {
    fun findOneById(requestId: Long) : GameRequestEntity?

}

@Transactional
interface GameRequestRepositoryCustom {

    fun createRequest(player1username: String): Long

    fun acceptRequest(requestId: Long, player2username: String): Long?

}
@Repository
@Transactional
open class GameRequestRepositoryImpl : GameRequestRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager


    override fun createRequest(player1username: String): Long {
        val entity = GameRequestEntity(player1username);
        em.persist(entity)
        return entity.id!!
    }

    override fun acceptRequest(requestId: Long, player2username: String): Long? {

    val found= (requestId)
        val GRE = em.find(GameRequestEntity::class.java, requestId)
        ?: return null

        if (GRE.player2username == null) {
            GRE.player2username = player2username
            em.persist(GRE)

            return GRE.id!!
        } else {
            throw error("DIDNT WORK")
        }


    }
}