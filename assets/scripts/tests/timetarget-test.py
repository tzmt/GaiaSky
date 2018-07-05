# Test script. Tests brightness and contrast commands.
# Created by Toni Sagrista

from gaia.cu9.ari.gaiaorbit.script import EventScriptingInterface
from time import sleep
from datetime import datetime

gs = EventScriptingInterface.instance()

gs.disableInput()
gs.cameraStop()

# Set free camera
gs.setCameraFree()
# Starting time
gs.setSimulationTime(2017, 12, 1, 10, 5, 0, 0)

gs.sleep(2)

# Target time, roughly 48000 years into the future
gs.setTargetTime(50000, 1, 1, 10, 5, 0, 0)
# Fast pace
gs.setSimulationPace(2.7e11)
# Start!
gs.startSimulationTime()

while(gs.isSimulationTimeOn()):
    sleep(0.2)

timearr = gs.getSimulationTimeArr()
print("%s - Time is:        %i/%i/%i %i:%i:%i.%i" % (datetime.now().time(), timearr[2], timearr[1], timearr[0], timearr[3], timearr[4], timearr[5], timearr[6]))
print("%s - Time should be: 1/1/50000 10:05:00.000" % datetime.now().time())
gs.sleep(2)

# Back to 2017
gs.setTargetTime(2017, 12, 1, 10, 5, 0, 0)
# Backwards now!
gs.setSimulationPace(-2.7e11)
# Start!
gs.startSimulationTime()

while(gs.isSimulationTimeOn()):
    sleep(0.2)
    
print("Time should now be 1/12/2017 10:05:00.000")
gs.sleep(2)

# Never forget to unset the target time, otherwise Gaia Sky will always stop at that time bookmark!
gs.unsetTargetTime()

gs.enableInput()
