package org.kuali.rice.krad.datadictionary;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisDataDictionaryIndexTests extends BaseDataDictionaryIndexTests{

	@Override
	protected DataDictionaryIndex doInitDDIndex(DefaultListableBeanFactory ddBeans) {
		final RedisTemplate<String, String> redisTemplate = (RedisTemplate<String, String>) applicationContext.getBean("redisTemplate");
		return new RedisDataDictionaryIndex(redisTemplate,redisTemplate.opsForHash(),ddBeans);
	}

}
