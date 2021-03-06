package es.uvigo.ei.sing.sds.controller

import scala.concurrent.Future
import scala.util.Try

import play.api.mvc._
import play.api.libs.json.{ Json, JsValue }

import es.uvigo.ei.sing.sds.annotator.Annotate
import es.uvigo.ei.sing.sds.entity.DocumentId
import es.uvigo.ei.sing.sds.Global.annotator

private[controller] trait AnnotatorController extends Controller with Authorization {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  def annotate( ) : Action[JsValue] =
    AuthorizedAction(parse.json) { _ => request =>
      (request.body \ "ids").validate[Set[DocumentId]] fold (
        errors => BadRequest(Json obj ("err" -> errors.toString)),
        annotateIds
      )
    }

  private[this] def annotateIds(ids : Set[DocumentId]) =
    (annotateResult _) tupled (ids map { id => Try(Annotate(id)) } partition (_.isSuccess))

  private[this] def annotateResult(successful : Set[Try[Annotate]], failed : Set[Try[Annotate]]) =
    if (failed.isEmpty) {
      Future(successful foreach { t => annotator ! t.get })
      Accepted
    } else NotFound(Json obj ("err" -> "Document not found"))

}

object AnnotatorController extends AnnotatorController

