package es.uvigo.ei.sing.sds.database

import scala.slick.jdbc.meta.MTable
import scala.slick.model.ForeignKeyAction._

import play.api.db.slick.Profile

import es.uvigo.ei.sing.sds.entity._

private[database] trait Tables { this : Profile with Mappers =>

  import profile.simple._

  protected final class AccountsTable (val tag : Tag) extends Table[Account](tag, "accounts") {

    def id       = column[AccountId]("account_id", O.PrimaryKey, O.AutoInc)
    def email    = column[String]("email", O.NotNull)
    def password = column[String]("password", O.NotNull)

    def unique_email = index("idx_accounts_unique_email", email, unique = true)

    def * = (id.?, email, password) <> (Account.tupled, Account.unapply)

  }

  protected final class DocumentsTable (val tag : Tag) extends Table[Document](tag, "documents") {

    def id        = column[DocumentId]("document_id", O.PrimaryKey, O.AutoInc)
    def title     = column[Sentence]("title", O.NotNull)
    def text      = column[String]("text", O.NotNull, O.DBType("TEXT"))
    def annotated = column[Boolean]("annotated", O.NotNull)
    def pubmedId  = column[PubMedId]("pubmed_id", O.Nullable)
    def blocked   = column[Boolean]("blocked", O.NotNull, O.Default(false))

    def * = (id.?, title, text, pubmedId.?, annotated, blocked) <> (Document.tupled, Document.unapply)

  }

  protected final class KeywordsTable (val tag : Tag) extends Table[Keyword](tag, "keywords") {

    def id          = column[KeywordId]("keyword_id", O.PrimaryKey, O.AutoInc)
    def normalized  = column[Sentence]("normalized_text", O.NotNull, O.DBType("VARCHAR(1023)"))
    def category    = column[Category]("category", O.NotNull)
    def occurrences = column[Size]("counter", O.Default(0L))

    def * = (id.?, normalized, category, occurrences) <> (Keyword.tupled, Keyword.unapply)

  }

  protected final class AnnotationsTable (val tag : Tag) extends Table[Annotation](tag, "annotations") {

    def id            = column[AnnotationId]("annotation_id", O.PrimaryKey, O.AutoInc)
    def documentId    = column[DocumentId]("document_id", O.NotNull)
    def keywordId     = column[KeywordId]("keyword_id", O.NotNull)
    def text          = column[Sentence]("original_text", O.NotNull, O.DBType("VARCHAR(1023)"))
    def startPosition = column[Position]("start", O.NotNull)
    def endPosition   = column[Position]("end", O.NotNull)

    def document = foreignKey("fk_annotation_document", documentId, TableQuery[DocumentsTable])(_.id, Cascade, Cascade)
    def keyword  = foreignKey("fk_annotation_keyword",  keywordId,  TableQuery[KeywordsTable] )(_.id, Cascade, Cascade)

    def * = (id.?, documentId, keywordId, text, startPosition, endPosition) <> (Annotation.tupled, Annotation.unapply)

  }

  protected final class DocumentStatsTable (val tag : Tag) extends Table[(DocumentId, KeywordId, Size, Double)](tag, "document_stats") {

    def documentId = column[DocumentId]("document_id")
    def keywordId  = column[KeywordId]("keyword_id")
    def counter    = column[Size]("count", O.NotNull)
    def ratio      = column[Double]("ratio", O.NotNull)

    def key = primaryKey("pk_stats", (documentId, keywordId))

    def document = foreignKey("fk_stats_document", documentId, TableQuery[DocumentsTable])(_.id, Cascade, Cascade)
    def keyword  = foreignKey("fk_stats_keyword",  keywordId,  TableQuery[KeywordsTable] )(_.id, Cascade, Cascade)

    def * = (documentId, keywordId, counter, ratio)

  }

}

