package com.bio.drqi.applet.contant;

public enum DeviceEnum {
    MIN_APP_DRQI("wxfdf5c2ae277168c0", "minAppDR");
    public String appId;
    public String device;

    DeviceEnum(String appId, String device) {
        this.appId = appId;
        this.device = device;
    }

    public static String getDevice(String appId) {
        for (DeviceEnum deviceEnum : DeviceEnum.values()) {
            if(deviceEnum.appId.equals(appId)){
                return deviceEnum.device;
            }
        }
        return null;
    }
}
