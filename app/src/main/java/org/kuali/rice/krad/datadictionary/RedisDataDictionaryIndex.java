package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Map;

//TODO: NEW IDEA - SHA keys to improve performance
//TODO: NEW IDEA - Check value exists and repoint to existing value
public class RedisDataDictionaryIndex extends DataDictionaryIndex {

	private static final String INDEX_BUSINESS_OBJECT = "businessObject";
	private static final String INDEX_JSTL = "jstl";
	private static final String INDEX_DATA_OBJECT = "dataObject";
	private static final String INDEX_DOCUMENT = "document";
	private static final String INDEX_DOCUMENT_OBJECT_CLASS = "document:objectClass";
	private static final String INDEX_DOCUMENT_MAINTAINABLE_CLASS = "document:maintainableClass";

	private final ListableBeanFactory ddBeans;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Resource(name = "redisTemplate")
	private HashOperations hashOperations;

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

	@Override
	public void run() {
		buildDataIndicies();
		buildDocumentIndicies();
	}

	@Override
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return hashOperations.entries(INDEX_BUSINESS_OBJECT);
	}

	@Override
	public Map<String, DataObjectEntry> getDataObjectEntries() {
		return hashOperations.entries(INDEX_DATA_OBJECT);
	}

	@Override
	public Map<String, DataDictionaryEntry> getEntriesByJstlKey() {
		return hashOperations.entries("jstl");
	}

	@Override
	public Map<String, DocumentEntry> getDocumentEntries() {
		return hashOperations.entries(INDEX_DOCUMENT);
	}

	@Override
	public Map<Class, DocumentEntry> getDocumentEntriesByBusinessObjectClass() {
		return hashOperations.entries(INDEX_DOCUMENT_OBJECT_CLASS);
	}

	@Override
	public Map<Class, DocumentEntry> getDocumentEntriesByMaintainableClass() {
		return hashOperations.entries("document:maintainableClass");
	}

	private void buildDataIndicies() {
		Map<String, DataObjectEntry> boBeans = ddBeans.getBeansOfType(DataObjectEntry.class);
		for (DataObjectEntry entry : boBeans.values()) {
			validateUniqueness(entry);
			// put all BO and DO entries in the objectEntries map
			hashOperations.put(INDEX_DATA_OBJECT, entry.getDataObjectClass().getSimpleName(), entry);
			hashOperations.put(INDEX_DATA_OBJECT, entry.getDataObjectClass().getName(), entry);
			// keep a separate map of BO entries for now
			if (entry instanceof BusinessObjectEntry) {
				BusinessObjectEntry boEntry = (BusinessObjectEntry) entry;
				hashOperations.put(INDEX_BUSINESS_OBJECT, boEntry.getBusinessObjectClass().getSimpleName(), boEntry);
				hashOperations.put(INDEX_BUSINESS_OBJECT, boEntry.getBusinessObjectClass().getName(), boEntry);
				// If a "base" class is defined for the entry, index the entry by that class as well.
				if (boEntry.getBaseBusinessObjectClass() != null) {
					hashOperations.put(INDEX_BUSINESS_OBJECT, boEntry.getBaseBusinessObjectClass().getSimpleName(), boEntry);
					hashOperations.put(INDEX_BUSINESS_OBJECT, boEntry.getBaseBusinessObjectClass().getName(), boEntry);
				}
			}
			hashOperations.put(INDEX_JSTL, entry.getJstlKey(), entry);
		}
	}

	private void buildDocumentIndicies() {
		Map<String, DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
		for (DocumentEntry entry : docBeans.values()) {
			validateTransactionDocuments(entry);
			validateUniqueness(entry);
			buildBaseDocumentIndices(entry);
			buildTransactionalDocumenIndicies(entry);
			buildMaintenanceDocumentIndicies(entry);
		}
	}

	private void buildBaseDocumentIndices(DocumentEntry entry) {
		final String documentTypeName = entry.getDocumentTypeName();
		hashOperations.put(INDEX_DOCUMENT, entry.getFullClassName(), entry);
		hashOperations.put(INDEX_DOCUMENT, entry.getDocumentClass().getName(), entry);
		if (entry.getBaseDocumentClass() != null) {
			hashOperations.put(INDEX_DOCUMENT, entry.getBaseDocumentClass().getName(), entry);
		}
		if (StringUtils.isNotBlank(entry.getJstlKey())){
			hashOperations.put(INDEX_JSTL, entry.getJstlKey(), entry);
		}
		if (StringUtils.isNotBlank(documentTypeName)){
			hashOperations.put(INDEX_DOCUMENT, documentTypeName, entry);
		}
	}

	private void buildMaintenanceDocumentIndicies(DocumentEntry entry) {
		if (entry instanceof MaintenanceDocumentEntry) {
			MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) entry;
			hashOperations.put(INDEX_DOCUMENT, mde.getDataObjectClass().getSimpleName() + "MaintenanceDocument", entry);
			hashOperations.put(INDEX_DOCUMENT_OBJECT_CLASS, mde.getDataObjectClass(), entry);
			hashOperations.put(INDEX_DOCUMENT_MAINTAINABLE_CLASS, mde.getMaintainableClass(), entry);
		}
	}

	private void buildTransactionalDocumenIndicies(DocumentEntry entry) {
		if (entry instanceof TransactionalDocumentEntry) {
			TransactionalDocumentEntry tde = (TransactionalDocumentEntry) entry;
			hashOperations.put(INDEX_DOCUMENT, tde.getDocumentClass().getSimpleName(), entry);
			if (tde.getBaseDocumentClass() != null) {
				hashOperations.put(INDEX_DOCUMENT, tde.getBaseDocumentClass().getSimpleName(), entry);
			}
		}
	}

	private void validateTransactionDocuments(DocumentEntry entry) {
		if (!(entry instanceof TransactionalDocumentEntry)) {
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
