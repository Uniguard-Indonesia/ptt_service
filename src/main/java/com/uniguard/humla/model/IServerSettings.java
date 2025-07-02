package com.uniguard.humla.model;

public interface IServerSettings {
    boolean getAllowHtml();

    int getMessageLength();

    int getImageMessageLength();

    int getMaxBandwidth();

    int getMaxUsers();

    String getWelcomeText();
}
