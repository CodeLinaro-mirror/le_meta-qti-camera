DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
inherit linux-kernel-base deploy
PR = "r0"

DEPENDS += "virtual/kernel securemsmdlkm-headers cameradlkm-headers"
DEPENDS += "mmrm-kernel"
DEPENDS += "synx-kernel synx-kernel-header"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
SRC_URI += "file://vendor/qcom/opensource/camera-kernel/"
S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"
KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"

EXT_MODULES = "${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")}"
INTERMEDIAT_KERNEL_PATH = "${WORKDIR}/out/${KERNEL_DEFCONFIG}"

do_configure[noexec] = "1"

do_compile[cleandirs] += "${INTERMEDIAT_KERNEL_PATH}"
do_compile() {
    ## cflag for extra include directory ##
    LE_EXTRA_CFLAGS="-I${STAGING_DIR_HOST}/usr/include -I${STAGING_DIR_HOST}/usr/include/linux"

    ## compile module ##
    cd ${KERNEL_PLATFORM_PATH}

    KBUILD_OPTIONS+="TARGET_SYNX_ENABLE=y" \
    LE_EXTRA_CFLAGS="${LE_EXTRA_CFLAGS}" \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=${EXT_MODULES} \
    KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
    OUT_DIR=${INTERMEDIAT_KERNEL_PATH} \
    INPLACE_COMPILE=y \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    MODULE_CAMERA=m \
    ./build/build_module.sh \
    KBUILD_EXTRA_SYMBOLS=${STAGING_DIR_HOST}/lib/modules/${KERNEL_VERSION}/mmrm-kernel/Module.symvers \
    KBUILD_EXTRA_SYMBOLS+=${WORKDIR}/recipe-sysroot/lib/modules/${KERNEL_VERSION}/synx-kernel/Module.symvers
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/camera.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/Module.symvers -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/camera-kernel/Module.symvers
    install -d ${D}/usr/include/dt-bindings
    install -m 0755 ${B}/dt-bindings/*.h -D ${D}${includedir}/dt-bindings/
#    install -m 0644 ${S}/camera-kernel.rules -D ${D}${sysconfdir}/udev/rules.d/camera-kernel.rules
}

FILES:${PN} += "${sysconfdir}/*"
FILES:${PN} += "${base_libdir}/modules/${KERNEL_VERSION}/*"
FILES:${PN} += "${includedir}/*"

do_deploy() {
    install -d ${DEPLOYDIR}/kernel_modules
    cp -rp ${B}/camera.ko ${DEPLOYDIR}/kernel_modules
}
addtask do_deploy after do_install
