package com.gottexbrokers.datemath

import com.mbc.jfin.daycount.impl.{ DaycountCalculator => JFinDaycountCalculator }
import com.mbc.jfin.daycount.impl.calculator._
import com.gottexbrokers.datemath.DayCountCalculators._
import org.specs2.{ ScalaCheck, Specification }
import com.gottexbrokers.datemath.test._
import org.specs2.specification.{ Example, Fragments }
import org.scalacheck.{ Arbitrary, Gen, Prop }
import org.joda.time._
import org.scalacheck.util.Pretty
import com.gottexbrokers.datemath.utils.OrderingImplicits
import org.specs2.matcher.Parameters
import com.gottexbrokers.datemath.math.{ PeriodGenerators }

class DayCountCalculatorsCoherenceTest extends Specification
  with ScalaCheck
  with FragmentBuildingTools
  with DateMathTestInstances
  with DayCountConventionTupleMatchers
  with JodaTimeGenerators
  with PeriodGenerators
  with OrderingImplicits
  with FromAmericanDiscoveryToJupiter {

  implicit val params = Parameters(workers = 4, minTestsOk = 5000, maxSize = 5000)

  override val defaultPrettyParams = Pretty.Params(2)

  def periodIsNotTooLongForJoda(paymentPeriod: Period[DateTime]): Boolean = {
    Math.abs((paymentPeriod.end.getMillis - paymentPeriod.start.getMillis) / DateTimeConstants.MILLIS_PER_DAY) < Int.MaxValue
  }
  //
  //	// Shows for easier debugging
  //
  import com.gottexbrokers.datemath.utils._
  import MillisShows._
  import IntervalShows._
  //	import PaymentPeriodShows._
  //
  //	def daysIn(paymentPeriod:PaymentPeriod[DateTime]):Int = {
  //		def days(interval:math.Period[DateTime]):Int = Days.daysBetween(interval.start,interval.end).getDays
  //		Math.max(days(paymentPeriod.actual) , (paymentPeriod.reference map days).getOrElse(0))
  //	}
  //
  //
  //
  def testTuple(tuple: (DayCountCalculator, JFinDaycountCalculator))(implicit arbitrary: Arbitrary[Period[DateTime]]): Example = {
    val (dateMathCalculator, jfinConvention) = tuple
    val exampleName = s"jfin.$jfinConvention and date-math.$dateMathCalculator must compute the same value "
    exampleName ! Prop.forAll {
      (period: Period[DateTime]) =>
        periodIsNotTooLongForJoda(period) ==> {
          tuple must computeIdenticalDayCountFor(period)
        }
    }
  }

  def testSimple: Example = {
    val (dateMathCalculator, jfinConvention) = tuple5
    val exampleName = s"jfin.$jfinConvention and date-math.$dateMathCalculator must compute the same value "
    val date1 = DateTime parse "2173-06-09T00:00:00.000+02:00"
    val date2 = DateTime parse "2242-02-28T00:00:00.000+01:00"
    val period: TimePeriod[DateTime] = TimePeriod(date1, date2, date1, date2)
    exampleName ! (tuple5 must computeIdenticalDayCountFor(period))
  }

  val tuple1 = Actual360DayCountCalculator -> new Actual360DaycountCalculator()

  val tuple2 = Actual365FixedDayCountCalculator -> new Actual365FixedDaycountCalculator()

  val tuple3 = Actual366DayCountCalculator -> new Actual366DaycountCalculator()

  val tuple4 = EU30360DayCountCalculator -> new EU30360DaycountCalculator()

  val tuple5 = IT30360DayCountCalculator -> new IT30360DaycountCalculator()

  val tuple6 = new US30360DayCountCalculator(true) -> new US30360DaycountCalculator()

  val nonSplittingDayCountCalculator = Seq(tuple1, tuple2, tuple3, tuple4, tuple5, tuple6)

  val tuple7 = AFBActualActualDayCountCalculator -> new AFBActualActualDaycountCalculator()

  val splittingDayCountCalculator = Seq(tuple7)

  def testNotSplitting: Fragments = {
    val periodGen = IndepedendentExtremesIntervalGenerator.intervalArbitrary[DateTime]
    val arbitrary = Arbitrary {
      periodGen.arbitrary filter periodIsNotTooLongForJoda
    }
    testChunk(nonSplittingDayCountCalculator, "The non-splitting day count calculator should be equivalent between Jfin and date-math", testTuple(_: (DayCountCalculator, JFinDaycountCalculator))(arbitrary))

  }

  def testSplitting: Fragments = {
    val maxMillisTenYears = Years.years(10) get DurationFieldType.millis()
    implicit val millisGen = Gen.choose(0L, maxMillisTenYears)
    implicit val adder = (d1: DateTime, millis: Long) => d1 plus millis
    val periodGen = MaxSizedGenerator.maxSizedIntervalArbitrary[DateTime, Long]
    val arbitrary = Arbitrary {
      periodGen.arbitrary filter periodIsNotTooLongForJoda
    }
    testChunk(splittingDayCountCalculator, "The non-splitting day count calculator should be equivalent between Jfin and date-math", testTuple(_: (DayCountCalculator, JFinDaycountCalculator))(arbitrary))

  }

  override def is: Fragments = testNotSplitting ^ testSplitting

}



