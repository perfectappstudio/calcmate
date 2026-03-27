package com.perfectappstudio.scientificcalc.core.math

import kotlin.math.PI
import kotlin.math.E

data class PhysicalConstant(
    val name: String,
    val symbol: String,
    val value: Double,
    val unit: String,
    val category: ConstantCategory = ConstantCategory.UNIVERSAL,
)

enum class ConstantCategory(val displayName: String) {
    PARTICLE_PHYSICS("Particle Physics"),
    ATOMIC("Atomic"),
    ELECTROMAGNETIC("Electromagnetic"),
    THERMODYNAMIC("Thermodynamic"),
    UNIVERSAL("Universal"),
}

object Constants {
    val PI_VALUE = PhysicalConstant("Pi", "\u03C0", PI, "", ConstantCategory.UNIVERSAL)
    val E_VALUE = PhysicalConstant("Euler's Number", "e", E, "", ConstantCategory.UNIVERSAL)

    // ── Particle Physics (01-04) ────────────────────────────────────────
    val PROTON_MASS = PhysicalConstant(
        "Proton Mass", "m\u209A", 1.67262192369e-27, "kg",
        ConstantCategory.PARTICLE_PHYSICS,
    )
    val NEUTRON_MASS = PhysicalConstant(
        "Neutron Mass", "m\u2099", 1.67492749804e-27, "kg",
        ConstantCategory.PARTICLE_PHYSICS,
    )
    val ELECTRON_MASS = PhysicalConstant(
        "Electron Mass", "m\u2091", 9.1093837015e-31, "kg",
        ConstantCategory.PARTICLE_PHYSICS,
    )
    val MUON_MASS = PhysicalConstant(
        "Muon Mass", "m\u03BC", 1.883531627e-28, "kg",
        ConstantCategory.PARTICLE_PHYSICS,
    )

    // ── Atomic (05, 10-16) ──────────────────────────────────────────────
    val BOHR_RADIUS = PhysicalConstant(
        "Bohr Radius", "a\u2080", 5.29177210903e-11, "m",
        ConstantCategory.ATOMIC,
    )
    val FINE_STRUCTURE = PhysicalConstant(
        "Fine-Structure Constant", "\u03B1", 7.2973525693e-3, "",
        ConstantCategory.ATOMIC,
    )
    val CLASSICAL_ELECTRON_RADIUS = PhysicalConstant(
        "Classical Electron Radius", "r\u2091", 2.8179403262e-15, "m",
        ConstantCategory.ATOMIC,
    )
    val COMPTON_WAVELENGTH = PhysicalConstant(
        "Compton Wavelength", "\u03BB\u1D04", 2.42631023867e-12, "m",
        ConstantCategory.ATOMIC,
    )
    val PROTON_COMPTON_WAVELENGTH = PhysicalConstant(
        "Proton Compton Wavelength", "\u03BB\u1D04\u209A", 1.32140985539e-15, "m",
        ConstantCategory.ATOMIC,
    )
    val NEUTRON_COMPTON_WAVELENGTH = PhysicalConstant(
        "Neutron Compton Wavelength", "\u03BB\u1D04\u2099", 1.31959090581e-15, "m",
        ConstantCategory.ATOMIC,
    )
    val RYDBERG_CONSTANT = PhysicalConstant(
        "Rydberg Constant", "R\u221E", 1.0973731568160e7, "m\u207B\u00B9",
        ConstantCategory.ATOMIC,
    )
    val ATOMIC_MASS_UNIT = PhysicalConstant(
        "Atomic Mass Unit", "u", 1.66053906660e-27, "kg",
        ConstantCategory.ATOMIC,
    )

