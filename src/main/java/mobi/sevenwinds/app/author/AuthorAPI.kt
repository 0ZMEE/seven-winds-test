package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import mobi.sevenwinds.app.budget.*
import org.joda.time.DateTime


fun NormalOpenAPIRoute.budget() {
    route("/author") {
        route("/add").post<Unit, Author, AuthorDto>(info("Добавить запись")) { param, body ->
            respond(AuthorService.addAuthor(body))
        }
    }
}

data class AuthorDto(
    val fullName: String
)
data class Author(
        val fullName: String,
        val dataOfCreate: DateTime
)