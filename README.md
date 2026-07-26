# FGCompanion

FGCompanion is a mobile companion app for [FlightGear](https://www.flightgear.org/) that receives live telemetry data (altitude, speed, heading, etc.) from the simulator using the **Generic Protocol**. It allows you to visualize flight parameters on your phone while FlightGear runs on your PC.

---

## ✈️ Features
- Real-time telemetry streaming over UDP/TCP
- Displays altitude, airspeed, heading, and other key parameters
- Configurable update frequency (default: 30 Hz)
- Lightweight and responsive interface for mobile devices

---

## 🛠️ FlightGear Setup & Protocol Configuration

To establish two-way UDP communication between FlightGear and FGCompanion, you must place the protocol XML file in FlightGear's protocol directory and pass the corresponding socket flags at startup.

### 1. Protocol File (`fgcompanion.xml`)

Create a file named `fgcompanion.xml` inside your FlightGear `Protocol` folder:
- **Windows:** `C:\Program Files\FlightGear\data\Protocol\`
- **Linux:** `/usr/share/games/flightgear/Protocol/` or `~/.fgfs/Protocol/`
- **macOS:** `/Applications/FlightGear.app/Contents/Resources/data/Protocol/`

Add the following XML definition:

```xml
<?xml version="1.0"?>

<PropertyList>
    <generic>
        <output>

            <binary_mode>false</binary_mode>
            <line_separator>newline</line_separator>
            <var_separator>,</var_separator>

            <!-- 0 Latitude -->
            <chunk>
                <node>/position/latitude-deg</node>
                <type>double</type>
                <format>%.6f</format>
            </chunk>

            <!-- 1 Longitude -->
            <chunk>
                <node>/position/longitude-deg</node>
                <type>double</type>
                <format>%.6f</format>
            </chunk>

            <!-- 2 Altitude -->
            <chunk>
                <node>/position/altitude-ft</node>
                <type>double</type>
                <format>%.1f</format>
            </chunk>

            <!-- 3 IAS -->
            <chunk>
                <node>/velocities/airspeed-kt</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 4 Heading -->
            <chunk>
                <node>/orientation/heading-deg</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 5 Pitch -->
            <chunk>
                <node>/orientation/pitch-deg</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 6 Roll -->
            <chunk>
                <node>/orientation/roll-deg</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 7 Vertical speed -->
            <chunk>
                <node>/velocities/vertical-speed-fps</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 8 TAS -->
            <chunk>
                <node>/velocities/true-airspeed-kt</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 9 Ground speed -->
            <chunk>
                <node>/velocities/groundspeed-kt</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 10 Magnetic track -->
            <chunk>
                <node>/orientation/track-magnetic-deg</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 11 Autopilot -->
            <chunk>
                <node>/instrumentation/annunciators/autoflight/ap/enabled</node>
                <type>bool</type>
                <format>%d</format>
            </chunk>

            <!-- 12 gear property -->
            <chunk>
                <node>/controls/gear/gear-down</node>
                <type>bool</type>
                <format>%d</format>
            </chunk>

            <!-- 13 flaps input -->
            <chunk>
                <node>/controls/flight/flaps</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

            <!-- 14 Throttle position -->
            <chunk>
                <node>/controls/engines/current-engine/throttle</node>
                <type>double</type>
                <format>%.2f</format>
            </chunk>

        </output>
    </generic>
</PropertyList>
```


---

## ⚙️ Setup Instructions

### 1. FlightGear Configuration
Run FlightGear with the following command-line option:

```bash
fgfs --generic=socket,out,30,<PHONE_IP>,5500,udp,fgcompanion.xml

