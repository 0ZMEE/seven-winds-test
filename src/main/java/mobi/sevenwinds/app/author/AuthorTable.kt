package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.model.schema.DataFormat
import mobi.sevenwinds.app.budget.BudgetEntity
import mobi.sevenwinds.app.budget.BudgetRecord
import mobi.sevenwinds.app.budget.BudgetType
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.stringLiteral

object AuthorTable : IntIdTable("budget") {
    val fullName = varchar("fullName", length = 70)
    val dateOfCreate = datetime("dateOfCreat")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fullName by AuthorTable.fullName
    var dateOfCreate by AuthorTable.dateOfCreate

    fun toResponse(): Author {
        return Author(fullName, dateOfCreate)
    }
}