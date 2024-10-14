## Type sum codec

While using databases storing json documents (MongoDB, ElasticSearch) someone could face a problem of decoding/encoding type sums e.g building codecs for navigation tree elements, which have varied types and structure. One should build boilerplate codecs like this one: 

```scala
sealed trait Node

object Node {
    @JsonCodec
    case class Folder(val id: String, name: String, folderSpecialField: Option[String]) extends Node

    @JsonCodec
    case class ListNode(val id: String, name: String, listSpecialField: Option[Int]) extends Node

    implicit val encoder: Encoder[Node] = new Encoder[Node] {
      override def apply(a: Node): Json = {
            val body = a match {
                case f: Folder => f.asJson
                case ln: ListNode => ln.asJson
            }
            
            val hint = Json.obj("hint" -> Json.fromString(a.getClass.getSimpleName))
            body.deepMerge(hint)
        }
    }

    implicit val decoder: Decoder[Node] = new Decoder[Node] {
      override def apply(c: HCursor): Decoder.Result[Node] = {
        c.downField("hint").as[String].flatMap {
          case "ListNode" => c.as[Node.ListNode]
          case "Folder" => c.as[Node.Folder]
        }
      }
    }
}
```

This minimal library allows automatic building of boilerplate codecs like this, you just need to add annotation to sealed trait:

```scala
import io.github.simulacrumx.tscodec._

@TypeSumCodec
sealed trait Node

object Node {
    @JsonCodec
    case class Folder(val id: String, name: String, folderSpecialField: Option[String]) extends Node

    @JsonCodec
    case class ListNode(val id: String, name: String, listSpecialField: Option[Int]) extends Node
}

```

You also can configure the key of type hints, that will be stored inside documents:

```scala
import io.github.simulacrumx.tscodec._

@TypeSumCodec(typeHint = "abcdef")
sealed trait Node

object Node {
    @JsonCodec
    case class Folder(val id: String, name: String, folderSpecialField: Option[String]) extends Node

    @JsonCodec
    case class ListNode(val id: String, name: String, listSpecialField: Option[Int]) extends Node
}
```

#### Important

Inheritants of sealed trait should be inside companion object of the trait, otherwise macro would not be working properly

#### Installation:

```scala
libraryDependencies ++= Seq(
    "io.github.simulacrumx" %% "type-sum-codec" % "0.1.1"
)
```

