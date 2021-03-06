package es.uvigo.ei.sing.sds.controller

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity._

private[controller] trait KeywordsController extends Controller {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  def get(id : KeywordId) : Action[AnyContent] =
    Action {
      withKeyword(id) { keyword => Ok(Json toJson keyword) }
    }

  private[this] def withKeyword(id : KeywordId)(f : Keyword => Result) =
    database withSession { implicit session =>
      (Keywords findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Keyword not found"))
    }

}

object KeywordsController extends KeywordsController

