package org.dodo.spring.boot.starter.config;

import org.dodo.config.ConfigManager;
import org.dodo.config.spring.bean.ProviderServerConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.server")
public class ProviderServerConfigProperties extends ProviderServerConfigBean {
    private Map<String, String> groups;
    private Map<String, ProviderServerConfigBean> configs;
    @Override
    public void afterPropertiesSet() throws Exception {
        if (configs != null) {
            configs.forEach((name,config)->ConfigManager.instance().addProviderServerConfig(config));
        }
    }

    private void split(Map<String, String> groups) {
        configs = new HashMap<>();
        List<String> groupKeys = new ArrayList<>(groups.size()/5);

        groups.entrySet().stream().filter(entry->entry.getKey().endsWith(".name"))
                .forEach(entry -> groupKeys.add(entry.getValue()));

        for (String groupKey : groupKeys) {
            try {
                ProviderServerConfigBean bean = new ProviderServerConfigBean();
                bean.setName(groupKey);
                bean.setIp(groups.get(groupKey + ".ip"));
                bean.setPort(Integer.valueOf(groups.get(groupKey + ".port")));
                bean.setCorePoolSize(Integer.valueOf(groups.get(groupKey + ".corePoolSize")));
                bean.setMaxPoolSize(Integer.valueOf(groups.get(groupKey + ".maxPoolSize")));
                bean.setWorkQueueSize(Integer.valueOf(groups.get(groupKey + ".workQueueSize")));
                if (groups.containsKey(groupKey + ".protocol")) {
                    bean.setProtocol(groups.get(groupKey + ".protocol"));
                }
                if (groups.containsKey(groupKey+".accepts")) {
                    bean.setAccepts(Integer.valueOf(groups.get(groupKey + ".accepts")));
                }
                if (groups.containsKey(groupKey+".serialization")) {
                    bean.setSerialization(groups.get(groupKey + ".serialization"));
                }
                configs.put(groupKey, bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, String> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, String> groups) {
        this.groups = groups;
        split(groups);
    }
}
