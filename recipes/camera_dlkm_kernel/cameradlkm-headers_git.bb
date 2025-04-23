DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPLv2.0-with-linux-syscall-note"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-bsp/files/common-licenses/${LICENSE};md5=8afb6abdac9a14cb18a0d6c9c151e9b4"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
do_compile[noexec] = "1"

SRC_URI    =  "file://vendor/qcom/opensource/camera-kernel/"
S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

do_install() {
	install -d ${D}/usr/include/
	install -d ${D}/usr/include/media
	install -m 0755 ${B}/include/uapi/camera/media/*.h -D ${D}${includedir}/media/
}
