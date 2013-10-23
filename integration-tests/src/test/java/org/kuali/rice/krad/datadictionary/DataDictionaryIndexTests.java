package org.kuali.rice.krad.datadictionary;

public class DataDictionaryIndexTests extends BaseDataDictionaryIndexTests{

	@Override
	protected DataDictionaryIndex doInitDDIndex(DefaultListableBeanFactory ddBeans) {
		return new DataDictionaryIndex(ddBeans);
	}
}
