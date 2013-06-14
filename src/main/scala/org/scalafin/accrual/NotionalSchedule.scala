package org.scalafin.accrual

import org.joda.time.DateMidnight
import org.scalafin.date.DateRange

trait NotionalSchedule {
	def getNotionalPeriodsBetween(dateRange: DateRange):Seq[NotionalPeriod]
}