package org.nutz.jcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;

import org.nutz.aop.MethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 本集成所的aop配置
 * @author wendal(wendal1985@gmail.com)
 *
 */
@IocBean(name="$aop_cache")
public class NutCacheAopConfigure implements AopConfigration {

    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
        List<InterceptorPair> list = new ArrayList<InterceptorPair>();
        boolean flag = true;
        for (Method method : clazz.getMethods()) {
            if (method.getAnnotation(CachePut.class) != null
                    || method.getAnnotation(CacheResult.class) != null
                    || method.getAnnotation(CacheRemove.class) != null
                    || method.getAnnotation(CacheRemoveAll.class) != null) {
                flag = false;
                break;
            }
        }
        if (flag)
            return list;
        list.add(new InterceptorPair(ioc.get(NutCachePutInterceptor.class), new JCacheMethodMatcher(CachePut.class)));
        list.add(new InterceptorPair(ioc.get(NutCacheResultInterceptor.class), new JCacheMethodMatcher(CacheResult.class)));
        list.add(new InterceptorPair(ioc.get(NutCacheRemoveEntryInterceptor.class), new JCacheMethodMatcher(CacheRemove.class)));
        list.add(new InterceptorPair(ioc.get(NutCacheRemoveAllInterceptor.class), new JCacheMethodMatcher(CacheRemoveAll.class)));
        return list;
    }
}

class JCacheMethodMatcher implements MethodMatcher {
    
    protected Class<? extends Annotation> klass;
    public JCacheMethodMatcher(Class<? extends Annotation> klass) {
        this.klass = klass;
    }

    public boolean match(Method method) {
        return method.getAnnotation(klass) != null;
    }
    
}