SUMMARY = "QTI Camera Package Group"

LICENSE = "BSD-3-Clause"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = ' \
    packagegroup-qti-camera \
'

RDEPENDS_packagegroup-qti-camera = ' \
    ${@bb.utils.contains("COMBINED_FEATURES", "qti-camera", "camx-ext", "", d)} \
'

