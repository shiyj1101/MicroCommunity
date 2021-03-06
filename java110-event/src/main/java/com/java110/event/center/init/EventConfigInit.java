package com.java110.event.center.init;

import com.java110.common.factory.ApplicationContextFactory;
import com.java110.event.app.AppEventPublishing;
import com.java110.event.center.DataFlowEventPublishing;
import com.java110.event.center.DataFlowListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.naming.ConfigurationException;
import java.util.Properties;

/**
 * 系统启动时加载信息
 * Created by wuxw on 2017/4/14.
 */
public class EventConfigInit {

    /**
     * 默认 事件配置路径classpath:/
     */
    private final static String DEFAULT_EVENT_PATH = "config/";


    /**
     * 默认 文件名称  .properties
     */
    private final static String DEFAULT_FILE_NAME = "center_event.properties";

    /**
     * 订单调度处理侦听
     */
    private final static String DATAFLOW_LISTENER = "java110.event.properties.centerServiceListener";

    /**
     * 订单调度事件
     */
    private final static String DISPATCH_EVENT = "java110.event.properties.centerServiceEvent";


    public static void initSystemConfig(){
        //加载配置文件，注册订单处理侦听
        try {

            Properties properties = load(DEFAULT_EVENT_PATH,DEFAULT_FILE_NAME);

            registerListener(properties);

            //注册事件
            registerEvent(properties);

        }
        catch (Exception ex) {
            throw new IllegalStateException("system init error", ex);
        }
    }


    /**
     * 加载文件
     * @param location
     * @param filename
     * @param
     */
    private  static Properties load(String location,String filename) throws Exception{
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(location+filename));
        return properties;
    }

    /**
     * 注册侦听
     * @param properties
     */
    private static void registerListener(Properties properties) throws Exception{

        String[] listeners = properties.getProperty(DATAFLOW_LISTENER).split("\\,");

        for(String listener : listeners){

            //这里不能直接反射，这样 IXXXService 无法注入，所以直接从spring 中获取已经注入的
            DataFlowListener<?> dataFlowListener = (DataFlowListener<?>) ApplicationContextFactory.getBean(listener);

            //将 listener 放入 AppEventPublishing 中方便后期操作
            //注册侦听
            DataFlowEventPublishing.addListenner(dataFlowListener);
        }
    }

    /**
     * 注册事件
     * @param properties
     * @throws Exception
     */
    private static void registerEvent(Properties properties) throws Exception{
        String[] events = properties.getProperty(DISPATCH_EVENT).split("\\,");

        for (String event : events){

            if(StringUtils.isBlank(event) || !event.contains("::")){
                throw new ConfigurationException("配置错误，["+DISPATCH_EVENT+"= "+events+"] 当前 [event = "+event+"],不存在 :: ,配置格式为 A::B");
            }

            String[] tmpEvent = event.split("::");

            if(tmpEvent.length > 2){
                throw new ConfigurationException("配置错误，["+DISPATCH_EVENT+"= "+events+"] 当前 [event = "+event+"],只能有一个 :: ,配置格式为 A::B");
            }

            Class clazz = Class.forName(tmpEvent[1]);

            AppEventPublishing.addEvent(tmpEvent[0],clazz);
        }

    }


}
