DESCRIPTION = "QTI Camera drivers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit linux-kernel-base deploy

PR = "r0"

DEPENDS = "rsync-native"
DEPENDS += "bc-native bison-native"
DEPENDS += "virtual/kernel securemsm"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

FILESPATH   =+ "${WORKSPACE}:"
SRC_URI    =  "file://camera/vendor/qcom/opensource/camera-kernel/"
SRC_URI    +=  "file://start_camera_le"
SRC_URI    +=  "file://camera.service"
SRC_URI    +=  "file://camera_load.conf"

S = "${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

# Disable parallel make
PARALLEL_MAKE = ""

# Disable parallel make
PARALLEL_MAKE = "-j1"

do_compile[lockfiles] = "${TMPDIR}/build_modules.lock"

do_configure() {
	cp -f ${WORKSPACE}/camera/vendor/qcom/opensource/camera-kernel/Makefile.am ${WORKSPACE}/camera/vendor/qcom/opensource/camera-kernel/Makefile
}

do_compile() {

    echo ${KERNEL_BUILD_CONFIG}

    cd ${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform && \

        BUILD_CONFIG=${KERNEL_BUILD_CONFIG} \
        EXT_MODULES=../../camera/vendor/qcom/opensource/camera-kernel \
        ROOTDIR=${WORKSPACE}/ \
        MODULE_CAMERA=m \
        MODULE_OUT=${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel \
        KERNEL_KIT=${KERNEL_OUT_PATH}/ \
        OUT_DIR=temp_out_dir \
        KCFLAGS="-I${STAGING_DIR_TARGET}/usr/include" \
        KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
        INSTALL_MODULE_HEADERS=1 \
        ./build/build_module.sh
}

do_install() {
	install -d ${D}${sysconfdir}/initscripts
	install -d ${D}${systemd_unitdir}/system/multi-user.target.wants/
	install -m 755 ${WORKDIR}/start_camera_le ${D}${sysconfdir}/initscripts
	install -d ${D}/usr/lib/modules/
	install -d ${D}/usr/include/

        # strip debug symbols and sign the module
        ${STAGING_DIR_NATIVE}/usr/libexec/aarch64-oe-linux/gcc/aarch64-oe-linux/9.3.0/strip \
              --strip-debug ${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel/camera.ko

        LD_LIBRARY_PATH=${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform/prebuilts/kernel-build-tools/linux-x86/lib64/ \
        ${KERNEL_PREBUILT_PATH}/../msm-kernel/scripts/sign-file sha1 ${KERNEL_PREBUILT_PATH}/../msm-kernel/certs/signing_key.pem \
             ${KERNEL_PREBUILT_PATH}/../msm-kernel/certs/signing_key.x509 ${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel/camera.ko

	install -m 0755 ${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel/camera.ko -D ${D}${libdir}/modules/camera.ko
	cp -r ${WORKDIR}/camera/vendor/qcom/opensource/camera-kernel/include/uapi/ ${D}/usr/include/

	install -m 0644 ${WORKDIR}/camera.service -D ${D}${systemd_unitdir}/system/camera.service
	install -m 0755 ${WORKDIR}/camera_load.conf -D ${D}${sysconfdir}/modules-load.d/camera_load.conf
	ln -sf ${systemd_unitdir}/system/camera.service ${D}${systemd_unitdir}/system/multi-user.target.wants/camera.service
}


FILES_${PN} += "${sysconfdir}/*"
FILES_${PN} += "/etc/initscripts/start_camera_le"
FILES_${PN} += "${systemd_unitdir}/system/camera.service"
FILES_${PN} += "${systemd_unitdir}/system/multi-user.target.wants/camera.service"
FILES_${PN} += "${libdir}/modules/*"
