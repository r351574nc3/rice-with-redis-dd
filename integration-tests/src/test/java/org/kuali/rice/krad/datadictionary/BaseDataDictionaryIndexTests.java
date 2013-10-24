package org.kuali.rice.krad.datadictionary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {
	"classpath:org/kuali/rice/krad/uif/UifControlDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifConfigurationDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifRiceDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifFieldDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifLookupDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifInquiryDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifGroupDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifHeaderFooterDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifLayoutManagerDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifViewPageDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifWidgetDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifMaintenanceDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifDocumentDefinitions.xml",
	"classpath:org/kuali/rice/krad/uif/UifElementDefinitions.xml",

	"classpath:org/kuali/rice/krad/datadictionary/DataDictionaryBaseTypes.xml",
	"classpath:org/kuali/rice/krad/datadictionary/AttributeReference.xml",
	"classpath:org/kuali/rice/kns/bo/datadictionary/DataDictionaryBaseTypes.xml",
	"classpath:org/kuali/rice/kew/bo/datadictionary/DocumentType.xml",

	"classpath:org/kualigan/rr/test/SampleDD.xml",
	"classpath:org/kualigan/rr/RedisDataSourceConfig.xml",
})
public abstract class BaseDataDictionaryIndexTests  extends AbstractJUnit4SpringContextTests {

	protected DataDictionaryIndex dataDictionaryIndex;
	protected static final String BUSINESS_OBJECT_BEAN_ID = "SampleBusinessObject";
	protected static final String BUSINESS_OBJECT_SIMPLE_NAME = "SampleBO";
	protected static final String BUSINESS_OBJECT_CLASS_NAME = "org.kualigan.rr.test.SampleBO";
	protected static final String BASE_BUSINESS_OBJECT_SIMPLE_NAME = "BaseSampleBO";
	protected static final String BASE_BUSINESS_OBJECT_CLASS_NAME = "org.kualigan.rr.test.SampleBO";

	protected static final String DATA_OBJECT_BEAN_ID = "SampleDataObject";
	protected static final String DATA_OBJECT_SIMPLE_NAME = "SampleDO";
	protected static final String DATA_OBJECT_CLASS_NAME = "org.kualigan.rr.test.SampleDO";

	@Before
	public void initDDIndex(){
		DefaultListableBeanFactory ddBeans = new DefaultListableBeanFactory();
		//todo: find a way to load kuali default listable beans from application context
		ddBeans.registerSingleton(BUSINESS_OBJECT_BEAN_ID, applicationContext.getBean(BUSINESS_OBJECT_BEAN_ID));
		ddBeans.registerSingleton(DATA_OBJECT_BEAN_ID, applicationContext.getBean(DATA_OBJECT_BEAN_ID));
		dataDictionaryIndex = doInitDDIndex(ddBeans);
		dataDictionaryIndex.run();
	}

	protected abstract DataDictionaryIndex doInitDDIndex(DefaultListableBeanFactory ddBeans);

	@Test
	public void getBusinessObjectEntries() throws Exception{
		//given dd Index is loaded
		//when
		Map<String, BusinessObjectEntry> result = dataDictionaryIndex.getBusinessObjectEntries();
		//then
		assertTrue(result.containsKey(BUSINESS_OBJECT_CLASS_NAME));
		assertTrue(result.containsKey(BUSINESS_OBJECT_SIMPLE_NAME));
		assertBusinessObjectEntry(result.get(BUSINESS_OBJECT_SIMPLE_NAME));
	}

	@Test
	public void getBusinessObjectEntriesByBaseObjectClass() throws Exception{
		//given dd Index is loaded
		//when
		Map<String, BusinessObjectEntry> result = dataDictionaryIndex.getBusinessObjectEntries();
		//then
		assertTrue(result.containsKey(BASE_BUSINESS_OBJECT_CLASS_NAME));
		assertTrue(result.containsKey(BASE_BUSINESS_OBJECT_SIMPLE_NAME));
		assertBusinessObjectEntry(result.get(BASE_BUSINESS_OBJECT_SIMPLE_NAME));
	}

	@Test
	public void getDataObjectEntries() throws Exception{
		//given dd Index is loaded
		//when
		Map<String, DataObjectEntry> result = dataDictionaryIndex.getDataObjectEntries();
		//then
		assertTrue(result.containsKey(DATA_OBJECT_SIMPLE_NAME));
		assertTrue(result.containsKey(DATA_OBJECT_CLASS_NAME));
		assertDataObjectEntry(result.get(DATA_OBJECT_SIMPLE_NAME));
	}

	@Test
	public void getEntriesByJstl() throws Exception{
		//given dd Index is loaded
		//when
		Map<String, DataDictionaryEntry> result = dataDictionaryIndex.getEntriesByJstlKey();
		//then
		assertTrue(result.containsKey(BUSINESS_OBJECT_SIMPLE_NAME));
		assertTrue(result.containsKey(DATA_OBJECT_SIMPLE_NAME));
	}

	private void assertDataObjectEntry(DataObjectEntry dataObjectEntry) throws Exception {
		assertEquals("Data Object Class",Class.forName("org.kualigan.rr.test.SampleDO"),dataObjectEntry.getDataObjectClass());
		assertEquals("Title Attribute","titleAttr",dataObjectEntry.getTitleAttribute());
		assertEquals("Object Label","Sample DO",dataObjectEntry.getObjectLabel());
	}

	private void assertBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) throws ClassNotFoundException {
		assertEquals("Business Object Class",Class.forName("org.kualigan.rr.test.SampleBO"), businessObjectEntry.getBusinessObjectClass());
		assertEquals("Title Attribute","titleAttr", businessObjectEntry.getTitleAttribute());
		assertEquals("Object Label","Sample BO", businessObjectEntry.getObjectLabel());
	}
}
