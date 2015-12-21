package com.bblink.spring;

import org.apache.curator.framework.CuratorFramework;


public class ZooKeeperClientCacheItem {
    public final String ensemble;
    public final CuratorFramework client;

    public ZooKeeperClientCacheItem(String ensemble, CuratorFramework client) {
        this.ensemble = ensemble;
        this.client = client;
    }
}
