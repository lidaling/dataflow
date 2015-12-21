package com.bblink.spring;

/*
 * Copyright (C) 2014 T (oss@capgemini.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;

/**
 * @author Muxi Zhang
 */
public class ArchaiusPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(ArchaiusPropertyPlaceholderConfigurer.class);
    private String prefix = null;

    @Override
    public String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        return DynamicPropertyFactory.getInstance().getStringProperty(placeholder, null).get();
    }

    public static void main(String[] args) throws Exception {
        ArchaiusPropertyPlaceholderConfigurer config = new ArchaiusPropertyPlaceholderConfigurer();
//        config.setPrefix("snappy-rest");
        config.afterPropertiesSet();
        String aa = config.resolvePlaceholder("brokers.ids.1.host", null, 0);
        System.out.println(aa + "\n" + aa);
    }

    public void afterPropertiesSet() throws Exception {
        try {
            ZooKeeperClientFactory.initializeAndStartZkConfigSource(prefix);
            DynamicPropertyFactory.initWithConfigurationSource(ConfigurationManager.getConfigInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
