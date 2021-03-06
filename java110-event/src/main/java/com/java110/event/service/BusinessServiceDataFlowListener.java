package com.java110.event.service;

import com.java110.event.app.order.Ordered;
import com.java110.event.center.DataFlowEvent;

import java.util.EventListener;

/**
 * 通用事件处理，
 * Created by wuxw on 2018/4/17.
 */
public interface BusinessServiceDataFlowListener extends EventListener,Ordered {

    /**
     * 业务 编码
     * @return
     */
    public String getServiceCode();

    public void soService(BusinessServiceDataFlowEvent event);
}
