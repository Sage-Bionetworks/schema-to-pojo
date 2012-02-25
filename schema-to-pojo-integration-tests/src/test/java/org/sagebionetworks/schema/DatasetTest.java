package org.sagebionetworks.schema;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class DatasetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests that a Dataset can be created, and used.
	 */
	@Test
	public void testDatasetCompiles() throws Exception {
		Dataset dSet = new Dataset();
		
		//add a few item to the dataset's enum
		List<DiseaseNames> datasetDiseases = dSet.getDiseases();
		assertNull(datasetDiseases);
		datasetDiseases =  new ArrayList<DiseaseNames>();
		datasetDiseases.add(DiseaseNames.Asthma);
		datasetDiseases.add(DiseaseNames.Cancer);
		dSet.setDiseases(datasetDiseases);
		
		List<DiseaseNames> newDiseases = dSet.getDiseases();
		assertNotNull(newDiseases);
		assertTrue(newDiseases.size() == 2);
		DiseaseNames first = newDiseases.get(0);
		assertEquals(DiseaseNames.Asthma, first);
		DiseaseNames second = newDiseases.get(1);
		assertEquals(DiseaseNames.Cancer, second);
	}
	
	/**
	 * Tests round trip for Dataset
	 */
	@Test
	public void testDatasetRoundTrip() throws Exception {
		Dataset dSet = new Dataset();
		
		//add a few item to the dataset's enum
		List<DiseaseNames> datasetDiseases = dSet.getDiseases();
		assertNull(datasetDiseases);
		datasetDiseases =  new ArrayList<DiseaseNames>();
		datasetDiseases.add(DiseaseNames.Asthma);
		datasetDiseases.add(DiseaseNames.Cancer);
		dSet.setDiseases(datasetDiseases);
		
		assertNotNull(dSet.getDiseases());
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		dSet.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		Dataset secondDataset = new Dataset(adapter);
		assertEquals(dSet, secondDataset);
	}
}
