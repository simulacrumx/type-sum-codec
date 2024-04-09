package core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import io.circe.generic.JsonCodec
 import io.circe.syntax._

class TypeSumCodecTest extends AnyFlatSpec with Matchers {
  
    @TypeSumCodec
    sealed trait TestSum

    object TestSum {
        @JsonCodec
        case class A(a: Int, b: Int) extends TestSum

        @JsonCodec
        case class B(a: Int, b: Int) extends TestSum

        @JsonCodec
        case class C(a: Int, b: Int) extends TestSum
    }


    it must "decode and encode single value of type sum" in {
        val a: TestSum = TestSum.A(1, 2)
        val result = a.asJson.as[TestSum]
        result mustBe Right(a)
    }

    it must "decode and encode list of different type sum values with same json structure" in {
        val a: List[TestSum] = List(TestSum.C(1, 2), TestSum.B(3, 4), TestSum.A(5, 6))
        val result =  a.asJson.as[List[TestSum]]
        result mustBe Right(a)
    }
}
