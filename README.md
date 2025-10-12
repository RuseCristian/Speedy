# 🏎️ Speedy - Car Acceleration Estimator

[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Stars](https://img.shields.io/github/stars/RuseCristian/Speedy?style=flat)](https://github.com/RuseCristian/Speedy/stargazers)

> **The physics-accurate way to estimate your car's straight-line acceleration performance**

Ever wondered how fast your car _actually_ accelerates from 0-60? Speedy calculates real-world acceleration times using precise physics formulas and detailed vehicle parameters – because guessing is for weather forecasts, not horsepower.

**🏆 2nd Place Winner** - Scientific Communications Session 2025, Polytechnic University of Bucharest, Faculty of Engineering in Foreing Languages

## What Does Speedy Do?

Speedy is a comprehensive vehicle acceleration simulator that transforms your car's technical specifications into accurate performance predictions. Developed as my **Bachelor's Thesis project**, this app bridges the gap between theoretical automotive physics, practical performance analysis and software development.

The application guides you through inputting detailed vehicle parameters across five specialized screens (Car, Drivetrain, Tires, Aerodynamics, Engine), then uses advanced physics calculations to simulate real-world acceleration performance. Whether you're an automotive engineer, racing enthusiast, or curious car owner, Speedy provides professional-grade acceleration analysis with the convenience of a mobile app.

## 📱 Screenshots

<div align="center">
  <img width="270" height="600" alt="Screenshot_1744552784" src="https://github.com/user-attachments/assets/d5cbf976-8a4d-4edb-868a-a58aad8aafe2" />
  <img width="270" height="600" alt="Screenshot_1744552936" src="https://github.com/user-attachments/assets/17f66680-f35e-4bc8-8f4b-7c62e8d7cd90" />
  <img width="270" height="600" alt="Screenshot_1744549461" src="https://github.com/user-attachments/assets/51e19961-ec8f-4365-9f01-7486392a0697" />
  <img width="270" height="600" alt="Screenshot_1744552773" src="https://github.com/user-attachments/assets/3b5180be-4ca1-453b-a52e-bb66c03f50a9" />
  <img width="270" height="600" alt="Screenshot_1744554824" src="https://github.com/user-attachments/assets/c9087ade-bf04-4bad-a899-3246174b1c2e" />
  <img width="270" height="600" alt="Screenshot_1744554850" src="https://github.com/user-attachments/assets/b5347e00-55d8-4455-9325-b01ebf900a8b" />
</div>

## Key Features

- **Physics-accurate calculations** – Advanced engine simulation with gear-specific torque curves, weight transfer, and aerodynamics
- **Comprehensive vehicle parameters** – Input engine torque curves, gear ratios, tire specs, aerodynamics, drivetrain layout, and more
- **Real-time performance visualization** – Speed vs. time graphs with gear-by-gear analysis using Vico charts
- **Multiple drivetrain support** – FWD, RWD, and AWD configurations with proper weight distribution
- **Advanced aerodynamics** – Downforce, drag coefficient, and air density calculations
- **Intelligent gear shifting** – Automatic optimal upshift point detection based on torque curves
- **Firebase integration** – User authentication and cloud data synchronization with caching
- **Theme customization** – Dark/light theme with persistent user preferences via DataStore
- **Vehicle profile management** – Save, load, and compare multiple car configurations in cloud storage
- **Unit conversion system** – Seamless metric/imperial conversion with precision retention
- **Modern Material 3 UI** – Clean Jetpack Compose interface with smooth animations and transitions

## 🚀 Quick Start

### Prerequisites

- Android device running API level 27+ (Android 8.1)
- About 15MB of storage space
- Google account for Firebase authentication
- Detailed knowledge of your vehicle's specifications (torque curve, gear ratios, etc.)

### Installation

1. **Download the APK** from the [Releases](https://github.com/RuseCristian/Speedy/releases) section
2. **Enable Unknown Sources** in your Android settings (`Settings > Security > Unknown Sources`)
3. **Install** and launch the app
4. **Sign in with Google** for cloud synchronization and data backup
5. **Enter your vehicle data** through the guided input screens and start calculating!

### Usage Example

1. **Create account** and authenticate with Google Firebase
2. **Navigate through comprehensive input screens**:

   - **Car Tab**: Mass (kg/lbs), center of mass distribution (%), wheelbase, height
   - **Drivetrain Tab**: Layout (FWD/RWD/AWD), final drive ratio, shift times, clutch RPM
   - **Tires Tab**: Width, aspect ratio, wheel diameter, friction coefficient, rolling resistance
   - **Aerodynamics Tab**: Drag coefficient, frontal area, optional downforce modeling
   - **Engine Tab**: Complete RPM-Torque curve points for accurate power delivery simulation

3. **View Results** with detailed performance graphs showing speed progression through all gears
4. **Save vehicle profiles** to Firebase cloud storage for future analysis and comparison
5. **Analyze performance data** with Vico chart visualizations

## 🛠️ Built With

- **[Kotlin](https://kotlinlang.org/)** - Modern Android development language
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Declarative UI toolkit
- **[Material 3](https://m3.material.io/)** - Google's latest design system
- **[Firebase](https://firebase.google.com/)** - Authentication and cloud data storage
- **[Vico Charts](https://github.com/patrykandpatrick/vico)** - Performance visualization graphs
- **[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)** - User preferences storage
- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** - Screen navigation

## How the Calculation Engine Works

Speedy transforms your vehicle specifications into acceleration performance through a sophisticated multi-step process:

### 1. Data Collection & Validation

```pseudocode
FOR each input screen (Car, Drivetrain, Tires, Aero, Engine):
    COLLECT user input parameters
    VALIDATE data ranges and dependencies
    CONVERT units if needed (metric/imperial)
    STORE in SharedViewModel
```

### 2. Physics Parameter Preparation

```pseudocode
CALCULATE tire radius from (width × aspect_ratio × 2 + wheel_diameter)
INTERPOLATE engine torque curve to match RPM resolution
APPLY drivetrain losses to torque values
SET initial conditions (speed, gear, RPM)
```

### 3. Gear-by-Gear Force Analysis

```pseudocode
FOR each gear ratio:
    FOR each RPM point:
        torque_at_wheels = (engine_torque × gear_ratio × final_drive) / wheel_radius
        air_resistance = 0.5 × air_density × drag_coeff × frontal_area × speed²
        rolling_resistance = rolling_coeff × gravity × mass

        IF downforce_enabled:
            downforce = 0.5 × air_density × lift_coeff × downforce_area × speed²

        net_force = torque_at_wheels - air_resistance - rolling_resistance

        IF weight_transfer_enabled:
            weight_distribution = CALCULATE_dynamic_load_transfer()
            max_traction = APPLY_traction_limits(drivetrain_layout, weight_distribution)
            net_force = MIN(net_force, max_traction)

        acceleration = net_force / mass
        STORE(speed, acceleration, rpm) in GearData
```

### 4. Optimal Shift Point Detection

```pseudocode
FOR each adjacent gear pair:
    FOR rpm = max_rpm DOWN TO idle_rpm:
        current_gear_accel = gear[i].acceleration[rpm]
        next_gear_accel = gear[i+1].acceleration[corresponding_speed]

        IF current_gear_accel >= next_gear_accel:
            optimal_shift_point = rpm
            BREAK
```

### 5. Time-Based Acceleration Simulation

```pseudocode
current_speed = initial_speed
total_time = 0
current_gear = FIND_starting_gear(initial_speed)

WHILE current_speed < target_speed:
    IF rpm == optimal_shift_point:
        SIMULATE_shift_time_with_deceleration()
        current_gear++

    IF rpm <= clutch_engagement_rpm:
        SIMULATE_clutch_slip_and_gas_modulation()

    time_step = speed_increment / acceleration[current_rpm]
    total_time += time_step
    current_speed += speed_increment
    current_rpm++

RETURN total_time
```

This multi-domain approach ensures that every physical factor affecting vehicle acceleration is accurately modeled, from engine characteristics and transmission behavior to aerodynamic forces and tire limitations.

## 🎓 Academic Background - Bachelor's Thesis Project

This application represents the culmination of my **Bachelor's Thesis in Automotive Engineering**, awarded **2nd Place** at the Scientific Communications Session 2025 at Polytechnic University of Bucharest. The project demonstrates the intersection of theoretical automotive physics and practical mobile application development.

### Thesis Objectives

- **Research Goal**: Develop a comprehensive digital tool for accurate vehicle acceleration prediction
- **Mathematical Foundation**: Implement complex automotive physics calculations in a user-friendly interface
- **Technical Challenge**: Bridge the gap between automotive engineering principles and modern Android development
- **Validation**: Compare theoretical calculations with real-world dyno testing data

### Engineering Methodology

The thesis employed a multi-disciplinary approach combining:

- **Mechanical Engineering**: Force analysis, weight transfer dynamics, and powertrain mechanics
- **Fluid Dynamics**: Aerodynamic drag and downforce modeling with Reynolds number considerations
- **Software Engineering**: Clean architecture principles with MVVM pattern and reactive programming
- **Human-Computer Interaction**: Intuitive multi-screen input flow for complex technical data

### Academic Validation

- **Data Verification**: Algorithm validated against published automotive performance data
- **Real-world Testing**: Compared app predictions with actual vehicle acceleration measurements
- **Peer Review**: Thesis reviewed by automotive engineering faculty and industry professionals
- **Academic Recognition**: Recognized for innovative approach to automotive simulation technology

## 🤝 Contributing

Want to make Speedy even better? Contributions are welcome!

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Ideas for contributions:

- [ ] Quarter-mile and half-mile time calculations
- [ ] Torque and power curve visualization
- [ ] Pre-built vehicle database with popular cars
- [ ] Export results to PDF/CSV
- [ ] Comparison mode for multiple vehicles
- [ ] Lap time simulation for different tracks
- [ ] Dyno chart import functionality
- [ ] Unit testing for physics calculations

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **[Iulia Cristina Stanica](https://ro.linkedin.com/in/iulia-cristina-stanica)** - My thesis advisor for guidance on the physics calculations and automotive engineering principles
- The Android development community for Jetpack Compose resources and best practices
- Fellow automotive engineering students and car enthusiasts who tested the app and provided valuable feedback

---

<div align="center">
  <strong>⭐ Star this repo if you found it useful! ⭐</strong>
  <br>
  <sub>Made with ❤️ and lots of ☕ for automotive engineering</sub>
</div>
