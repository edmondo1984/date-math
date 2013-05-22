package org.scalafin.date

import java.util.GregorianCalendar
import org.joda.time.DateMidnight
import org.junit.runner.RunWith
import org.scalafin.date.util.ISDADateFormat
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.scalafin.date.daycount.DaycountCalculators.ISMAActualActualDaycountCalculator
import org.scalafin.date.daycount.DaycountCalculators.Actual360DaycountCalculator
import scalaz.Success
import org.scalafin.date.daycount.DaycountCalculators
import org.scalafin.date.daycount.Actual360

@RunWith(classOf[JUnitRunner])
class ScheduleGeneratorSanityCheckTest extends Specification {

  "daysBetween" should {
    "return the right number of days between 2 Gregorian dates" in {
      val start = new DateMidnight(new GregorianCalendar(2005, 0, 1))
      val end = new DateMidnight(new GregorianCalendar(2010, 1, 1))
      val calculator = new DateOps {}
      calculator.daysBetween(start, end) === 1857
    }

    "return the right number of days between 2 Midnight dates" in {
      val start = ISDADateFormat.parse("2005/1/1")
      val end = ISDADateFormat.parse("2010/2/1")
      val calculator = new DateOps {}
      calculator.daysBetween(start, end) === 1857
    }
  }

  "ScheduleGenerator with no stubs" should {
    "create even periods" in {
      val start = new DateMidnight(new GregorianCalendar(2005, 0, 1))
      val end = new DateMidnight(new GregorianCalendar(2010, 0, 1))
      val frequency = QUARTERLY
      val stubType = NONE
      val dateRange = DateRange(start, end)

      val calculator2 = DaycountCalculators[Actual360]
      
      
      val calculator = ISMAActualActualDaycountCalculator(frequency)

      val calculationResult = dateRange map calculator.apply

      calculationResult must beLike {
        case Success(e) => e must beCloseTo(5.0722222, 1e-7)
      }

    }

    //    "again even periods" in {
    //      val start = ISDADateFormat.parse("2006/3/23")
    //      val end = ISDADateFormat.parse("2011/3/23")
    //      val frequency = QUARTERLY
    //      val stubType = NONE
    //
    //      val dateRange = DateRange(start, end).toOption.get
    //      val periods = ScheduleGenerator.generateSchedule(dateRange, stubType, frequency)
    //
    //      val calculator = new ISMAActualActual()
    //
    //      periods map { period =>
    //        calculator.calculateDaycountFraction(period).toOption.get === 0.25d
    //      }
    //    }

  }

  //  "ScheduleGenerator with Short first" should {
  //    "have a shorter first period" in {
  //      val start = new DateMidnight(new GregorianCalendar(2005, 0, 1))
  //      val end = new DateMidnight(new GregorianCalendar(2010, 1, 1))
  //      val frequency = QUARTERLY
  //      val stubType = SHORT_FIRST
  //
  //      val dateRange = DateRange(start, end).toOption.get
  //      val periods = ScheduleGenerator.generateSchedule(dateRange, stubType, frequency)
  //
  //      val calculator = new ISMAActualActual()
  //
  //      periods map { period =>
  //        calculator.calculateDaycountFraction(period).toOption.get === 0.25d
  //      }
  //    }
  //  }
}