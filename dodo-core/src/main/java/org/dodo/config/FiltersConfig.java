package org.dodo.config;

import java.util.*;

/**
 * 过滤器配置
 * @author maxlim
 *
 */
public class FiltersConfig {
    private Set<String> includesList;
    private Map<String, String> parametersMap;

    public final static String PARAMETER_RATE_LIMIT_LIMITED = "rate-limit.limited";
    public final static String PARAMETER_TOKEN_RATE_LIMIT_CAPACITY = "token-rate-limit.capacity";
    public final static String PARAMETER_TOKEN_RATE_LIMIT_RATE_PER_SECOND = "token-rate-limit.rate-per-second";

    public FiltersConfig() {
        this.parametersMap = new HashMap<>();
    }

    public FiltersConfig(Set<String> includesList, Map<String, String> parametersMap) {
        this.includesList = includesList;
        this.parametersMap = parametersMap;
    }

    public FiltersConfig(String includes, String parameters) {
        this.setIncludesList(includes);
        this.setParametersMap(parameters);
    }

    public FiltersConfig setIncludesList(String includes) {
        Set<String> includesList = new HashSet<>();
        for (String filterName : includes.split(",")) {
            includesList.add(filterName.trim());
        }
        this.includesList = includesList;
        return this;
    }

    public Set<String> getIncludesList() {
        return Collections.unmodifiableSet(this.includesList);
    }

    public boolean isInclude(String filterName) {
        if(this.includesList == null || includesList.isEmpty()) return true;
        return this.includesList.contains(filterName);
    }

    public FiltersConfig setParametersMap(String parameters) {
        Map<String, String> parametersMap = new HashMap<>();
        for (String parameter : parameters.split(";")) {
            String kv[] = parameter.trim().split("=");
            parametersMap.put(kv[0].trim(), kv[1].trim());
        }
        if (this.parametersMap == null) this.parametersMap = parametersMap;
        else this.parametersMap.putAll(parametersMap);
        return this;
    }

    public <T>T getParameter(String key, T defaultValue, Class<T> returnType) {
        String value = this.parametersMap.get(key);
        if(value == null) return defaultValue;
        //类型转换，处理几个常用的就可以了
        if(returnType == int.class || returnType == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (returnType == long.class || returnType == Long.class) {
            return (T) Long.valueOf(value);
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return (T) Boolean.valueOf(value);
        }
        return (T) value;
    }

    @Override
    public String toString() {
        return "FiltersConfig{" +
                "includesList=" + includesList +
                ", parametersMap=" + parametersMap +
                '}';
    }
}
