package org.dodo.config.spring.bean;

import org.dodo.common.utils.StringUtils;
import org.dodo.config.ConfigManager;
import org.dodo.config.FiltersConfig;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author maxlim
 */
public class FiltersConfigBean extends FiltersConfig implements InitializingBean {
    private String includes;
    private String parameters;
    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(includes)) {
            super.setIncludesList(includes);
        }
        if (StringUtils.isNotBlank(parameters)) {
            super.setParametersMap(parameters);
        }
        ConfigManager.instance().setFiltersConfig(this);
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
