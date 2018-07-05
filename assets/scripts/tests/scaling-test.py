# Test script. Tests the scaling commands.
# Created by Toni Sagrista

from gaia.cu9.ari.gaiaorbit.script import EventScriptingInterface
from datetime import datetime
import time

def current_time_ms():
    return int(round(time.time() * 1000))

gs = EventScriptingInterface.instance()

gs.disableInput()
gs.minimizeInterfaceWindow()
gs.cameraStop()

# Orbits and labels off
gs.setVisibility("element.orbits", False)
gs.setVisibility("element.labels", False)

# Around 5 AU in north ecliptic pole
eclpos = gs.eclipticToInternalCartesian(0.0, 90.0, 7.5e8)

gs.setCameraPosition(eclpos)
gs.setCameraFocus("Sol", -1)


# Set free camera
gs.setCameraFree()
# Starting time
gs.setSimulationTime(2017, 12, 1, 10, 5, 0, 0)



# Scale objects
gs.setObjectSizeScaling("Mercury", 3000.0)
gs.setObjectSizeScaling("Venus", 3000.0)
gs.setObjectSizeScaling("Earth", 3000.0)
gs.setObjectSizeScaling("Mars", 3000.0)
gs.setObjectSizeScaling("Jupiter", 3000.0)
gs.setObjectSizeScaling("Saturn", 3000.0)
gs.setObjectSizeScaling("Uranus", 3000.0)
gs.setObjectSizeScaling("Neptune", 3000.0)
gs.setObjectSizeScaling("Pluto", 100000.0)

gs.sleep(3)

# Fast pace
gs.setSimulationPace(2e6)
# Start!
gs.startSimulationTime()

gs.sleep(8.0)
gs.setVisibility("element.labels", True)
gs.sleep(8.0)
gs.setVisibility("element.orbits", True)
gs.sleep(4.0)

# Gently zoom out for 30 seconds (30 * 60 frames)
gs.setCinematicCamera(True)
gs.setCameraSpeed(1.0)

start_frame = gs.getCurrentFrameNumber()
current_frame = start_frame

while current_frame - start_frame < 1800:
    gs.cameraForward(-0.5)
    gs.sleep(0.2)
    current_frame = gs.getCurrentFrameNumber()

# Finish! stop time
gs.stopSimulationTime()

# Scales back to normal
gs.setObjectSizeScaling("Mercury", 1.0)
gs.setObjectSizeScaling("Venus", 1.0)
gs.setObjectSizeScaling("Earth", 1.0)
gs.setObjectSizeScaling("Mars", 1.0)
gs.setObjectSizeScaling("Jupiter", 1.0)
gs.setObjectSizeScaling("Saturn", 1.0)
gs.setObjectSizeScaling("Uranus", 1.0)
gs.setObjectSizeScaling("Neptune", 1.0)
gs.setObjectSizeScaling("Pluto", 1.0)

# Orbits and labels off
gs.setVisibility("element.orbits", False)
gs.setVisibility("element.labels", False)

# Restore
gs.enableInput()
gs.maximizeInterfaceWindow()
