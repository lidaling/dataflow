package com.bblink.dataflow;

/**
 * Created by lidl on 12/21/15.
 */

public enum TOPIC {

    LOGINFLOW("back-portal-loginflowlog"),
    WECHAT_LOGINFLOW("back-portal-wechatlog");

    private String topicName;

    private TOPIC(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
