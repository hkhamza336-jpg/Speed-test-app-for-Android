package com.example

import com.example.ui.SpeedUnit
import com.example.ui.components.speedToDialFraction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun speedDialMapping_boundariesAndProgression() {
    assertEquals(0f, speedToDialFraction(0.0), 0.001f)
    assertEquals(1f, speedToDialFraction(1000.0), 0.001f)
    assertEquals(1f, speedToDialFraction(2000.0), 0.001f)

    val fraction10 = speedToDialFraction(10.0)
    val fraction50 = speedToDialFraction(50.0)
    val fraction100 = speedToDialFraction(100.0)

    assertTrue(fraction10 < fraction50)
    assertTrue(fraction50 < fraction100)
  }

  @Test
  fun speedUnit_conversionsAreAccurate() {
    val mbps = 80.0
    val mbPerSec = mbps * SpeedUnit.MB_S.multiplier
    assertEquals(10.0, mbPerSec, 0.001)

    val kbps = mbps * SpeedUnit.KBPS.multiplier
    assertEquals(80000.0, kbps, 0.001)
  }
}

