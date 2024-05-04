package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month, SortOrder.ASC)
                .orderBy(BudgetTable.amount, SortOrder.DESC)

            val total = query.count()

            val allData = BudgetEntity.wrapRows(query).map { it.toResponse() }.map { mapToBudgetRecordDto(it) }
            if(param.filter != null) {
                allData.filter { e -> (e.fullNameAuthor != null) and (e.fullNameAuthor!!.toLowerCase().contains(param.filter!!.toLowerCase())) }
            }

            val sumByType = allData.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            query.limit(n = param.limit, offset = param.offset)
            val limitData = BudgetEntity.wrapRows(query).map { it.toResponse() }.map { mapToBudgetRecordDto(it) }
            if(param.filter != null) {
                limitData.filter { e -> (e.fullNameAuthor != null) and (e.fullNameAuthor!!.toLowerCase().contains(param.filter!!.toLowerCase())) }
            }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = limitData
            )
        }
    }

    private fun mapToBudgetRecordDto(e: BudgetRecord): BudgetRecordDto{
        if(e.authorId != null){
            val query = AuthorTable.select{AuthorTable.id eq e.authorId}
            val author = AuthorEntity.wrapRows(query).map { it.toResponse() }.first()
            val fullNameAuthor = author.fullName
            val dateOfCreate = author.dataOfCreate
            return BudgetRecordDto(e.year, e.month, e.amount, e.type, fullNameAuthor, dateOfCreate)
        }
        return BudgetRecordDto(e.year, e.month, e.amount, e.type)
    }
}