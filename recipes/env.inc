DEPENDS += "system-core-headers"
DEPENDS += "llvm-arm-toolchain-native"
DEPENDS += "glib-2.0"
DEPENDS += "libutils"
DEPENDS += "libcutils"
DEPENDS += "libsync"

OECMAKE_C_COMPILER       = "${STAGING_DIR_NATIVE}/usr/bin/llvm-arm-toolchain/bin/clang"
OECMAKE_CXX_COMPILER     = "${STAGING_DIR_NATIVE}/usr/bin/llvm-arm-toolchain/bin/clang++"
CAMX_FLAGS               = "-target ${TARGET_SYS} "
CAMX_FLAGS              += "-I ${STAGING_INCDIR}/c++ "
#TODO remove EXTERNAL_TOOLCHAIN
CAMX_FLAGS              += "-L ${EXTERNAL_TOOLCHAIN}/deb/usr/lib/gcc-cross/aarch64-linux-gnu/7/ "
OECMAKE_C_FLAGS         += "${CAMX_FLAGS}"
OECMAKE_CXX_FLAGS       += "${CAMX_FLAGS}"
#OECMAKE_C_LINK_FLAGS   += "-Wl,--no-as-needed"
#OECMAKE_CXX_LINK_FLAGS += "-Wl,--no-as-needed"
AARCH64_LIB_PATH         = "${STAGING_DIR_HOST}/usr/lib/aarch64-linux-gnu"

def get_platform(d):
    if d.getVar('MACHINE', True) == 'qrb5165-rb5':
        return "kona"
    else:
        return ""

EXTRA_OECMAKE = "\
        -DCAMXDEBUG:STRING=True \
        -DPLATFORM:STRING=linux \
        -DCPU:STRING=64 \
        -DCMAKE_C_FLAGS:STRING="${OECMAKE_C_FLAGS}" \
        -DCMAKE_CXX_FLAGS:STRING="${OECMAKE_CXX_FLAGS}" \
        -DCMAKE_CROSSCOMPILING:BOOL=True \
        -DCMAKE_C_COMPILER:STRING=${OECMAKE_C_COMPILER} \
        -DCMAKE_CXX_COMPILER:STRING=${OECMAKE_CXX_COMPILER} \
        -DKERNEL_INCDIR=${STAGING_KERNEL_BUILDDIR} \
        -DCMAKE_LIBRARY_PATH:PATH=${STAGING_LIBDIR} \
        -DCMAKE_INCLUDE_PATH:PATH=${STAGING_INCDIR} \
        -DCMAKE_SYSROOT:PATH=${STAGING_DIR_HOST} \
        -DTARGET_PLATFORM:STRING=${@get_platform(d)} \
"

addtask do_setToolChain after do_prepare_recipe_sysroot before do_configure
do_setToolChain() {
    echo "WORKSPACE               = ${WORKSPACE}"
    echo "WORKDIR                 = ${WORKDIR}"
    echo "OECMAKE_C_COMPILER      = ${OECMAKE_C_COMPILER}"
    echo "OECMAKE_CXX_COMPILER    = ${OECMAKE_CXX_COMPILER}"
    echo "OECMAKE_C_FLAGS         = ${OECMAKE_C_FLAGS}"
    echo "OECMAKE_CXX_FLAGS       = ${OECMAKE_CXX_FLAGS}"
    echo "STAGING_LIBDIR          = ${STAGING_LIBDIR}"
    echo "STAGING_DIR_HOST        = ${STAGING_DIR_HOST}"
    echo "STAGING_KERNEL_BUILDDIR = ${STAGING_KERNEL_BUILDDIR}"
    echo "STAGING_INCDIR          = ${STAGING_INCDIR}"
    echo "libdir                  = ${libdir}"
    echo "TARGET_SYS              = ${TARGET_SYS}"
    echo "EAT_TARGET_SYS          = ${EAT_TARGET_SYS}"

    #TODO remove EXTERNAL_TOOLCHAIN
    ln -sf ${EXTERNAL_TOOLCHAIN}/deb/usr/bin/aarch64-linux-gnu-as                       ${STAGING_DIR_NATIVE}/usr/bin/llvm-arm-toolchain/bin/as
    ln -sf ${EXTERNAL_TOOLCHAIN}/deb/usr/bin/aarch64-linux-gnu-ld                       ${STAGING_DIR_NATIVE}/usr/bin/llvm-arm-toolchain/bin/ld
    ln -sf ${EXTERNAL_TOOLCHAIN}/deb/usr/bin/aarch64-linux-gnu-gcc                      ${STAGING_DIR_NATIVE}/usr/bin/llvm-arm-toolchain/bin/gcc
    ln -sf ${EXTERNAL_TOOLCHAIN}/deb/usr/lib/gcc-cross/aarch64-linux-gnu/7/libgcc.a     ${AARCH64_LIB_PATH}/libgcc.a

    ln -sf ${AARCH64_LIB_PATH}/libstdc++.so.6                                           ${AARCH64_LIB_PATH}/libstdc++.so
    ln -sf ${AARCH64_LIB_PATH}/libm.so.6                                                ${AARCH64_LIB_PATH}/libm.so
    ln -sf ${AARCH64_LIB_PATH}/libgcc_s.so.1                                            ${AARCH64_LIB_PATH}/libgcc_s.so
    ln -sf ${AARCH64_LIB_PATH}/ld-2.27.so                                               ${AARCH64_LIB_PATH}/libdl.so
    rm -rf ${AARCH64_LIB_PATH}/libdl.a
}
