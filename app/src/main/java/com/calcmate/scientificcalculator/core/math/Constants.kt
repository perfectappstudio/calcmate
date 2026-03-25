package com.calcmate.scientificcalculator.core.math

import kotlin.math.PI
import kotlin.math.E

data class PhysicalConstant(
    val name: String,
    val symbol: String,
    val value: Double,
    val unit: String
)

object Constants {
    val PI_VALUE = PhysicalConstant("Pi", "π", PI, "")
    val E_VALUE = PhysicalConstant("Euler's Number", "e", E, "")

    val SPEED_OF_LIGHT = PhysicalConstant(
        "Speed of Light", "c", 299792458.0, "m/s"
    )
    val AVOGADRO = PhysicalConstant(
        "Avogadro's Number", "Nₐ", 6.02214076e23, "mol⁻¹"
    )
    val PLANCK = PhysicalConstant(
        "Planck's Constant", "h", 6.62607015e-34, "J·s"
    )
    val BOLTZMANN = PhysicalConstant(
        "Boltzmann Constant", "k", 1.380649e-23, "J/K"
    )
    val GRAVITATIONAL = PhysicalConstant(
        "Gravitational Constant", "G", 6.67430e-11, "m³/(kg·s²)"
    )
    val ELECTRON_MASS = PhysicalConstant(
        "Electron Mass", "mₑ", 9.1093837015e-31, "kg"
    )
    val PROTON_MASS = PhysicalConstant(
        "Proton Mass", "mₚ", 1.67262192369e-27, "kg"
    )
    val ELEMENTARY_CHARGE = PhysicalConstant(
        "Elementary Charge", "e", 1.602176634e-19, "C"
    )

    val ALL = listOf(
        PI_VALUE, E_VALUE,
        SPEED_OF_LIGHT, AVOGADRO, PLANCK, BOLTZMANN,
        GRAVITATIONAL, ELECTRON_MASS, PROTON_MASS, ELEMENTARY_CHARGE
    )
}
