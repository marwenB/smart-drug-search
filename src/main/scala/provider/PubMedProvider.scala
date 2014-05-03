package es.uvigo.esei.tfg.smartdrugsearch.provider

import scala.concurrent.{ Future, future }

import es.uvigo.esei.tfg.smartdrugsearch.database.dao.DocumentsDAO
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.EUtils._

case class PubMedSearchResult(totalResults : Long, firstElement : Position, idList : Set[PubMedId])

class PubMedProvider private {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile.database

  private type FetchedDocument = (PubMedId, Sentence, String)

  private lazy val documents = DocumentsDAO()

  def search(
    searchTerms : Sentence, limitDays : Option[Int] = None, startingOn : Position = 0, countPerPage : Int = 100
  ) : Future[PubMedSearchResult] =
    future { findPubMedIDs(searchTerms, limitDays, startingOn.toInt, countPerPage) } map {
      case (totalResults, idList) => PubMedSearchResult(totalResults, startingOn, idList map PubMedId)
    }

  def download(ids : Seq[PubMedId]) : Future[Seq[DocumentId]] =
    future { fetchPubMedArticles(ids map (_.value)) } map {
      _ map { case (id, title, text) => saveDocument(id, title, text) }
    }

  private def saveDocument(pmid : PubMedId, title : Sentence, text : String) =
    database withTransaction { implicit session =>
      documents findByPubMedId pmid match {
        case Some(doc) => doc.id.get
        case None      => documents save Document(None, title, text, false, Some(pmid))
      }
    }

}

object PubMedProvider extends (() => PubMedProvider) {

  def apply( ) : PubMedProvider =
    new PubMedProvider()

}
