package org.kuali.rice.krad.datadictionary;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

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


	//TODO: NEW IDEA - SHA keys to improve performance
	//TODO: NEW IDEA - Check value exists and repoint to existing value

	private void buildDDIndicies() {
		HashOperations hashOperations = redisTemplate.opsForHash();


//		// primary indices
//		businessObjectEntries = new HashMap<String, BusinessObjectEntry>();
//		objectEntries = new HashMap<String, DataObjectEntry>();
//		documentEntries = new HashMap<String, DocumentEntry>();
//
//		// alternate indices
//		documentEntriesByBusinessObjectClass = new HashMap<Class, DocumentEntry>();
//		documentEntriesByMaintainableClass = new HashMap<Class, DocumentEntry>();
//		entriesByJstlKey = new HashMap<String, DataDictionaryEntry>();

		// loop over all beans in the context
		Map<String, DataObjectEntry> boBeans = ddBeans.getBeansOfType(DataObjectEntry.class);
		for (DataObjectEntry entry : boBeans.values()) {

//			DataObjectEntry indexedEntry = objectEntries.get(entry.getJstlKey());
//			if (indexedEntry == null) {
//				indexedEntry = businessObjectEntries.get(entry.getJstlKey());
//			}
//
//			if ((indexedEntry != null) && !(indexedEntry.getDataObjectClass().equals(entry.getDataObjectClass()))) {
//				throw new DataDictionaryException(new StringBuffer(
//					"Two object classes may not share the same jstl key: this=").append(entry.getDataObjectClass())
//					.append(" / existing=").append(indexedEntry.getDataObjectClass()).toString());
//			}

			// put all BO and DO entries in the objectEntries map
			hashOperations.put("dataObject", entry.getDataObjectClass().getSimpleName(), entry);
			hashOperations.put("dataObject", entry.getDataObjectClass().getName(), entry);

			// keep a separate map of BO entries for now
			if (entry instanceof BusinessObjectEntry) {
				BusinessObjectEntry boEntry = (BusinessObjectEntry) entry;
				hashOperations.put("businessObject", boEntry.getBusinessObjectClass().getSimpleName(), boEntry);
				hashOperations.put("businessObject", boEntry.getBusinessObjectClass().getName(), boEntry);
				// If a "base" class is defined for the entry, index the entry by that class as well.
				if (boEntry.getBaseBusinessObjectClass() != null) {
					hashOperations.put("businessObject", boEntry.getBaseBusinessObjectClass().getSimpleName(), boEntry);
					hashOperations.put("businessObject", boEntry.getBaseBusinessObjectClass().getName(), boEntry);
				}
			}

			hashOperations.put("jstl",entry.getJstlKey(), entry);
		}

	}

	@Override
	public void run() {
		buildDDIndicies();
	}

	@Override
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		HashOperations hashOperations = redisTemplate.opsForHash();
		Map<String, BusinessObjectEntry> entries = hashOperations.entries("businessObject");
		return entries;
	}

	@Override
	public Map<String, DataObjectEntry> getDataObjectEntries() {
		HashOperations hashOperations = redisTemplate.opsForHash();
		Map<String, DataObjectEntry> entries = hashOperations.entries("dataObject");
		return entries;
	}

	@Override
	public Map<String, DataDictionaryEntry> getEntriesByJstlKey() {
		HashOperations hashOperations = redisTemplate.opsForHash();
		Map<String, DataDictionaryEntry> entries = hashOperations.entries("jstl");
		return entries;
	}


}
