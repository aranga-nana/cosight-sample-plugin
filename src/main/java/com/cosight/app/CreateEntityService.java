package com.cosight.app;

import au.com.cosight.entity.domain.EntityInstance;
import au.com.cosight.entity.domain.InstanceValue;
import au.com.cosight.sdk.auth.DefaultCredentialProvider;
import au.com.cosight.sdk.entities.instances.EntityInstanceClient;
import au.com.cosight.sdk.entities.instances.EntityInstanceClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class CreateEntityService {

    private static Logger logger = LoggerFactory.getLogger(CreateEntityService.class);

    @Value("${namespace}")
    private String namespace;

    public void create() {

        if (StringUtils.isEmpty(namespace)) {
            throw new IllegalStateException("namespace is null");
        }
        try {
            EntityInstanceClient client = EntityInstanceClientBuilder.standard().build(new DefaultCredentialProvider());
            EntityInstance instance = new EntityInstance();
            instance.set_vertexName(namespace+"__TestEntity1");
            instance.setFtxCreatedUser("system");
            InstanceValue value = new InstanceValue();
            value.setName("Name");
            value.setValue("api inserted"+new Date());
            instance.setInstanceValues(Collections.singletonList(value));
            instance = client.saveInstance(instance);
            logger.info("created rid {}, ftxUUID {}",instance.getId(),instance.getFtxUUID());

        }catch (Throwable e){
            e.printStackTrace();
        }



    }
}
