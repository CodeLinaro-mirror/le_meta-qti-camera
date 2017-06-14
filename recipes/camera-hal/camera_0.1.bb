inherit autotools pkgconfig qlicense

DESCRIPTION = "MM Camera libraries for MSM/QSD"
SECTION  = "camera"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI   = "file://camera/lib"

SRCREV = "${AUTOREV}"
S      = "${WORKDIR}/lib"

SRC_DIR = "${WORKSPACE}/camera/lib"

DEPENDS = "glib-2.0 media"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_KERNEL_BUILDDIR}/usr/include"
EXTRA_OECONF += "--with-glib"
EXTRA_OECONF += "--with-common-includes=${STAGING_INCDIR}"

include camera-${BASEMACHINE}.inc

FILES_${PN}-dbg  = "${libdir}/.debug/*"
FILES_${PN}      = "${libdir}/*.so ${libdir}/*.so.* ${sysconfdir}/* ${libdir}/pkgconfig/* ${bindir}/* ${libdir}/hw/*.so"
FILES_${PN}-dev  = "${libdir}/*.la ${includedir}"
INSANE_SKIP_${PN} = "dev-so"
