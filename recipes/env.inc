DEPENDS += "system-core"
DEPENDS += "llvm-arm-toolchain-native"
DEPENDS += "glib-2.0"
DEPENDS += "libutils"
DEPENDS += "libcutils"
DEPENDS += "libsync"

CAMX_FLAGS               = " -I${STAGING_INCDIR}/c++/ "
#CAMX_FLAGS               = "-target ${TARGET_SYS} "
CAMX_FLAGS               += "-I ${STAGING_INCDIR}/c++ "
OECMAKE_C_FLAGS_append   = "${CAMX_FLAGS}"
OECMAKE_CXX_FLAGS_append = "${CAMX_FLAGS}"
#TARGET_SYS               = "aarch64-linux-gnu"

def get_platform(d):
    if d.getVar('BASEMACHINE', True) == 'sm8250':
        return "kona"
    else:
        return ""

EXTRA_OECMAKE = "\
        -DCAMXDEBUG:STRING=True \
        -DPLATFORM:STRING=linux \
        -DCPU:STRING=64 \
        -DCMAKE_CROSSCOMPILING:BOOL=True \
        -DCMAKE_C_COMPILER:STRING=${OECMAKE_C_COMPILER} \
	-DTARGET_PLATFORM:STRING=${@get_platform(d)} \
	-DCMAKE_TARGET_DIR:PATH=/usr/lib64/ \
"
