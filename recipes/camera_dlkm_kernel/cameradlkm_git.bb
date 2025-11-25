DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
inherit linux-kernel-base deploy
PR = "r0"

DEPENDS += "virtual/kernel "
#DEPENDS += "virtual/kernel securemsmdlkm-headers cameradlkm-headers"
#DEPENDS += "mmrm-kernel"
#DEPENDS += "synx-kernel synx-kernel-header"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
SRC_URI += "file://vendor/qcom/opensource/camera-kernel/"
S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"
KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"

EXT_MODULES = "${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")}"
INTERMEDIAT_KERNEL_PATH = "${WORKDIR}/out/${KERNEL_DEFCONFIG}"

do_configure[noexec] = "1"

do_compile[cleandirs] += "${INTERMEDIAT_KERNEL_PATH}"
do_compile[lockfiles] = "${TMPDIR}/build_modules.lock"
do_compile() {
    cd ${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform  && \
    BUILD_CONFIG=${KERNEL_BUILD_CONFIG} \
    EXT_MODULES=${EXT_MODULES} \
    ENABLE_DDK_BUILD=${DDK_BUILD} \
    TARGET_BOARD_PLATFORM=${BASEMACHINE}-le \
    VARIANT=${KERNEL_VARIANT} \
    OUT_DIR=${KERNEL_OUT_PATH}/ \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/camera-kernel \
    ./build/build_module.sh
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/camera.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/Module.symvers -D ${D}/${base_libdir}/modules/${KERNEL_VERSION}/camera-kernel/Module.symvers
    install -d ${D}/usr/include/dt-bindings
    install -m 0755 ${B}/dt-bindings/*.h -D ${D}${includedir}/dt-bindings/
}

FILES:${PN} += "${sysconfdir}/*"
FILES:${PN} += "${base_libdir}/modules/${KERNEL_VERSION}/*"
FILES:${PN} += "${includedir}/*"

do_deploy() {
    install -d ${DEPLOYDIR}/kernel_modules
    cp -rp ${B}/camera.ko ${DEPLOYDIR}/kernel_modules
}
addtask do_deploy after do_install
