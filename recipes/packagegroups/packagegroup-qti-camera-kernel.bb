SUMMARY = "QTI Camera Kernel Package Group"

LICENSE = "BSD-3-Clause"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = ' \
    packagegroup-qti-camera-kernel \
'

RDEPENDS:packagegroup-qti-camera-kernel = ' \
      ${@'cameradlkm' if d.getVar('BASEMACHINE') == 'seraph' else bb.utils.contains("COMBINED_FEATURES", "qti-camera", "cameradlkm", "", d)} \
'
