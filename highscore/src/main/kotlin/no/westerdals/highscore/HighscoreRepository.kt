package no.westerdals.highscore


import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
interface HighscoreEntityRepository : CrudRepository<HighscoreEntity, Long>, HighscoreRepositoryCustom {
    fun findOneById(requestId: Long) : HighscoreEntity?

}

@Transactional
interface HighscoreRepositoryCustom {

    fun createHighscore(score1: Int, score2: Int, user1: String, user2: String): Long

    fun replaceHighscore(highScoreId: Long, score1: Int, score2: Int, user1: String, user2: String): Long?

    fun deleteHighscore(highScoreId: Long): Long?

    fun existsHighscore(highScoreId: Long): Boolean

}
@Repository
@Transactional
open class HighscoreEntityRepositoryImpl : HighscoreRepositoryCustom {


    @PersistenceContext
    private lateinit var em: EntityManager

    override fun existsHighscore(highScoreId: Long): Boolean {
        val exists = em.find(HighscoreEntity::class.java, highScoreId)

        if(exists != null) {
            return true
        }
        return false
    }

    override fun replaceHighscore(highScoreId: Long, score1: Int, score2: Int, user1: String, user2: String): Long? {

        val entity = em.find(HighscoreEntity::class.java, highScoreId)
                ?: return null

        entity.score1 = score1
        entity.score2 = score2
        entity.user1 = user1
        entity.user2 = user2
        em.persist(entity)

        return entity.id!!

    }

    override fun deleteHighscore(highScoreId: Long): Long? {

        val entity = em.find(HighscoreEntity::class.java, highScoreId)
                ?: return null

        em.remove(entity)

        return entity.id!!


    }

    override fun createHighscore(score1: Int, score2: Int, user1: String, user2: String): Long {
        val entity = HighscoreEntity(null, score1, score2, user1, user2)
        em.persist(entity)
        return entity.id!!
    }

}