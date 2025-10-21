DESCRIPTION = "QTI Camera devicetree"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"
inherit linux-kernel-base deploy
PR = "r0"
DEPENDS += "virtual/kernel"
DEPENDS += "cameradlkm"
DEPENDS += "rsync-native"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
SRC_URI     =  "file://vendor/qcom/opensource/camera-devicetree/"
S = "${WORKDIR}/vendor/qcom/opensource/camera-devicetree"
KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"
EXT_MODULES = "${@os.path.relpath("${S}", "${KERNEL_PLATFORM_PATH}")}"
INTERMEDIAT_KERNEL_PATH = "${WORKDIR}/out/${KERNEL_DEFCONFIG}"
do_configure[noexec] = "1"
do_compile[depends] += "virtual/kernel:do_shared_workdir"
do_compile[cleandirs] += "${INTERMEDIAT_KERNEL_PATH}"
do_compile() {
    cd ${KERNEL_PLATFORM_PATH}
    BUILD_CONFIG=soc-repo/${KERNEL_CONFIG} \
    EXT_MODULES=${EXT_MODULES} \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/camera-devicetree \
    INPLACE_COMPILE=y \
    KBUILD_OPTIONS+="KERNAL_HEADER_DIR=${RECIPE_SYSROOT}/usr/include" \
    KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
    ROOTDIR=${KERNEL_PLATFORM_PATH}/ \
    OUT_DIR=${WORKDIR}/out/${KERNEL_DEFCONFIG} \
    ./build/build_module.sh dtbs
}
do_deploy() {
    install -d ${DEPLOYDIR}/tech_dtbs/
    install -m 0644 ${B}/*.dtbo ${DEPLOYDIR}/tech_dtbs/
}
ALLOW_EMPTY:${PN} = "1"
addtask do_deploy after do_install
