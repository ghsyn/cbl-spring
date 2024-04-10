package itman.com.cmmn.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class RefreshableSqlSessionFactoryBean extends SqlSessionFactoryBean implements DisposableBean {

    private static final Log log = LogFactory.getLog(RefreshableSqlSessionFactoryBean.class);
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    private SqlSessionFactory proxy;
    private int interval = 500;
    private Timer timer;
    private TimerTask task;
    private Resource[] mapperLocations;
    /**
     * 파일 감시 쓰레드가 실행중인지 여부.
     */
    private boolean running = false;

    public void setMapperLocations(Resource[] mapperLocations) {
        super.setMapperLocations(mapperLocations);
        this.mapperLocations = mapperLocations;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void refresh() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("refreshing sqlMapClient.");
        }
        w.lock();
        try {
            super.afterPropertiesSet();

        } finally {
            w.unlock();
        }
    }

    /**
     * 싱글톤 멤버로 SqlMapClient 원본 대신 프록시로 설정하도록 오버라이드.
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        setRefreshable();
    }

    private void setRefreshable() {
        proxy = (SqlSessionFactory) Proxy.newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSessionFactory.class},
                (proxy, method, args) -> {
                    // log.debug("method.getName() : " + method.getName());
                    return method.invoke(getParentObject(), args);
                });

        task = new TimerTask() {
            private final Map<Resource, Long> map = new HashMap<>();

            public void run() {
                if (isModified()) {
                    try {
                        refresh();
                    } catch (Exception e) {
                        log.error("caught exception", e);
                    }
                }
            }

            private boolean isModified() {
                boolean retVal = false;

                if (mapperLocations != null) {
                    for (Resource mappingLocation : mapperLocations) {
                        retVal |= findModifiedResource(mappingLocation);
                    }
                }

                return retVal;
            }

            private boolean findModifiedResource(Resource resource) {
                boolean retVal = false;
                List<String> modifiedResources = new ArrayList<>();

                try {
                    long modified = resource.lastModified();

                    if (map.containsKey(resource)) {
                        long lastModified = map.get(resource);

                        if (lastModified != modified) {
                            map.put(resource, modified);
                            modifiedResources.add(resource.getDescription());
                            retVal = true;
                        }
                    } else {
                        map.put(resource, modified);
                    }
                } catch (IOException e) {
                    log.error("caught exception", e);
                }
                if (retVal) {
                    if (log.isInfoEnabled()) {
                        log.info("modified files : " + modifiedResources);
                    }
                }
                return retVal;
            }
        };

        timer = new Timer(true);
        resetInterval();

    }

    private Object getParentObject() throws Exception {
        r.lock();
        try {
            return super.getObject();

        } finally {
            r.unlock();
        }
    }

    public SqlSessionFactory getObject() {
        return this.proxy;
    }

    public Class<? extends SqlSessionFactory> getObjectType() {
        return (this.proxy != null ? this.proxy.getClass()
                : SqlSessionFactory.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void setCheckInterval(int ms) {
        interval = ms;
        if (timer != null) {
            resetInterval();
        }
    }

    private void resetInterval() {
        if (running) {
            timer.cancel();
            running = false;
        }
        if (interval > 0) {
            timer.schedule(task, 0, interval);
            running = true;
        }
    }

    public void destroy() throws Exception {
        timer.cancel();
    }
}