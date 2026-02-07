package com.example.ch4

import java.time.Duration
import java.util.*

object Legs {
    @JvmStatic
    fun findLongestLegOver(
        legs: List<Leg>,
        duration: Duration
    ): Optional<Leg> {
        var result: Leg? = null
        for (leg in legs) {
            if (isLongerThan(leg, duration))
                if (result == null ||
                    isLongerThan(leg, result.plannedDuration))
                    result = leg
        }
        return Optional.ofNullable(result)
    }


    private fun isLongerThan(leg: Leg, duration: Duration): Boolean {
        return leg.plannedDuration.compareTo(duration) > 0
    }

    // 마이그레이션을 위해 추가된 버전1
//    fun longestLegOver(legs: List<Leg>, duration: Duration): Leg? {
//        var result: Leg? = null
//        for (leg in legs) {
//            if (isLongerThan(leg, duration))
//                if (result == null ||
//                    isLongerThan(leg, result.plannedDuration))
//                    result = leg
//        }
//        return result
//    }

    // 마이그레이션을 위해 추가된 버전2
//    fun longestLegOver(
//        legs: List<Leg>,
//        duration: Duration
//    ): Leg? {
//        val longestLeg = legs.maxByOrNull(Leg::plannedDuration)
//        return when {
//            longestLeg == null -> null
//            longestLeg.plannedDuration > duration -> longestLeg
//            else -> null
//        }
//    }

    // 마이그레이션을 위해 주석 처리된 버전3
    fun longestLegOver(
        legs: List<Leg>, duration: Duration
    ): Leg? = legs.maxByOrNull(Leg::plannedDuration)?.takeIf { longestLeg ->
        longestLeg.plannedDuration > duration
    }

    /*
    @JvmStatic
    fun findLongestLegOver(
        legs: List<Leg>,
        duration: Duration
    ): Optional<Leg> {
        return Optional.ofNullable(longestLegOver(legs, duration))
    }

     */

}