package com.example.speedy

sealed class Screens(val screen:String) {

    data object Car: Screens("car")
    data object Drivetrain: Screens("drivetrain")
    data object Tires: Screens("tires")
    data object Aerodynamics: Screens("aerodynamics")
    data object Results: Screens("results")
    data object Settings: Screens("settings")
    data object About: Screens("about")
    data object DataSets: Screens("datasets")
}