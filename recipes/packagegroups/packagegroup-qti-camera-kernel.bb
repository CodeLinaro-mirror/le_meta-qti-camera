SUMMARY = "QTI Camera Kernel Package Group"

LICENSE          = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=7a434440b651f4a472ca93716d01033a"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = ' \
    packagegroup-qti-camera-kernel \
'

RDEPENDS:packagegroup-qti-camera-kernel = ' \
    cameradlkm \
    cameradtb \
'
