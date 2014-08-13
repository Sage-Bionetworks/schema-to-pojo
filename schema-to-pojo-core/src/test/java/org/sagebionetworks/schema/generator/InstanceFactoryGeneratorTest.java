package org.sagebionetworks.schema.generator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class InstanceFactoryGeneratorTest {

	ObjectSchema interfaceOne;
	ObjectSchema interfaceTwo;
	ObjectSchema interfaceThree;
	ObjectSchema oneImpl;
	ObjectSchema twoImpl;
	ObjectSchema bothImpl;
	ObjectSchema threeImpl;
	List<ObjectSchema> all;
	JCodeModel codeModel;
	
	@Before
	public void before(){
		// one
		interfaceOne = new ObjectSchema();
		interfaceOne.setType(TYPE.INTERFACE);
		interfaceOne.setId("org.example.InterfaceOne");
		
		interfaceTwo = new ObjectSchema();
		interfaceTwo.setType(TYPE.INTERFACE);
		interfaceTwo.setId("org.example.InterfaceTwo");
		
		interfaceThree = new ObjectSchema();
		interfaceThree.setType(TYPE.INTERFACE);
		interfaceThree.setId("org.example.InterfaceThree");
		// Three extends the first two.
		interfaceThree.setImplements(new ObjectSchema[]{interfaceOne, interfaceTwo});
		
		// reference to two.
		// one impl
		oneImpl = new ObjectSchema();
		oneImpl.setId("org.example.OneImpl");
		oneImpl.setType(TYPE.OBJECT);
		oneImpl.setImplements(new ObjectSchema[]{interfaceOne});
		// two impl
		twoImpl = new ObjectSchema();
		twoImpl.setId("org.example.TwoImpl");
		twoImpl.setType(TYPE.OBJECT);
		twoImpl.setImplements(new ObjectSchema[]{interfaceTwo});
		// Implements both
		bothImpl = new ObjectSchema();
		bothImpl.setId("org.example.BothImpl");
		bothImpl.setType(TYPE.OBJECT);
		bothImpl.setImplements(new ObjectSchema[]{interfaceOne, interfaceTwo});
		
		// three impl
		threeImpl = new ObjectSchema();
		threeImpl.setId("org.example.ThreeImpl");
		threeImpl.setType(TYPE.OBJECT);
		threeImpl.setImplements(new ObjectSchema[]{interfaceThree});
		
		// the list
		all = new ArrayList<ObjectSchema>();
		all.add(interfaceOne);
		all.add(interfaceTwo);
		all.add(interfaceThree);
		all.add(oneImpl);
		all.add(twoImpl);
		all.add(bothImpl);
		all.add(threeImpl);
		
		codeModel = new JCodeModel();
	}
	
	@Test
	public void testRoundTrip(){
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, all);
		// There should be a factory for both interfaces.
		JDefinedClass factoryClass = ifg.getFactoryClass(interfaceOne.getId());
		assertNotNull(factoryClass);
		assertEquals("org.example.InterfaceOneInstatanceFactory", factoryClass.fullName());
		// This factory should create one, both and three
		Set<String> expected = new HashSet<String>(Arrays.asList(oneImpl.getId(), bothImpl.getId(), threeImpl.getId()));
		assertEquals(expected, ifg.getImplementaionIdsForFactory(factoryClass));
		// two
		factoryClass = ifg.getFactoryClass(interfaceTwo.getId());
		assertNotNull(factoryClass);
		assertEquals("org.example.InterfaceTwoInstatanceFactory", factoryClass.fullName());
		// This factory should create two, both and three
		expected = new HashSet<String>(Arrays.asList(twoImpl.getId(), bothImpl.getId(), threeImpl.getId()));
		assertEquals(expected, ifg.getImplementaionIdsForFactory(factoryClass));
		// three
		factoryClass = ifg.getFactoryClass(interfaceThree.getId());
		assertNotNull(factoryClass);
		assertEquals("org.example.InterfaceThreeInstatanceFactory", factoryClass.fullName());
		// This factory should create just three
		expected = new HashSet<String>(Arrays.asList(threeImpl.getId()));
		assertEquals(expected, ifg.getImplementaionIdsForFactory(factoryClass));
	}
}
