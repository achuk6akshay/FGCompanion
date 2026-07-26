# FGCompanion

FGCompanion is a mobile companion app for [FlightGear](https://www.flightgear.org/) that receives live telemetry data (altitude, speed, heading, etc.) from the simulator using the **Generic Protocol**. It allows you to visualize flight parameters on your phone while FlightGear runs on your PC.

---

## ✈️ Features
- Real-time telemetry streaming over UDP/TCP
- Displays altitude, airspeed, heading, and other key parameters
- Configurable update frequency (default: 30 Hz)
- Lightweight and responsive interface for mobile devices

---

## ⚙️ Setup Instructions

### 1. FlightGear Configuration
Run FlightGear with the following command-line option:

```bash
fgfs --generic=socket,out,30,<PHONE_IP>,5500,udp,myprotocol.xml
