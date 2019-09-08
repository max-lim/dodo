package org.dodo.config.spring;

import org.dodo.config.MethodConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * spring xml自定义bean解析
 * @author maxlim
 *
 */
public class DodoBeanDefinitionParser implements BeanDefinitionParser {
    private final Class beanClass;
    private boolean requiredId = false;
    public DodoBeanDefinitionParser(Class beanClass) {
        this(beanClass, false);
    }
    public DodoBeanDefinitionParser(Class beanClass, boolean requiredId) {
        this.beanClass = beanClass;
        this.requiredId = requiredId;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, requiredId);
    }

    public BeanDefinition parse(Element element, ParserContext parserContext, Class beanClass, boolean requiredId) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        NamedNodeMap namedNodeMap = element.getAttributes();
        Node idNode = Optional.ofNullable(namedNodeMap.getNamedItem("id")).orElse(namedNodeMap.getNamedItem("name"));
        idNode = Optional.ofNullable(idNode).orElse(namedNodeMap.getNamedItem("class"));
        if (idNode == null && requiredId) {
            throw new IllegalArgumentException("the element("+element.getNodeName()+") has not defined id or name or class");
        }
        String id = idNode == null ? beanClass.getName() : idNode.getNodeValue();
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalArgumentException("duplicate bean had be found:" + id);
        }
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
//        beanDefinition.getPropertyValues().addPropertyValue("id", id);

        for (Method method : beanClass.getMethods()) {
            if ( ! method.getName().startsWith("set")
                    && ! Modifier.isPublic(method.getModifiers())
                    && method.getParameterCount() != 1) {
                continue;
            }
            String property = (method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4));
            if ("id".equals(property)) {
                continue;
            }
            String value = element.getAttribute(property);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            Object reference;
            if ("ref".equals(property)) {
                if (parserContext.getRegistry().containsBeanDefinition(value)) {
                    BeanDefinition beanDefinitionRef = parserContext.getRegistry().getBeanDefinition(value);
                    if ( ! beanDefinitionRef.isSingleton()) {
                        throw new IllegalArgumentException("please redefined the bean " +value+ " is single");
                    }
                }
                reference = new RuntimeBeanReference(value);
            }
            else if ("methods".equals(property)) {
                parseMethods(id, element.getChildNodes(), beanDefinition, parserContext, beanClass);
                continue;
            }
            else {
                reference = new TypedStringValue(value);
            }
            beanDefinition.getPropertyValues().addPropertyValue(property, reference);
        }
        return beanDefinition;
    }

    private void parseMethods(String id, NodeList nodeList, BeanDefinition beanDefinition, ParserContext parserContext, Class beanClass) {
        if (nodeList != null && nodeList.getLength() > 0) {
            ManagedList managedList = new ManagedList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("methods".equals(element.getLocalName()) && "methods".equals(element.getNodeName())) {
                        String name = Optional.ofNullable(element.getAttribute("name")).orElseThrow(()->new IllegalArgumentException("name cannot be null of method under the node " + id));
                        BeanDefinition beanDefinitionMethod = parse(element, parserContext, MethodConfig.class, true);
                        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinitionMethod, id+"."+name);
                        managedList.add(beanDefinitionHolder);
                    }
                }
            }
            if ( ! managedList.isEmpty()) {
                beanDefinition.getPropertyValues().addPropertyValue("methods", managedList);
            }
        }
    }
}
