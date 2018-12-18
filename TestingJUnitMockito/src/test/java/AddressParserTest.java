import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.juanramos.testingjunitmockito.AddressParser;
import com.juanramos.testingjunitmockito.Shipment;
import com.juanramos.testingjunitmockito.Shipment.Address;

public class AddressParserTest {
	@Mock Shipment shipment;
	@Mock Address address;
	
	AddressParser addressParser;
	
	@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Before
	public void setup() {
		Mockito.when(shipment.getReceiverAddress()).thenReturn(address);
		Mockito.when(shipment.getSenderAddress()).thenReturn(address);
		addressParser = new AddressParser(shipment);      
	} 
	
	@Test
	public void testSubstringStreetOneRecvAddress() {
		Mockito.when(shipment.getReceiverAddress().getStreet1()).thenReturn("Street 1 test");
		Assert.assertEquals(addressParser.substringOneRecvAddress(), "treet 1 test");
		
	}
	
	@Test
	public void testUpperCaseStreetTwoSender() {
		Mockito.when(shipment.getSenderAddress().getStreet2()).thenReturn("Street 2 test");
		Assert.assertEquals(addressParser.upperCaseStreetTwoSender(), "STREET 2 TEST");
	}
}
