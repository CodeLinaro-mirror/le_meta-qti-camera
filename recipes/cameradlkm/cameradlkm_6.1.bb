inherit module

HOMEPAGE         = "https://git.codelinaro.org"
LICENSE          = "GPLv2.0-with-linux-syscall-note"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-bsp/files/common-licenses/${LICENSE};md5=8afb6abdac9a14cb18a0d6c9c151e9b4"
DESCRIPTION = "QTI Camera drivers"

DEFAULT_PREFERENCE = "-1"

FILESPATH   =+ "${WORKSPACE}:"
SRC_URI     =  "file://vendor/qcom/opensource/camera-kernel"
SRC_URI    +=  "file://camera_load.conf"

S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

EXTRA_OEMAKE += "STAGING_INCDIR=${STAGING_INCDIR}"

DEPENDS = "rsync-native linux-msm-headers kernel-module-mmrm-kernel kernel-module-synx-kernel kernel-module-fastrpc-kernel libsynx"
DEPENDS += "synx-kernel-header"
DEPENDS:append:aarch64 = " libgcc"
RPROVIDES:${PN} += "kernel-module-camera-${KERNEL_VERSION}"
KERNEL_MODULES = "camera"
EXTRA_OEMAKE += "M=${S}"
EXTRA_OEMAKE += "TAR=${PACKAGE_ARCH}"
KERNEL_CC = "${STAGING_BINDIR_NATIVE}/clang/bin/clang -target ${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}"
MAKE_TARGETS = "modules"

do_install() {
    install -d ${D}${includedir}/media/
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/
    install -d ${D}${sysconfdir}/
    install -m 0755 ${WORKDIR}/camera_load.conf -D ${D}${sysconfdir}/modules-load.d/camera_load.conf
    install -m 0755 ${S}/camera.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/
    install -m 0644 ${S}/include/uapi/camera/media/*.h -D ${D}${includedir}/media/
}

FILES:${PN} = "${sysconfdir}/*"
FILES:${PN} += "${base_libdir}/*"
