package com.sfebiz.demo.service.http;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.sfebiz.demo.api.DemoService;
import com.sfebiz.demo.api.DemoThirdPartyService;
import com.sfebiz.demo.dao.dto.DemoDTO;
import com.sfebiz.demo.dao.mapper.DemoMapper;
import com.sfebiz.demo.entity.DemoEntity;
import com.sfebiz.demo.entity.DemoReturnCode;
import net.pocrd.define.Evaluater;
import net.pocrd.dubboext.DubboExtProperty;
import net.pocrd.entity.ServiceException;
import net.pocrd.entity.ServiceRuntimeException;
import net.pocrd.util.EvaluaterProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class DemoServiceImpl implements DemoService {
    private static final Logger    logger    = LoggerFactory.getLogger(DemoServiceImpl.class);
    private static final Evaluater evaluater = EvaluaterProvider.getEvaluater(DemoEntity.class, DemoDTO.class);
    @Autowired
    private DemoMapper            demoMapper;
    @Autowired
    private DemoThirdPartyService demoThirdPartyService;

    /**
     * 边界异常处理
     *
     * @param name
     *
     * @return
     *
     * @throws ServiceException
     */
    @Override
    public DemoEntity sayHello(String name) throws ServiceException {
        DemoEntity result = null;
        try {
            result = new DemoEntity();
            DemoDTO demoDTO = demoMapper.queryEntity(name);
            demoDTO.setId(demoThirdPartyService.testThirdParty(demoDTO.getId()));
            evaluater.evaluate(result, demoDTO);
        } catch (ServiceRuntimeException sre) {
            logger.error("api failed.", sre);
            throw new ServiceException("api failed.", sre);
        } catch (Throwable t) {
            logger.error("api failed.", t);
            if (t instanceof ServiceException) {
                throw (ServiceException)t;
            } else {
                throw new ServiceException(DemoReturnCode.DEMO_UNKNOW_ERROR, "api failed.");
            }
        }
        return result;
    }

    @Override
    public DemoEntity tryError(String in) throws ServiceException {
        throw new RuntimeException("try error!");
    }

    @Override
    public String testRegistedDevice() throws ServiceException {
        return "test";
    }

    @Override
    public String testUserLogin(long deviceId, long userId) throws ServiceException {
        System.out.println("deviceId:" + deviceId + ", userId:" + userId);
        return "deviceId:" + deviceId + ", userId:" + userId;
    }

    @Override
    public String getResByThirdPartyId(String something) throws ServiceException {
        System.out.println("something:" + something);
        return something;
    }

    @Override
    public String testRedirect() throws ServiceException {
        DubboExtProperty.setRedirectUrl("http://www.sfht.com");
        return null;
    }
}
