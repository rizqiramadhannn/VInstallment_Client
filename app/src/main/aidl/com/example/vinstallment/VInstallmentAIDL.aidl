// VInstallmentAIDL.aidl
package com.example.vinstallment;

// Declare any non-default types here with import statements

interface VInstallmentAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void showNotif(String title, String subtitle, int id);
    void stopPlaying();
    void startPlaying();
    void removeNotif();
    void disableCamera(boolean status);
    void suspendApps(boolean status);
    void installmentComplete();
    void firstPunishmentStatus(boolean status);
    boolean getFirstPunishmentStatus();
    void secondPunishmentStatus(boolean status);
    boolean getSecondPunishmentStatus();
    void thirdPunishmentStatus(boolean status);
    boolean getThirdPunishmentStatus();
}