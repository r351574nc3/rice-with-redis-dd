package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Map;

public class RedisDataDictionaryIndex extends DataDictionaryIndex{

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Resource(name="redisTemplate")
	private HashOperations hashOperations;

	private final ListableBeanFactory ddBeans;

	/* !!!For testing purposes only!!! */
	RedisDataDictionaryIndex(RedisTemplate<String, String> redisTemplate, HashOperations hashOperations, DefaultListableBeanFactory ddBeans) {
		this(ddBeans);
		this.redisTemplate = redisTemplate;
		this.hashOperations = hashOperations;
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
		buildDataIndicies();
		buildDocumentIndicies();

	}

	@Override
	public void run() {
		buildDDIndicies();
	}

	@Override
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		Map<String, BusinessObjectEntry> entries = hashOperations.entries("businessObject");
		return entries;
	}

	@Override
	public Map<String, DataObjectEntry> getDataObjectEntries() {
		Map<String, DataObjectEntry> entries = hashOperations.entries("dataObject");
		return entries;
	}

	@Override
	public Map<String, DataDictionaryEntry> getEntriesByJstlKey() {
		Map<String, DataDictionaryEntry> entries = hashOperations.entries("jstl");
		return entries;
	}

	@Override
	public Map<String, DocumentEntry> getDocumentEntries() {
		Map<String, DocumentEntry> entries = hashOperations.entries("document");
		return entries;
	}

	private void buildDataIndicies() {
		Map<String, DataObjectEntry> boBeans = ddBeans.getBeansOfType(DataObjectEntry.class);
		for (DataObjectEntry entry : boBeans.values()) {
			validateUniqueness(entry);
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

	private void buildDocumentIndicies() {
//		//Build Document Entry Index
		Map<String, DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
		for (DocumentEntry entry : docBeans.values()) {
			String entryName = entry.getDocumentTypeName();
			validateTransactionDocuments(entry);
			validateUniqueness(entry);
			hashOperations.put("document", entryName, entry);
			hashOperations.put("jstl", entry.getJstlKey(), entry);

//			//documentEntries.put(entry.getFullClassName(), entry);
//			documentEntries.put(entry.getDocumentClass().getName(), entry);
//			if (entry.getBaseDocumentClass() != null) {
//				documentEntries.put(entry.getBaseDocumentClass().getName(), entry);
//			}
//			entriesByJstlKey.put(entry.getJstlKey(), entry);
//
//			if (entry instanceof TransactionalDocumentEntry) {
//				TransactionalDocumentEntry tde = (TransactionalDocumentEntry) entry;
//
//				documentEntries.put(tde.getDocumentClass().getSimpleName(), entry);
//				if (tde.getBaseDocumentClass() != null) {
//					documentEntries.put(tde.getBaseDocumentClass().getSimpleName(), entry);
//				}
//			}
//
//			if (entry instanceof MaintenanceDocumentEntry) {
//				MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) entry;
//
//				documentEntriesByBusinessObjectClass.put(mde.getDataObjectClass(), entry);
//				documentEntriesByMaintainableClass.put(mde.getMaintainableClass(), entry);
//				documentEntries.put(mde.getDataObjectClass().getSimpleName() + "MaintenanceDocument", entry);
//			}
		}
	}


	private void validateTransactionDocuments(DocumentEntry entry){
		if (!(entry instanceof TransactionalDocumentEntry)){
			  return;
		}
		DocumentEntry existingDocumentEntry = getDocumentEntries().get(entry.getFullClassName());
		if (existingDocumentEntry != null && !StringUtils.equals(existingDocumentEntry.getDocumentTypeName(), entry.getDocumentTypeName())) {
			throw new DataDictionaryException(String.format("Two transactional document types may not share the same document class: this=%s / existing=%s",
				entry.getDocumentTypeName(), existingDocumentEntry.getDocumentTypeName()));
		}
	}

	private void validateUniqueness(DataDictionaryEntry entry) {
		Map<String, DataDictionaryEntry> entries = hashOperations.entries("jstl");
		//todo: think this is a bug in org implementation
//		DataObjectEntry indexedEntry = objectEntries.get(entry.getJstlKey());
//		if (indexedEntry == null) {
//			indexedEntry = businessObjectEntries.get(entry.getJstlKey());
//		}

		final DataDictionaryEntry existingEntry = entries.get(entry.getJstlKey());
		if (existingEntry != null && (!existingEntry.getFullClassName().equals(entry.getFullClassName()))) {
			throw new DataDictionaryException(new StringBuffer(
				"Two object classes may not share the same jstl key: this=").append(entry.getFullClassName())
				.append(" / existing=").append(existingEntry.getFullClassName()).toString());
		}
	}



}
