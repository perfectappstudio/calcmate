package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.Constants
import org.junit.Test
import org.junit.Assert.*

class ConstantsReferenceTest {
    @Test fun physicalConstantsMatchReviewedCodataReference() {
        val reference = javaClass.getResourceAsStream("/codata-2022-values.tsv")!!.bufferedReader().use { it.readLines() }
        assertEquals(39, reference.size)
        val constants = Constants.ALL.associateBy { it.name }
        assertEquals(42, constants.size)
        reference.forEach { row ->
            val fields = row.split('\t')
            assertEquals(fields[0], fields[1].toDouble(), constants.getValue(fields[0]).value, 0.0)
        }
        assertEquals(273.15, Constants.CELSIUS_TEMPERATURE.value, 0.0)
    }

    @Test fun exactSiDerivedConstantsUseStoredFundamentalPrecision() {
        assertEquals(1.0, Constants.FARADAY.value / (Constants.AVOGADRO.value * Constants.ELEMENTARY_CHARGE.value), 1e-15)
        assertEquals(1.0, Constants.MOLAR_GAS.value / (Constants.AVOGADRO.value * Constants.BOLTZMANN.value), 1e-15)
        assertEquals(1.0, Constants.REDUCED_PLANCK.value / (Constants.PLANCK.value / (2 * Math.PI)), 1e-15)
        assertTrue(Constants.MOLAR_VOLUME.name.contains("101.325 kPa"))
        assertEquals(1.0, Constants.MOLAR_VOLUME.value / (Constants.MOLAR_GAS.value * 273.15 / 101325), 1e-15)
    }
}
