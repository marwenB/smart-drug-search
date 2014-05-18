package es.uvigo.esei.tfg.smartdrugsearch.annotator

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.service.OscarService

private[annotator] class OscarAnnotator extends AnnotatorAdapter {

  import context._

  lazy val oscar = OscarService()

  override protected def annotate(document : Document) =
    oscar getNamedEntities document.text map {
      entities => entities foreach (saveEntity(_, document.id.get))
    }

  private[this] def saveEntity(entity : ResolvedNamedEntity, documentId : DocumentId) = {
    val normalized = oscar normalize entity
    val keywordId  = getOrStoreKeyword(normalized, Compound)
    val annotation = Annotation(None, documentId, keywordId, entity.getSurface, entity.getStart, entity.getEnd)
    storeAnnotation(annotation)
  }

}

