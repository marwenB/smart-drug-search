package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class AnnotationId (value : Long) extends AnyVal with Identifier
object AnnotationId extends IdentifierCompanion[AnnotationId]

final case class Annotation (
  id            : Option[AnnotationId] = None,
  documentId    : DocumentId,
  keywordId     : KeywordId,
  text          : Sentence,
  startPosition : Position,
  endPosition   : Position
) {

  require(startPosition < endPosition, "Start Position must be inferior than End Position")

}

object Annotation extends ((Option[AnnotationId], DocumentId, KeywordId, Sentence, Position, Position) => Annotation) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val annotationFormat = Json.format[Annotation]

}
