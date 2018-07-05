# This script tests the planetarium, 360 and stereo mode commands
# Created by Toni Sagrista

from gaia.cu9.ari.gaiaorbit.script import EventScriptingInterface

from gaia.cu9.ari.gaiaorbit.event import EventManager
from gaia.cu9.ari.gaiaorbit.event import Events


gs = EventScriptingInterface.instance()

gs.setCameraFocus("Sol")
gs.sleep(3)

gs.setCinematicCamera(True)
gs.cameraRotate(1.0, 0.0)

print("Bloom")
gs.setBloom(100.0)
gs.sleep(4)
gs.setBloom(50.0)
gs.sleep(4)
gs.setBloom(10.0)
gs.sleep(4)

print("Star glow")
gs.setStarGlow(False)
gs.sleep(4)
gs.setStarGlow(True)
gs.sleep(4)


print("Motion blur")
gs.setMotionBlur(True)
gs.sleep(4)
gs.setMotionBlur(False)
gs.sleep(4)


print("Lens flare")
gs.setLensFlare(True)
gs.sleep(4)
gs.setLensFlare(False)
gs.sleep(4)
gs.setLensFlare(True)

gs.setCinematicCamera(False)
