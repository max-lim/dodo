package org.dodo.consumer.invoker;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 其他Invoker接口实现类的装饰器，包装了过滤器的功能
 * @author maxlim
 *
 */
public class InvokerWrapper implements Invoker {
	private static final Logger logger = LoggerFactory.getLogger(InvokerWrapper.class);
    private Invoker invoker;

	public void setInvoker(Invoker invoker) {
		//先构建后置链，拿到后置的头节点，把该节点作为前置的尾，接入前置链
		Invoker postHead = buildPostChain(invoker, filtersByGroupAndOrder(FilterGroup.CONSUMER_POST));
		Invoker frontHead = buildFrontChain(postHead, filtersByGroupAndOrder(FilterGroup.CONSUMER_FRONT));
		this.invoker = frontHead;
    }

	protected List<Filter> filtersByGroupAndOrder(FilterGroup group) {
		List<Filter> filters = new ArrayList<>(SpiLoader.getExtensionHolder(Filter.class).getAll()).stream()
				.filter(filter -> {
					if( ! filter.getClass().isAnnotationPresent(FilterAttribute.class)) {
						logger.warn("the annotation:{} of filter:{} is not present", FilterAttribute.class.getName(), filter.getClass().getName());
						return false;
					}
					return filter.getClass().getAnnotation(FilterAttribute.class).group() == group && ConfigManager.instance().getFiltersConfig().isInclude(filter.getName());
				}).collect(Collectors.toList());
		filters.sort(Comparator.comparingInt(filter -> filter.getClass().getAnnotation(FilterAttribute.class).order()));
		return filters;
	}

	/**
	 * 构建 rpc invoker 前置过滤器链
	 * @param last 前置的结尾
	 * @param filters
	 * @return 前置链头
	 */
	protected Invoker buildFrontChain(Invoker last, List<Filter> filters) {
    	if(filters == null || filters.isEmpty()) return last;
		for (int i = filters.size() - 1; i >= 0; i--) {
			Filter filter = filters.get(i);
			Invoker next = last;
			last = invokerRequest -> filter.invoke(invokerRequest, next);
		}
		return last;
	}

	/**
	 * 构建 rpc invoker 后置过滤器链
	 * @param first 后置的开头
	 * @param filters
	 * @return 被封装过的后置链头
	 */
	protected Invoker buildPostChain(Invoker first, List<Filter> filters) {
		if(filters == null || filters.isEmpty()) return first;
		Invoker last = null;
		for (int i = filters.size() - 1; i >= 0; i--) {
			Filter filter = filters.get(i);
			Invoker next = last;
			last = invokerRequest -> filter.invoke(invokerRequest, next);
		}
		Invoker firstFilter = last;
		return invokerRequest -> {
			Object result = first.invoke(invokerRequest);
			//忽略后置过滤器的返回值
			firstFilter.invoke(invokerRequest);
			return result;
		};
	}

	@Override
	public Object invoke(InvokerRequest invokerRequest) throws Exception {
		return invoker.invoke(invokerRequest);
	}
}
