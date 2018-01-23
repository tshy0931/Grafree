package tshy0931.github.grafree.models

import tshy0931.github.grafree.TypeClass.Show._
import cats.syntax.show._
import org.scalatest.{FlatSpec, Matchers}
import tshy0931.github.grafree.adt.{Alias, Argument, Field, query}
import tshy0931.github.grafree.codec.GrafreeCodec

class GrafreeCodecSpec extends FlatSpec with Matchers with GrafreeCodecFixture {

  behavior of "GraphQL codec"

  it should "encode adt object into GraphQL query string correctly" in {

    val pageInfo = PageInfo()
    val node = Node()
    val edges = Edges(Field(node))

    val followers = Followers(
      first    = Argument(3),
      after    = Argument("someIndex"),
      pageInfo = Field(pageInfo),
      edges    = Field(edges)
    )
    val user = User(
      login     = Argument("someUser"),
      followers = Field(followers)
    )

    val userQuery = UserQuery(Field(user))

    val nodeResult = shapeless.the[GrafreeCodec[Node]].encode(node)("node")
    val edgesResult = shapeless.the[GrafreeCodec[Edges]].encode(edges)("edges")
    val pageInfoResult = shapeless.the[GrafreeCodec[PageInfo]].encode(pageInfo)("pageInfo")
    val followersResult = shapeless.the[GrafreeCodec[Followers]].encode(followers)("followers")
    val userResult = shapeless.the[GrafreeCodec[UserQuery]].encode(userQuery)(query("QueryName"))

    //TODO - 1. add test for mutation; 2. add support for variables

    nodeResult.name shouldBe "node"
    nodeResult.alias shouldBe None
    nodeResult.fragments shouldBe List()
    nodeResult.directives shouldBe List()
    nodeResult.arguments shouldBe List()
    nodeResult.fields shouldBe List("login", "id")

    edgesResult.name shouldBe "edges"
    edgesResult.alias shouldBe None
    edgesResult.fragments shouldBe List()
    edgesResult.directives shouldBe List()
    edgesResult.arguments shouldBe List()
    edgesResult.fields shouldBe List("cursor", nodeResult.show)

    followersResult.name shouldBe "followers"
    followersResult.alias shouldBe None
    followersResult.fragments shouldBe List()
    followersResult.directives shouldBe List()
    followersResult.arguments shouldBe List("after: \"someIndex\"", "first: 3")
    followersResult.fields shouldBe List(edgesResult.show, pageInfoResult.show, "totalCount")

    nodeResult.show shouldBe "node{login id}"
    edgesResult.show shouldBe "edges{cursor node{login id}}"
    pageInfoResult.show shouldBe "pageInfo{hasNextPage endCursor}"
    followersResult.show shouldBe "followers(after: \"someIndex\", first: 3){edges{cursor node{login id}} pageInfo{hasNextPage endCursor} totalCount}"
    userResult.show shouldBe "query QueryName{user(login: \"someUser\"){followers(after: \"someIndex\", first: 3){edges{cursor node{login id}} pageInfo{hasNextPage endCursor} totalCount} company}}"
  }
}

trait GrafreeCodecFixture {

  case class Query(user: User)

  /**
    * user(login: $login) {
    *   company
    *   followers(first: $first, after: $after) {
    *     totalCount
    *     pageInfo {
            endCursor
            hasNextPage
          }
          edges {
            node {
              id
              login
            }
            cursor
          }
    *   }
    * }
    * @param login
    * @param company
  //    * @param followers
    */
  case class User(login: Argument[String], alias: Alias = Alias(), company: Field[Unit] = Field(), followers: Field[Followers])
  case class PageInfo(endCursor: Field[Unit] = Field(), hasNextPage: Field[Unit] = Field())
  case class Node(id: Field[Unit] = Field(), login: Field[Unit] = Field())
  case class Edges(node: Field[Node], cursor: Field[Unit] = Field())
  case class Followers(first: Argument[Int], after: Argument[String], totalCount: Field[Unit] = Field(), pageInfo: Field[PageInfo], edges: Field[Edges])

  case class UserQuery(user: Field[User])
}
