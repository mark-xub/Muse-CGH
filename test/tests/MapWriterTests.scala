package tests

import ui.font_editor.Editing
import main.{LetterSeg, MuseChar, MuseCharType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Prop._
import utilities.{CubicCurve, Vec2}

/**
  * Created by weijiayi on 3/3/16.
  */
object MapWriterTests {

  import utilities.MapWriter._

  val vec2Gen = for{
    x <- arbitrary[Double]
    y <- arbitrary[Double]
  } yield Vec2(x,y)

  val cubicGen = for{
    p0 <- vec2Gen
    p1 <- vec2Gen
    p2 <- vec2Gen
    p3 <- vec2Gen
  } yield CubicCurve(p0,p1,p2,p3)

  val letterSegGen = for{
    c <- cubicGen
    dots <- Gen.choose(1, 100)
    start <- Gen.choose(0.01, 1.0)
    end <- Gen.choose(0.01, 1.0)
    b1 <- arbitrary[Boolean]
    b2 <- arbitrary[Boolean]
  } yield LetterSeg(c, start, end, b1, b2)

  val letterGen = for{
    segs <- Gen.containerOf[IndexedSeq, LetterSeg](letterSegGen)
    tId <- Gen.choose(0, MuseCharType.maxId)
  } yield MuseChar(segs, MuseCharType(tId))

  val editingGen = for{
    l <- letterGen
    selects <- Gen.containerOf[List, Int](Gen.choose(0,100))
  } yield Editing(l, selects)

  val vec2Check = forAll(vec2Gen){ v => readOption[Vec2](write(v)) contains v }

  val cubicCheck = forAll(cubicGen){ c => readOption[CubicCurve](write(c)) contains c }

  val letterSegCheck = forAll(letterSegGen) { l => readOption[LetterSeg](write(l)) contains l}

  val letterCheck = forAll(letterGen) { l => readOption[MuseChar](write(l)) contains l}

  val editingCheck = forAll(editingGen) { e => readOption[Editing](write(e)) contains e}


  def main(args: Array[String]) {
    vec2Check.check
    cubicCheck.check
    letterSegCheck.check
    letterCheck.check
    editingCheck.check
  }

}
