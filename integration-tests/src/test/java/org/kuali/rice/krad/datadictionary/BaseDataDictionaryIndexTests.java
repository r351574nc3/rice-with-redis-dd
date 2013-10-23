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
	protected static final String SAMPLE_BO_ENTRY = "SampleBO";

	@Before
	public void initDDIndex(){
		DefaultListableBeanFactory ddBeans = new DefaultListableBeanFactory();
		//todo: find a way to load kuali default listable beans from application context
		ddBeans.registerSingleton(SAMPLE_BO_ENTRY, applicationContext.getBean(SAMPLE_BO_ENTRY));
		dataDictionaryIndex = doInitDDIndex(ddBeans);
	}

	protected abstract DataDictionaryIndex doInitDDIndex(DefaultListableBeanFactory ddBeans);


	@Test
	public void loadBusinessObjectEntries() throws Exception{
		//given
		dataDictionaryIndex.run();
		//when
		Map<String, BusinessObjectEntry> result = dataDictionaryIndex.getBusinessObjectEntries();
		//then
		assertTrue(result.containsKey(SAMPLE_BO_ENTRY));
		BusinessObjectEntry sampleBO = result.get(SAMPLE_BO_ENTRY);
		assertEquals("Business Object Class",Class.forName("org.kualigan.rr.test.SampleBO"),sampleBO.getBusinessObjectClass());
		assertEquals("Title Attribute","titleAttr",sampleBO.getTitleAttribute());
		assertEquals("Object Label","Sample BO",sampleBO.getObjectLabel());
	}
}
