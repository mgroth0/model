package matt.model.data.bmi

fun calculateBMI(
    heightInInches: Double,
    weightInPounds: Double,
): Double {
    val heightInMeters = heightInInches * 0.0254 // Convert inches to meters
    val weightInKilograms = weightInPounds * 0.45359237 // Convert pounds to kilograms
    return weightInKilograms / (heightInMeters * heightInMeters)
}
