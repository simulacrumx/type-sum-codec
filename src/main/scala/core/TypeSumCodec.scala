package core

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import io.circe.generic.JsonCodec
import io.circe.Encoder
import io.circe.syntax._
import io.circe.Json

class TypeSumCodec(typeHint: String = "type") extends scala.annotation.StaticAnnotation  {
  def macroTransform(annottees: Any*): Any = macro TypeSumCodecMacros.innerMacros
}

private[core] final class TypeSumCodecMacros(val c: blackbox.Context) {
    import c.universe._

    def innerMacros(annottees: Tree*): Tree = { 
      annottees match {
        case List(
          clsDef: ClassDef,
          comp @ q"..$mods object $companionName extends { ..$directParent } with ..$objParents { $self => ..$definitions }"
        ) if isTypeSum(clsDef) =>

          val typeTagStr = codecTypeHintName

          val filtered = filterChildrenCaseClasses(definitions, clsDef)

          val encoderCaseDefs = filtered.map {
            case typeName => 
              val strName = typeName.toString()

              cq"""t: $typeName => {
                Json.fromJsonObject(t.asJson.asObject.get.add($typeTagStr, io.circe.Json.fromString($strName))) 
              }"""
          }

          val encoderType = tq"Encoder[${clsDef.name.toTypeName}]"

          val encoderBody = 
              q"""
              implicit val _encoder: $encoderType = new Encoder[${clsDef.name.toTypeName}] {
                def apply(a: ${clsDef.name.toTypeName}): io.circe.Json = 
                  a match { case ..${encoderCaseDefs} }
              }
              """

          val decoderType = tq"Decoder[${clsDef.name.toTypeName}]"    

          val decoderCaseDefs =  filtered.map {
            case typeName => 
              cq""" `${typeName.toTermName.toString()}` => c.as[$typeName]"""
          }

          val decoderBody = 
            q"""
              implicit val _decoder: $decoderType = new Decoder[${clsDef.name.toTypeName}] {
                def apply(c: HCursor): Decoder.Result[${clsDef.name.toTypeName}] = {
                  c.get[String]($typeTagStr).flatMap { 
                    a => a match { case  ..${decoderCaseDefs} }
                  }
                }
              }"""

          val imports: List[Tree] = List(
            q"import io.circe.Json",
            q"import io.circe.Decoder",
            q"import io.circe.Encoder",
            q"import io.circe.HCursor"
          )

          q"""
            $clsDef
            $mods object $companionName extends { ..$directParent } with ..$objParents { 
              $self => ..${imports ::: (decoderBody :: encoderBody :: definitions)} 
            }
          """

        case n => q""" ..$n """ 
      }
    }
   
    private def filterChildrenCaseClasses(definitions: List[Tree], target: ClassDef): List[TypeName] = {
      definitions.collect {
        case n @ q"""
          ..$mods class $className(..$params) extends { ..$earlyDefs } with ..$parents { $self => ..$definitions }
        """ if hasParent(parents, target) && isCaseClass(mods) => 
          className
      }
    }


    private def hasParent(parents: List[Tree], parent:ClassDef): Boolean = {
      parents.exists {
        case Ident(name) if name == parent.name => true
        case x => false
      }
    }

    private def isCaseClass(mods: Modifiers): Boolean = {
      mods.hasFlag(Flag.CASE)
    }

    private def isTypeSum(clsDef: ClassDef): Boolean = {
      clsDef.mods.hasFlag(Flag.SEALED)
    }

    private[this] val macroName: Tree = {
      c.prefix.tree match {
        case Apply(Select(New(name), _), _) => name
        case _                              => c.abort(c.enclosingPosition, "Unexpected macro application")
      }
    }

    private[this] val codecTypeHintName: String = {
      c.prefix.tree match {
        case q"new ${`macroName`}()" => "type"
        case q"new ${`macroName`}(typeHint = $v)"                  => v match {
          case Literal(Constant(s: String)) => s
          case _ => c.abort(c.enclosingPosition, s"Unsupported arguments supplied to @$macroName")
        }
        case _ => c.abort(c.enclosingPosition, s"Unsupported arguments supplied to @$macroName")
      }
    }
}