package org.kuali.rice.krad.datadictionary;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisDataDictionaryIndex extends DataDictionaryIndex{

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	private final ListableBeanFactory ddBeans;

	/* !!!For testing purposes only!!! */
	RedisDataDictionaryIndex(RedisTemplate<String, String> redisTemplate, DefaultListableBeanFactory ddBeans) {
		this(ddBeans);
		this.redisTemplate = redisTemplate;
	}

	public RedisDataDictionaryIndex(DefaultListableBeanFactory ddBeans) {
		super(ddBeans);
		this.ddBeans = ddBeans;
	}

}
