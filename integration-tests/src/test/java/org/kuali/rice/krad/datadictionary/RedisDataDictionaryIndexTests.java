package org.kuali.rice.krad.datadictionary;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisDataDictionaryIndexTests extends BaseDataDictionaryIndexTests{

	@Override
	protected DataDictionaryIndex doInitDDIndex(DefaultListableBeanFactory ddBeans) {
		return new RedisDataDictionaryIndex((RedisTemplate<String, String>) applicationContext.getBean("redisTemplate"), ddBeans);
	}

}
