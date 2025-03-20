DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
inherit linux-kernel-base deploy
PR = "r0"

DEPENDS += "virtual/kernel"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
SRC_URI += "file://vendor/qcom/opensource/camera-kernel/"


S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"
EXT_MODULES = "${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")}"

do_configure[noexec] = "1"

do_compile[depends] += "virtual/kernel:do_shared_workdir"
do_compile[cleandirs] += "${WORKDIR}/out/${KERNEL_DEFCONFIG}"
do_compile() {
    cd ${KERNEL_PLATFORM_PATH}
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=${EXT_MODULES} \
    KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
    OUT_DIR=${WORKDIR}/out/${KERNEL_DEFCONFIG} \
    INPLACE_COMPILE=y \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    MODULE_CAMERA=m \
    ./build/build_module.sh
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/camera.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}

    install -m 0755 ${B}/Module.symvers -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/camera-kernel/Module.symvers

    install -d ${D}/usr/include/camera/media
    install -m 0755 ${B}/include/uapi/camera/media/*.h -D ${D}${includedir}/camera/media/

}

FILES:${PN} += "${sysconfdir}/*"
FILES:${PN} += "${base_libdir}/modules/${KERNEL_VERSION}/*"

do_deploy() {
    install -d ${DEPLOYDIR}/kernel_modules
    cp -rp ${B}/camera.ko ${DEPLOYDIR}/kernel_modules
}
addtask do_deploy after do_install
