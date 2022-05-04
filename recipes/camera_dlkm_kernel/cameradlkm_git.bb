DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit linux-kernel-base deploy

PR = "r0"

DEPENDS = "rsync-native linux-msm-headers"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

FILESPATH   =+ "${WORKSPACE}:"
SRC_URI    +=  "file://vendor/qcom/opensource/camera-kernel/"
SRC_URI    +=  "file://kernel-5.10/kernel_platform"
SRC_URI    +=  "file://kernel-5.10/out/${KERNEL_DEFCONFIG}"
SRC_URI    +=  "file://camera_load.conf"

S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

do_compile() {
    cd ${WORKDIR}/kernel-5.10/kernel_platform  && \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=../../vendor/qcom/opensource/camera-kernel \
    ROOTDIR=${WORKDIR}/ \
    MODULE_CAMERA=m \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/camera-kernel \
    OUT_DIR=${WORKDIR}/kernel-5.10/out/${KERNEL_DEFCONFIG} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    INSTALL_MODULE_HEADERS=1 \
    ./build/build_module.sh
}

do_install() {
    KERNEL_VERSION="${@oe.utils.read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"
    bbnote "Kernel Version: \"${KERNEL_VERSION}\""
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/camera-kernel/camera.ko -D ${D}/${nonarch_base_libdir}/modules/${KERNEL_VERSION}/camera.ko
    install -d ${D}/usr/include/media
    install -m 0755 ${STAGING_KERNEL_BUILDDIR}/usr/include/camera/media/*.h -D ${D}${includedir}/media/
    install -m 0755 ${WORKDIR}/camera_load.conf -D ${D}${sysconfdir}/modules-load.d/camera_load.conf
}

do_deploy() {
# Deploy unstripped kernel modules into ${DEPLOYDIR}/kernel_modules for debugging purposes
    install -d ${DEPLOYDIR}/kernel_modules
    for kmod in $(find ${D} -name "*.ko") ; do
        install -m 0644 $kmod ${DEPLOYDIR}/kernel_modules
    done
}

addtask deploy after do_install before do_package

FILES_${PN} += "${sysconfdir}/*"
FILES_${PN} += "${nonarch_base_libdir}/modules/*"
