package es.uvigo.ei.sing.sds.service

import scala.collection.JavaConversions._
import scala.concurrent.{ ExecutionContext, Future }

import uk.ac.cam.ch.wwmm.oscar.Oscar
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.{ FormatType, ResolvedNamedEntity }

import es.uvigo.ei.sing.sds.entity.Sentence

class OscarService private {

  import OscarService.oscar

  def getNamedEntities(text : String)(implicit ec : ExecutionContext) : Future[Set[ResolvedNamedEntity]] =
    Future(oscar findResolvableEntities text) map { _.toSet }

  def normalize(entity : ResolvedNamedEntity) : Sentence =
    entity.getFirstChemicalStructure(FormatType.STD_INCHI).getValue

}

object OscarService extends (() => OscarService) {

  lazy val oscar = new Oscar()

  def apply( ) : OscarService =
    new OscarService

}

