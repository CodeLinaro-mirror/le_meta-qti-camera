SUMMARY = "QTI Camera Kernel Package Group"

LICENSE = "BSD-3-clause-clear"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = ' \
    packagegroup-qti-camera-kernel \
'

RDEPENDS_packagegroup-qti-camera-kernel = ' \
    cameradlkm \
'
DEPENDS += " cameradevicetree"
