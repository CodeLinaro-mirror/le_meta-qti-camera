DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit linux-kernel-base deploy

PR = "r0"

DEPENDS = "rsync-native linux-msm-headers"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

FILESPATH   =+ "${WORKSPACE}:"
SRC_URI    +=  "file://vendor/qcom/opensource/camera-kernel/"
SRC_URI    +=  "file://start_camera_le"
SRC_URI    +=  "file://camera.service"
SRC_URI    +=  "file://camera_load.conf"

S = "${WORKDIR}/vendor/qcom/opensource/camera-kernel"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

do_compile() {
    cd ${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform  && \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=../../vendor/qcom/opensource/camera-kernel \
    ROOTDIR=${WORKSPACE}/ \
    MODULE_CAMERA=m \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/camera-kernel \
    OUT_DIR=${KERNEL_PREBUILT_PATH} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    INSTALL_MODULE_HEADERS=1 \
    ./build/build_module.sh
}

do_install() {
    install -d ${D}${sysconfdir}/initscripts
    install -d ${D}${systemd_unitdir}/system/multi-user.target.wants/
    install -m 755 ${WORKDIR}/start_camera_le ${D}${sysconfdir}/initscripts
    install -d ${D}/usr/lib/modules/
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/camera-kernel/camera.ko -D ${D}${libdir}/modules/camera.ko
    install -d ${D}/usr/include/media
    echo "Staging path -> " ${STAGING_KERNEL_BUILDDIR}
    install -m 0755 ${STAGING_KERNEL_BUILDDIR}/usr/include/camera/media/*.h -D ${D}${includedir}/media/
    install -m 0644 ${WORKDIR}/camera.service -D ${D}${systemd_unitdir}/system/camera.service
    install -m 0755 ${WORKDIR}/camera_load.conf -D ${D}${sysconfdir}/modules-load.d/camera_load.conf
    ln -sf ${systemd_unitdir}/system/camera.service ${D}${systemd_unitdir}/system/multi-user.target.wants/camera.service
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
FILES_${PN} += "${systemd_unitdir}/system/camera.service"
FILES_${PN} += "${systemd_unitdir}/system/multi-user.target.wants/camera.service"
FILES_${PN} += "${libdir}/modules/*"
