inherit autotools

DESCRIPTION = "SYNX FRAMEWORK Headers"

LICENSE          = "GPLv2.0-with-linux-syscall-note"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-bsp/files/common-licenses/${LICENSE};md5=8afb6abdac9a14cb18a0d6c9c151e9b4"

DEFAULT_PREFERENCE = "-1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
do_compile[noexec] = "1"

SRC_URI    =  "file://vendor/qcom/opensource/camera-kernel/"
S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

do_install() {
    ## install synx kernel headers
    bbnote "includedir is set to: ${includedir}"
    install -d ${D}/${includedir}
    install -d -p ${D}${includedir}/dt-bindings
    install ${S}/dt-bindings/*.h ${D}${includedir}/dt-bindings
}
FILES:${PN} += "${base_libdir}/* ${includedir}/* "
