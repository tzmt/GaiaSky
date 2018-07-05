# This script tests the various coordinate conversion utilities in the scripting API. To be run asynchronously.
# Created by Toni Sagrista

from gaia.cu9.ari.gaiaorbit.script import EventScriptingInterface
from gaia.cu9.ari.gaiaorbit.util import GlobalConf

gs = EventScriptingInterface.instance()

gs.disableInput()
gs.cameraStop()
gs.minimizeInterfaceWindow()

gs.setRotationCameraSpeed(40)
gs.setTurningCameraSpeed(30)
gs.setCameraSpeed(30)

gs.setCameraFocus("Sol")

au_to_km = 149597900.0
text_x = 0.2
text_y = 0.2
# Text size scaling (HiDPI)
text_size = 22 * GlobalConf.SCALE_FACTOR

# Disable all grids
gs.setVisibility("element.equatorial", False)
gs.setVisibility("element.ecliptic", False)
gs.setVisibility("element.galactic", False)

#
# EQUATORIAL COORDINATES
#
gs.displayMessageObject(0, "Equatorial coordinates test", text_x, text_y, 1.0, 0.3, 0.3, 1.0, text_size)
# Enable equatorial grid
gs.setVisibility("element.equatorial", True)
gs.sleep(1.5)

# Go to north equatorial pole, looking down on the Sun
eqxyz = gs.equatorialToInternalCartesian(0, 90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Go to south equatorial pole
eqxyz = gs.equatorialToInternalCartesian(0, -90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Equatorial plane
eqxyz = gs.equatorialToInternalCartesian(90, 0, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

gs.removeObject(0)
# Disable equatorial grid
gs.setVisibility("element.equatorial", False)
gs.sleep(1.5)

#
# ECLIPTIC COORDINATES
#
gs.displayMessageObject(0, "Ecliptic coordinates test", text_x, text_y, 0.3, 1.0, 0.3, 1.0, text_size)
# Enable equatorial grid
gs.setVisibility("element.ecliptic", True)
gs.sleep(1.5)

# Go to north equatorial pole, looking down on the Sun
eqxyz = gs.eclipticToInternalCartesian(0, 90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Go to south equatorial pole
eqxyz = gs.eclipticToInternalCartesian(0, -90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Equatorial plane
eqxyz = gs.eclipticToInternalCartesian(90, 0, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

gs.removeObject(0)
# Disable equatorial grid
gs.setVisibility("element.ecliptic", False)
gs.sleep(1.5)


#
# GALACTIC COORDINATES
#
gs.displayMessageObject(0, "Galactic coordinates test", text_x, text_y, 0.3, 0.3, 1.0, 1.0, text_size)
# Enable equatorial grid
gs.setVisibility("element.galactic", True)
gs.sleep(1.5)

# Go to north equatorial pole, looking down on the Sun
eqxyz = gs.galacticToInternalCartesian(0, 90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Go to south equatorial pole
eqxyz = gs.galacticToInternalCartesian(0, -90, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

# Equatorial plane
eqxyz = gs.galacticToInternalCartesian(90, 0, 8 * au_to_km)
gs.setCameraPosition(eqxyz)

# Sleep
gs.sleep(3.0)

gs.removeObject(0)
# Disable equatorial grid
gs.setVisibility("element.galactic", False)
gs.sleep(1.5)