    // ── Electromagnetic (06-09, 18-23, 32-34) ──────────────────────────
    val PLANCK = PhysicalConstant(
        "Planck Constant", "h", 6.62607015e-34, "J\u00B7s",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val NUCLEAR_MAGNETON = PhysicalConstant(
        "Nuclear Magneton", "\u03BC\u2099", 5.0507837461e-27, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val BOHR_MAGNETON = PhysicalConstant(
        "Bohr Magneton", "\u03BC\u0042", 9.2740100783e-24, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val REDUCED_PLANCK = PhysicalConstant(
        "Reduced Planck Constant", "\u0127", 1.054571817e-34, "J\u00B7s",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val PROTON_MAGNETIC_MOMENT = PhysicalConstant(
        "Proton Magnetic Moment", "\u03BC\u209A", 1.41060674333e-26, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val ELECTRON_MAGNETIC_MOMENT = PhysicalConstant(
        "Electron Magnetic Moment", "\u03BC\u2091", -9.2847647043e-24, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val NEUTRON_MAGNETIC_MOMENT = PhysicalConstant(
        "Neutron Magnetic Moment", "\u03BC\u2099\u2099", -9.6623651e-27, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val MUON_MAGNETIC_MOMENT = PhysicalConstant(
        "Muon Magnetic Moment", "\u03BC\u03BC", -4.4904483e-26, "J/T",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val FARADAY = PhysicalConstant(
        "Faraday Constant", "F", 96485.33212, "C/mol",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val ELEMENTARY_CHARGE = PhysicalConstant(
        "Elementary Charge", "e\u2080", 1.602176634e-19, "C",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val ELECTRIC_CONSTANT = PhysicalConstant(
        "Electric Constant", "\u03B5\u2080", 8.8541878128e-12, "F/m",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val MAGNETIC_CONSTANT = PhysicalConstant(
        "Magnetic Constant", "\u03BC\u2080", 1.25663706212e-6, "N/A\u00B2",
        ConstantCategory.ELECTROMAGNETIC,
    )
    val MAGNETIC_FLUX_QUANTUM = PhysicalConstant(
        "Magnetic Flux Quantum", "\u03A6\u2080", 2.067833848e-15, "Wb",
        ConstantCategory.ELECTROMAGNETIC,
    )

    // ── Thermodynamic (24-27, 31, 38) ──────────────────────────────────
    val AVOGADRO = PhysicalConstant(
        "Avogadro Constant", "N\u2090", 6.02214076e23, "mol\u207B\u00B9",
        ConstantCategory.THERMODYNAMIC,
    )
    val BOLTZMANN = PhysicalConstant(
        "Boltzmann Constant", "k", 1.380649e-23, "J/K",
        ConstantCategory.THERMODYNAMIC,
    )
    val MOLAR_VOLUME = PhysicalConstant(
        "Molar Volume of Ideal Gas", "V\u2098", 0.022413969545, "m\u00B3/mol",
        ConstantCategory.THERMODYNAMIC,
    )
    val MOLAR_GAS = PhysicalConstant(
        "Molar Gas Constant", "R", 8.314462618, "J/(mol\u00B7K)",
        ConstantCategory.THERMODYNAMIC,
    )
    val STEFAN_BOLTZMANN = PhysicalConstant(
        "Stefan-Boltzmann Constant", "\u03C3", 5.670374419e-8, "W/(m\u00B2\u00B7K\u2074)",
        ConstantCategory.THERMODYNAMIC,
    )
    val CELSIUS_TEMPERATURE = PhysicalConstant(
        "Celsius Temperature", "t", 273.15, "K",
        ConstantCategory.THERMODYNAMIC,
    )

    // ── Universal (13, 28-30, 35-40) ───────────────────────────────────
    val PROTON_GYROMAGNETIC_RATIO = PhysicalConstant(
        "Proton Gyromagnetic Ratio", "\u03B3\u209A", 2.6752218744e8, "rad/(s\u00B7T)",
        ConstantCategory.UNIVERSAL,
    )
    val SPEED_OF_LIGHT = PhysicalConstant(
        "Speed of Light", "c", 299792458.0, "m/s",
        ConstantCategory.UNIVERSAL,
    )
    val FIRST_RADIATION = PhysicalConstant(
        "First Radiation Constant", "c\u2081", 3.741771852e-16, "W\u00B7m\u00B2",
        ConstantCategory.UNIVERSAL,
    )
    val SECOND_RADIATION = PhysicalConstant(
        "Second Radiation Constant", "c\u2082", 0.014387768775, "m\u00B7K",
        ConstantCategory.UNIVERSAL,
    )
    val GRAVITY = PhysicalConstant(
        "Standard Acceleration of Gravity", "g", 9.80665, "m/s\u00B2",
        ConstantCategory.UNIVERSAL,
    )
    val CONDUCTANCE_QUANTUM = PhysicalConstant(
        "Conductance Quantum", "G\u2080", 7.748091729e-5, "S",
        ConstantCategory.UNIVERSAL,
    )
    val IMPEDANCE_OF_VACUUM = PhysicalConstant(
        "Characteristic Impedance of Vacuum", "Z\u2080", 376.730313668, "\u03A9",
        ConstantCategory.UNIVERSAL,
    )
    val GRAVITATIONAL = PhysicalConstant(
        "Newtonian Gravitational Constant", "G", 6.67430e-11, "m\u00B3/(kg\u00B7s\u00B2)",
        ConstantCategory.UNIVERSAL,
    )
    val STANDARD_ATMOSPHERE = PhysicalConstant(
        "Standard Atmosphere", "atm", 101325.0, "Pa",
        ConstantCategory.UNIVERSAL,
    )

    val ALL: List<PhysicalConstant> = listOf(
        PI_VALUE, E_VALUE,
        // Particle Physics
        PROTON_MASS, NEUTRON_MASS, ELECTRON_MASS, MUON_MASS,
        // Atomic
        BOHR_RADIUS, FINE_STRUCTURE, CLASSICAL_ELECTRON_RADIUS,
        COMPTON_WAVELENGTH, PROTON_COMPTON_WAVELENGTH, NEUTRON_COMPTON_WAVELENGTH,
        RYDBERG_CONSTANT, ATOMIC_MASS_UNIT,
        // Electromagnetic
        PLANCK, NUCLEAR_MAGNETON, BOHR_MAGNETON, REDUCED_PLANCK,
        PROTON_MAGNETIC_MOMENT, ELECTRON_MAGNETIC_MOMENT,
        NEUTRON_MAGNETIC_MOMENT, MUON_MAGNETIC_MOMENT,
        FARADAY, ELEMENTARY_CHARGE, ELECTRIC_CONSTANT, MAGNETIC_CONSTANT,
        MAGNETIC_FLUX_QUANTUM,
        // Thermodynamic
        AVOGADRO, BOLTZMANN, MOLAR_VOLUME, MOLAR_GAS,
        STEFAN_BOLTZMANN, CELSIUS_TEMPERATURE,
        // Universal
        PROTON_GYROMAGNETIC_RATIO, SPEED_OF_LIGHT, FIRST_RADIATION,
        SECOND_RADIATION, GRAVITY, CONDUCTANCE_QUANTUM, IMPEDANCE_OF_VACUUM,
        GRAVITATIONAL, STANDARD_ATMOSPHERE,
    )

    /** Group constants by category for UI display. */
    val byCategory: Map<ConstantCategory, List<PhysicalConstant>>
        get() = ALL.groupBy { it.category }
}
