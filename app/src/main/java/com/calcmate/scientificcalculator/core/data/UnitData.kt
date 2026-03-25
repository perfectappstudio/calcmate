package com.calcmate.scientificcalculator.core.data

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    VOLUME("Volume"),
    AREA("Area"),
    SPEED("Speed"),
    TIME("Time"),
    DATA_STORAGE("Data"),
    PRESSURE("Pressure"),
    ENERGY("Energy"),
}

data class UnitDef(
    val name: String,
    val symbol: String,
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double,
)

object UnitData {

    val units: Map<UnitCategory, List<UnitDef>> = mapOf(

        // ── LENGTH (base: meter) ────────────────────────────────────────
        UnitCategory.LENGTH to listOf(
            UnitDef("Millimeter", "mm", { it / 1_000.0 }, { it * 1_000.0 }),
            UnitDef("Centimeter", "cm", { it / 100.0 }, { it * 100.0 }),
            UnitDef("Meter", "m", { it }, { it }),
            UnitDef("Kilometer", "km", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Inch", "in", { it * 0.0254 }, { it / 0.0254 }),
            UnitDef("Foot", "ft", { it * 0.3048 }, { it / 0.3048 }),
            UnitDef("Yard", "yd", { it * 0.9144 }, { it / 0.9144 }),
            UnitDef("Mile", "mi", { it * 1_609.344 }, { it / 1_609.344 }),
            UnitDef("Nautical Mile", "nmi", { it * 1_852.0 }, { it / 1_852.0 }),
            UnitDef("Micrometer", "\u00B5m", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
            UnitDef("Nanometer", "nm", { it / 1_000_000_000.0 }, { it * 1_000_000_000.0 }),
            UnitDef("Light-year", "ly", { it * 9.4607e15 }, { it / 9.4607e15 }),
        ),

        // ── WEIGHT (base: kilogram) ─────────────────────────────────────
        UnitCategory.WEIGHT to listOf(
            UnitDef("Milligram", "mg", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
            UnitDef("Gram", "g", { it / 1_000.0 }, { it * 1_000.0 }),
            UnitDef("Kilogram", "kg", { it }, { it }),
            UnitDef("Tonne", "t", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Pound", "lb", { it * 0.45359237 }, { it / 0.45359237 }),
            UnitDef("Ounce", "oz", { it * 0.028349523125 }, { it / 0.028349523125 }),
            UnitDef("Stone", "st", { it * 6.35029318 }, { it / 6.35029318 }),
            UnitDef("Grain", "gr", { it * 0.00006479891 }, { it / 0.00006479891 }),
            UnitDef("Carat", "ct", { it * 0.0002 }, { it / 0.0002 }),
        ),

        // ── TEMPERATURE (non-linear conversions, base: Celsius) ─────────
        UnitCategory.TEMPERATURE to listOf(
            UnitDef(
                "Celsius", "\u00B0C",
                toBase = { it },
                fromBase = { it },
            ),
            UnitDef(
                "Fahrenheit", "\u00B0F",
                toBase = { (it - 32.0) * 5.0 / 9.0 },
                fromBase = { it * 9.0 / 5.0 + 32.0 },
            ),
            UnitDef(
                "Kelvin", "K",
                toBase = { it - 273.15 },
                fromBase = { it + 273.15 },
            ),
            UnitDef(
                "Rankine", "\u00B0R",
                toBase = { (it - 491.67) * 5.0 / 9.0 },
                fromBase = { it * 9.0 / 5.0 + 491.67 },
            ),
        ),

        // ── VOLUME (base: liter) ────────────────────────────────────────
        UnitCategory.VOLUME to listOf(
            UnitDef("Milliliter", "mL", { it / 1_000.0 }, { it * 1_000.0 }),
            UnitDef("Liter", "L", { it }, { it }),
            UnitDef("Gallon (US)", "gal", { it * 3.785411784 }, { it / 3.785411784 }),
            UnitDef("Gallon (UK)", "gal UK", { it * 4.54609 }, { it / 4.54609 }),
            UnitDef("Quart", "qt", { it * 0.946352946 }, { it / 0.946352946 }),
            UnitDef("Pint", "pt", { it * 0.473176473 }, { it / 0.473176473 }),
            UnitDef("Cup", "cup", { it * 0.2365882365 }, { it / 0.2365882365 }),
            UnitDef("Fluid Ounce", "fl oz", { it * 0.0295735296 }, { it / 0.0295735296 }),
            UnitDef("Tablespoon", "tbsp", { it * 0.0147867648 }, { it / 0.0147867648 }),
            UnitDef("Teaspoon", "tsp", { it * 0.00492892159 }, { it / 0.00492892159 }),
            UnitDef("Cubic Meter", "m\u00B3", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Cubic Centimeter", "cm\u00B3", { it / 1_000.0 }, { it * 1_000.0 }),
        ),

        // ── AREA (base: m\u00B2) ───────────────────────────────────────────
        UnitCategory.AREA to listOf(
            UnitDef("Square Millimeter", "mm\u00B2", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
            UnitDef("Square Centimeter", "cm\u00B2", { it / 10_000.0 }, { it * 10_000.0 }),
            UnitDef("Square Meter", "m\u00B2", { it }, { it }),
            UnitDef("Square Kilometer", "km\u00B2", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("Hectare", "ha", { it * 10_000.0 }, { it / 10_000.0 }),
            UnitDef("Acre", "ac", { it * 4_046.8564224 }, { it / 4_046.8564224 }),
            UnitDef("Square Foot", "ft\u00B2", { it * 0.09290304 }, { it / 0.09290304 }),
            UnitDef("Square Inch", "in\u00B2", { it * 0.00064516 }, { it / 0.00064516 }),
            UnitDef("Square Yard", "yd\u00B2", { it * 0.83612736 }, { it / 0.83612736 }),
            UnitDef("Square Mile", "mi\u00B2", { it * 2_589_988.110336 }, { it / 2_589_988.110336 }),
        ),

        // ── SPEED (base: m/s) ───────────────────────────────────────────
        UnitCategory.SPEED to listOf(
            UnitDef("Meter/second", "m/s", { it }, { it }),
            UnitDef("Kilometer/hour", "km/h", { it / 3.6 }, { it * 3.6 }),
            UnitDef("Mile/hour", "mph", { it * 0.44704 }, { it / 0.44704 }),
            UnitDef("Knot", "kn", { it * 0.514444 }, { it / 0.514444 }),
            UnitDef("Foot/second", "ft/s", { it * 0.3048 }, { it / 0.3048 }),
            UnitDef("Mach", "Ma", { it * 343.0 }, { it / 343.0 }),
        ),

        // ── TIME (base: second) ─────────────────────────────────────────
        UnitCategory.TIME to listOf(
            UnitDef("Nanosecond", "ns", { it / 1_000_000_000.0 }, { it * 1_000_000_000.0 }),
            UnitDef("Microsecond", "\u00B5s", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
            UnitDef("Millisecond", "ms", { it / 1_000.0 }, { it * 1_000.0 }),
            UnitDef("Second", "s", { it }, { it }),
            UnitDef("Minute", "min", { it * 60.0 }, { it / 60.0 }),
            UnitDef("Hour", "h", { it * 3_600.0 }, { it / 3_600.0 }),
            UnitDef("Day", "d", { it * 86_400.0 }, { it / 86_400.0 }),
            UnitDef("Week", "wk", { it * 604_800.0 }, { it / 604_800.0 }),
            UnitDef("Month", "mo", { it * 2_592_000.0 }, { it / 2_592_000.0 }),
            UnitDef("Year", "yr", { it * 31_536_000.0 }, { it / 31_536_000.0 }),
        ),

        // ── DATA STORAGE (base: byte) ───────────────────────────────────
        UnitCategory.DATA_STORAGE to listOf(
            UnitDef("Bit", "b", { it / 8.0 }, { it * 8.0 }),
            UnitDef("Byte", "B", { it }, { it }),
            UnitDef("Kilobyte", "KB", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Megabyte", "MB", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("Gigabyte", "GB", { it * 1_000_000_000.0 }, { it / 1_000_000_000.0 }),
            UnitDef("Terabyte", "TB", { it * 1_000_000_000_000.0 }, { it / 1_000_000_000_000.0 }),
            UnitDef("Petabyte", "PB", { it * 1_000_000_000_000_000.0 }, { it / 1_000_000_000_000_000.0 }),
            UnitDef("Kibibyte", "KiB", { it * 1_024.0 }, { it / 1_024.0 }),
            UnitDef("Mebibyte", "MiB", { it * 1_048_576.0 }, { it / 1_048_576.0 }),
            UnitDef("Gibibyte", "GiB", { it * 1_073_741_824.0 }, { it / 1_073_741_824.0 }),
            UnitDef("Tebibyte", "TiB", { it * 1_099_511_627_776.0 }, { it / 1_099_511_627_776.0 }),
        ),

        // ── PRESSURE (base: pascal) ─────────────────────────────────────
        UnitCategory.PRESSURE to listOf(
            UnitDef("Pascal", "Pa", { it }, { it }),
            UnitDef("Kilopascal", "kPa", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Megapascal", "MPa", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("Bar", "bar", { it * 100_000.0 }, { it / 100_000.0 }),
            UnitDef("Millibar", "mbar", { it * 100.0 }, { it / 100.0 }),
            UnitDef("Atmosphere", "atm", { it * 101_325.0 }, { it / 101_325.0 }),
            UnitDef("PSI", "psi", { it * 6_894.757293168 }, { it / 6_894.757293168 }),
            UnitDef("mmHg", "mmHg", { it * 133.322387415 }, { it / 133.322387415 }),
            UnitDef("inHg", "inHg", { it * 3_386.389 }, { it / 3_386.389 }),
            UnitDef("Torr", "Torr", { it * 133.322368421 }, { it / 133.322368421 }),
        ),

        // ── ENERGY (base: joule) ────────────────────────────────────────
        UnitCategory.ENERGY to listOf(
            UnitDef("Joule", "J", { it }, { it }),
            UnitDef("Kilojoule", "kJ", { it * 1_000.0 }, { it / 1_000.0 }),
            UnitDef("Megajoule", "MJ", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("Calorie", "cal", { it * 4.184 }, { it / 4.184 }),
            UnitDef("Kilocalorie", "kcal", { it * 4_184.0 }, { it / 4_184.0 }),
            UnitDef("Watt-hour", "Wh", { it * 3_600.0 }, { it / 3_600.0 }),
            UnitDef("Kilowatt-hour", "kWh", { it * 3_600_000.0 }, { it / 3_600_000.0 }),
            UnitDef("BTU", "BTU", { it * 1_055.06 }, { it / 1_055.06 }),
            UnitDef("Electronvolt", "eV", { it * 1.602176634e-19 }, { it / 1.602176634e-19 }),
            UnitDef("Foot-pound", "ft\u00B7lbf", { it * 1.3558179483 }, { it / 1.3558179483 }),
        ),
    )

    fun convert(value: Double, from: UnitDef, to: UnitDef): Double {
        val baseValue = from.toBase(value)
        return to.fromBase(baseValue)
    }

    fun unitsFor(category: UnitCategory): List<UnitDef> =
        units[category] ?: emptyList()
}
