IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'qti-camera', 'camx-ext', '', d)}"

copy_edge_cam_ext_prop_rpm() {
    cp ${DEPLOY_DIR_RPM}/aarch64/camx-ext-0.1-r0.aarch64.rpm ${IMAGE_ROOTFS}/packages/aarch64/
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'qti-camera', 'copy_edge_cam_ext_prop_rpm;', '', d)}"
