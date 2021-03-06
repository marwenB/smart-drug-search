package es.uvigo.ei.sing.sds.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.service.LinnaeusService

private[searcher] class LinnaeusSearcher extends SearcherAdapter {

  import database._
  import database.profile.simple._

  lazy val linnaeus = LinnaeusService()

  override def search(searchTerms : Sentence)(implicit ec : ExecutionContext) =
    linnaeus obtainMentions searchTerms map (_ map linnaeus.normalize) map {
      normalizedTerms => searchNormalized(normalizedTerms.toSet)
    }

  private[this] def searchNormalized(normalizedTerms : Set[Sentence]) =
    database withSession { implicit session =>
      normalizedTerms flatMap {
        normalized => (Keywords findByNormalized normalized).firstOption
      }
    }

}

private[searcher] object LinnaeusSearcher extends (() => LinnaeusSearcher) {

  def apply( ) : LinnaeusSearcher =
    new LinnaeusSearcher()

}

