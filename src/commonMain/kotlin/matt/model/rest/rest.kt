package matt.model.rest

import matt.lang.anno.SeeURL
import matt.lang.idea.Idea
import matt.model.data.hash.md5.MD5
import matt.model.op.convert.StringConverter


interface EndpointIdea : Idea

interface OneEndpoint : EndpointIdea

interface StaticEndpoint : EndpointIdea {
    val path: String
}

sealed interface BaseRestResource<R : Any, A, T, Q : Query> : EndpointIdea {
    fun concretePath(
        arg: A,
        query: Q
    ): String

    fun abstractPath(token: T): String
}

val BaseRestResource<*, *, *, *>.canHaveQuery
    get() = when (this) {
        is RestResource                     -> false
        is RestResourceWithArgument         -> false
        is RestResourceWithQuery            -> true
        is RestResourceWithArgumentAndQuery -> true
    }

@SeeURL("https://ktor.io/docs/type-safe-routing.html")
interface RestResource<R : Any> : BaseRestResource<R, NoArg, NoToken, NoQuery> {
    val path: String
    override fun concretePath(
        arg: NoArg,
        query: NoQuery
    ) = path

    override fun abstractPath(token: NoToken) = path
    fun arg(): NoArg = NoArg
}

fun <R : Any> RestResource<R>.concretePath() = concretePath(NoArg, NoQuery)
fun <R : Any> RestResource<R>.abstractPath() = abstractPath(NoToken)


interface Query {
    fun toMap(): Map<String, String>
    fun urlStringRep(): String
}

object NoArg
object NoToken
object NoQuery : Query {
    override fun toMap() = emptyMap<String, String>()
    override fun urlStringRep() = ""
}


interface RestResourceWithArgument<R : Any, A> : BaseRestResource<R, A, String, NoQuery> {
    val basePath: String
    val converter: StringConverter<A>

    override fun concretePath(
        arg: A,
        query: NoQuery
    ) = basePath + "/" + converter.toString(arg)

    override fun abstractPath(token: String) = "$basePath/{$token}"
    fun abstractPostPath() = basePath
    fun concretePostPath() = basePath
}


fun <R : Any, A> RestResourceWithArgument<R, A>.concretePath(arg: A) = concretePath(arg, NoQuery)


@SeeURL("https://ktor.io/docs/routing-in-ktor.html#multiple_routes")
interface RestResourceWithMultiPartArgument<R : Any, A> : RestResourceWithArgument<R, A> {
    override fun abstractPath(token: String) = "$basePath/{$token...}"
}


interface RestResourceWithLocation<R : Any, L : Any> : RestResourceWithArgument<R, L>


interface RestResourceWithQuery<R : Any, Q : Query> : BaseRestResource<R, NoArg, NoToken, Q> {


    val basePath: String


    override fun concretePath(
        arg: NoArg,
        query: Q
    ) = basePath + query.urlStringRep()

    override fun abstractPath(token: NoToken) = basePath

}


interface RestResourceWithArgumentAndQuery<R : Any, A, Q : Query> : BaseRestResource<R, A, String, Q> {
    val basePath: String
    val converter: StringConverter<A>

    override fun concretePath(
        arg: A,
        query: Q
    ) = basePath + "/" + converter.toString(arg) + query.urlStringRep()

    override fun abstractPath(token: String) = "$basePath/{$token}"
}


@Suppress("UnusedReceiverParameter")
inline fun <reified R : Any> BaseRestResource<R, *, *, *>.typeInfo() = io.ktor.util.reflect.typeInfo<R>()


interface SecureImageInter
interface HtmlResponse
interface CssResponse
interface Download
interface Upload
class UploadInfo(
    val md5: MD5?,
    val contentLength: Long
)


interface FormSubmission : Query {
    fun didNotAnswer(): String?
}