DESCRIPTION = "QTI Camera drivers"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

inherit linux-kernel-base deploy
CLEANBROKEN = "1"

PR = "r0"

FILESPATH   =+ "${WORKSPACE}:"
SRC_URI     =  "file://camera/vendor/qcom/opensource/camera-devicetree/"

S = "${WORKDIR}camera/vendor/qcom/opensource/camera-devicetree"

do_configure[depends] = "virtual/kernel:do_shared_workdir"

KERNEL_VERSION = "${@get_kernelversion_headers('${STAGING_KERNEL_BUILDDIR}')}"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

# Disable parallel make
PARALLEL_MAKE = ""

# Disable parallel make
PARALLEL_MAKE = "-j1"

do_compile[lockfiles] = "${TMPDIR}/build_modules.lock"

do_configure () {
	:
}

do_compile() {
    cd ${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform && \

        BUILD_CONFIG=${KERNEL_BUILD_CONFIG} \
        EXT_MODULES=../../camera/vendor/qcom/opensource/camera-devicetree \
        ROOTDIR=${WORKSPACE}/ \
        MODULE_OUT=${WORKDIR}/camera/vendor/qcom/opensource/camera-devicetree \
        KERNEL_KIT=${KERNEL_OUT_PATH}/ \
        OUT_DIR=temp_out_dir \
        KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
        ./build/build_module.sh
}

do_deploy() {
	install -d ${DEPLOYDIR}/build-artifacts/techpack-dtbos
	cp -a \
	${WORKDIR}/camera/vendor/qcom/opensource/camera-devicetree/*.dtbo \
	${DEPLOYDIR}/build-artifacts/techpack-dtbos/
}

addtask do_deploy after do_install

FILES_${PN} += "${sysconfdir}/*"
FILES_${PN} += "/usr/lib/modules/${KERNEL_VERSION}/camera/vendor/qcom/opensource/camera-kernel/drivers/*"
