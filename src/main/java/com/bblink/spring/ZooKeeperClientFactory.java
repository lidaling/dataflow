package com.bblink.spring;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;

/**
 * ZooKeeper client factory.  Caches the created objects.
 *
 * @author cfregly
 */
public class ZooKeeperClientFactory {
    // zookeeper
    public static final String ZK_CONFIG_ENSEMBLE = "zookeeper.config.ensemble";
    public static final String ZK_CONFIG_ROOT_PATH = "zookeeper.config.root.path";
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperClientFactory.class);
    private static final String DEFAULT_PATH_UNIX = "/env/config.properties";
    private static final String DEFAULT_PATH_WINDOWS = "c://config.properties";
    private static Properties env;

    public static final Cache<String, ZooKeeperClientCacheItem> cache = CacheBuilder.newBuilder().concurrencyLevel(64)
            .build();

    public static void loadProperties(String project) {
        String projectConfigPath = System.getProperty("user.home") + "/" + project + "/conf/config.properties";
        try {
            env = loadPropertiesFromFile(projectConfigPath);
        } catch (IOException e) {
            String path = DEFAULT_PATH_UNIX;
            if(SystemUtils.IS_OS_WINDOWS) {
                path = DEFAULT_PATH_WINDOWS;
            }
            System.out.println("load " + projectConfigPath + " error!!! try " + path);
            try {
                env = loadPropertiesFromFile(path);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private static Properties loadPropertiesFromFile(String path) throws IOException {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path);
            // load a properties file
            properties.load(input);
            return properties;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Get a started ZK client
     */
    public static CuratorFramework getStartedZKClient(String ensemble) {
        ZooKeeperClientCacheItem cachedItem = cache.getIfPresent(ensemble);
        if (cachedItem != null) {
            return cachedItem.client;
        }
        return createAndStartZKClient(ensemble);
    }

    public static void initializeAndStartZkConfigSource(String prefix) throws Exception {
        loadProperties(prefix);
        //String zkConfigEnsemble = DynamicPropertyFactory.getInstance().getStringProperty(ZK_CONFIG_ENSEMBLE, "127.0.0.1:2181").get();
        String zkConfigEnsemble = env.getProperty(ZK_CONFIG_ENSEMBLE, "127.0.0.1:2181");
        System.out.println("zookeeper address: " + zkConfigEnsemble);
        //String zkConfigRootPath = DynamicPropertyFactory.getInstance().getStringProperty(ZK_CONFIG_ROOT_PATH, "/ttpod/config").get();
        String zkConfigRootPath = env.getProperty(ZK_CONFIG_ROOT_PATH + "/" + prefix, "/TTPOD/CONFIG/" + prefix);
        System.out.println("Config path: " + zkConfigRootPath);
        // ZooKeeper Dynamic Override Properties
        CuratorFramework client = ZooKeeperClientFactory.getStartedZKClient(zkConfigEnsemble);

        if (client.getState() != CuratorFrameworkState.STARTED) {
            throw new RuntimeException("ZooKeeper located at " + zkConfigEnsemble + " is not started.");
        }

        ZooKeeperConfigurationSource zookeeperConfigSource = new ZooKeeperConfigurationSource(
                client, zkConfigRootPath);
        zookeeperConfigSource.start();

        DynamicWatchedConfiguration zookeeperDynamicConfig = new DynamicWatchedConfiguration(
                zookeeperConfigSource);

        // insert ZK DynamicConfig into the 2nd spot
        ((ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance()).addConfigurationAtIndex(
                zookeeperDynamicConfig, "zk dynamic override", 1);


    }

    /**
     * Create and start a zkclient if needed
     */
    private synchronized static CuratorFramework createAndStartZKClient(String ensemble) {
        ZooKeeperClientCacheItem cachedItem = cache.getIfPresent(ensemble);
        if (cachedItem != null) {
            return cachedItem.client;
        }

        CuratorFramework client = CuratorFrameworkFactory.newClient(ensemble,
                Integer.parseInt(env.getProperty("zookeeper.session.timeout", "15000")),
                Integer.parseInt(env.getProperty("zookeeper.connection.timeout", "5000")),
                //DynamicPropertyFactory.getInstance().getIntProperty("zookeeper.session.timeout", 15000).get(),
                //DynamicPropertyFactory.getInstance().getIntProperty("zookeeper.connection.timeout", 5000).get(),
                new ExponentialBackoffRetry(1000, 3));

        client.start();

        cache.put(ensemble, new ZooKeeperClientCacheItem(ensemble, client));

        logger.info("Created, started, and cached zk client [{}] for ensemble [{}]", client, ensemble);

        return client;
    }
}
