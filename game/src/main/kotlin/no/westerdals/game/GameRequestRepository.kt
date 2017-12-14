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

    fun createRequest(player1Id: Long): Long

    fun acceptRequest(player1Id: Long, player2Id: Long): Long?

}
@Repository
@Transactional
open class GameRequestRepositoryImpl : GameRequestRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager


    override fun createRequest( player1Id: Long): Long {
        val entity = GameRequestEntity(player1Id);
        em.persist(entity)
        return entity.id!!
    }

    override fun acceptRequest(requestId: Long ,player2Id: Long): Long? {

    val found= (requestId)
        val GRE = em.find(GameRequestEntity::class.java, requestId)
        ?: return null

        if (GRE.player2Id == null) {
            GRE.player2Id = player2Id
            em.persist(GRE)

            return GRE.id!!
        } else {
            throw error("DIDNT WORK")
        }


    }
}