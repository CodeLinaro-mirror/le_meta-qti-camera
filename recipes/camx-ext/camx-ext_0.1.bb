inherit cmake pkgconfig

DESCRIPTION = "Recipe to provide Camera external library"
HOMEPAGE = "http://developer.android.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESPATH =+ "${WORKSPACE}/vendor/qcom/opensource/:"
SRC_URI =+ "file://camx-ext"

include ../env.inc

S = "${WORKDIR}/camx-ext"

do_install_append() {
     rm -rf ${D}${includedir}/hardware/hardware.h
     rm -rf ${D}${includedir}/hardware/power.h
     rm -rf ${D}${includedir}/hardware/fb.h  
#     rm -rf ${D}${includedir}/hardware/gralloc1.h  
#     rm -rf ${D}${includedir}/hardware/gralloc.h
     rm -rf ${D}${includedir}/camera/CameraMetadata.h
     rm -rf ${D}${includedir}/camera/VendorTagDescriptor.h
} 

FILES_${PN}        += "${libdir}/*.so"
FILES_${PN}-dbg    += "${libdir}/.debug/"
FILES_SOLIBSDEV     = ""
INSANE_SKIP_${PN}   = "dev-so"
