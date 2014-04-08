package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class DocumentSpec extends BaseSpec {

  "A Document" - {

    "can be constructed" - {
      "by using an Optional DocumentId, a Sentence as title, and a String as text" in {
        val docOne = Document(None, Sentence.Empty, "text")
        docOne should have (
          'id    (None),
          'title (Sentence.Empty),
          'text  ("text")
        )

        val docTwo = Document(Some(1), "my title", "my text document body")
        docTwo should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("my title")),
          'text  ("my text document body")
        )
      }
    }

    "should throw an IllegalArgumentException" - {
      "when constructed when an empty text" in {
        a [IllegalArgumentException] should be thrownBy {
          val invalid : Document = Document(None, Sentence.Empty, "")
        }
      }
    }

  }

}
