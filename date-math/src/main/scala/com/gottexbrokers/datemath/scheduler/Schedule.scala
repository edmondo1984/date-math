package com.gottexbrokers.datemath.scheduler

import com.gottexbrokers.datemath.{ Frequency, StubType, TimePeriod }
import org.joda.time.ReadableDateTime

case class Schedule private[datemath] (periods: Stream[TimePeriod[ReadableDateTime]], start: ReadableDateTime, end: ReadableDateTime)



