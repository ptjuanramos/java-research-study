## How to use JUnit Mockito(Noobie explanation)
Back on the old Java days, I didn't even know what JUnit was. My testing process consisted in:
 - step 1: Run
 - step 2: If somethign showed up highlighted in red, it meant that it didn't worked
 
But now with JUnit we have a great tool for testing, no more excuses to create propper unit tests. However, this tool is not free of limitations. For example: If we have an object, it is not convenient to query that database whenever we need to test a particular method or employ one of those methods to perform some mathematical operation from the cloud service. So, to solve this, we are left with two options: either we re-implement the method that receives that object for testing – which, in my opinion it shouldn't be a consequence of unit test creation, or we simply use *Mockito*.


## Maven dependencies

* Using [JUnit](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.3.1)
* Using [Mockito](https://mvnrepository.com/artifact/org.mockito/mockito-all/1.10.19)

## Code snipets

Imagine that you have a `Shipment` object containing several pieces of information. That information is populate with a DB query and as part of aiming for good pratice, your operations are made over `Shipment interface`:

```java
public interface Shipment {
  public String getShipmentId();
  public void setShipmentId(String shipmentId);
  public void setShipmentReference(String shipmentReference);
  public String getShipmentReference();
  public Address getReceiverAddress();
  public void setReceiverAddress(Address receiverAddress);
  public Address getSenderAddress();
  public void setSenderAddress(Address senderAddress);

  public interface Address {
    String getStreet1();
    String getStreet2();
  }
}
```
The curious thing about this case is that the `Shipment` interface contains another interface nested within it – the `Address` interface.

`AddressParser` was the term chosen to refer to the operations made in the `Shipment` interface. In essence, it consists in two methods implemented to “mess around” with the information accommodated in the `Address` interface:

```java
public class AddressParser {
  private Shipment shipment;

  public AddressParser(Shipment shipment) {
    this.shipment = shipment;
  }

  /**
  * Substring of street1 property from the Receiver address from the index 1
  */
  public String substringOneRecvAddress() {
    return shipment.getReceiverAddress().getStreet1().substring(1);
  }

  /**
  * Returns the Upper case sender street 2
  */
  public String upperCaseStreetTwoSender() {
    return shipment.getSenderAddress().getStreet2().toUpperCase();
  }
}
```

The methods themselves don’t need any further introductions, since their comments already contain the needed explanations, as shown above. 

## Why Mockito in JUnit?!

Now that we have the project implemented, we need to test it... But how do we achieve that without creating a Db connection to populate the `Shipment`? It’s simple: we use *Mockito*:

Although *Mockito* is usually used for other scenarios, we are going to employ it here in order to create method stubs for the `Shipment` interface. Before any lengthy explanations, let’s just follow the *Watch first and understand later* method for now: 

```java
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
```

As you can see we are using the `@Mock` annotation for both `Shipment` and the `Address` interfaces. Basically by reflection operations, Mockito is creating methods subts for those classes, or in other words, creating dummy data but without any actual data.
For instance, if you try to use the method `setShipmentReference("1234F")` it will not do anything unless you have previously mocked a rule for this method (`Mockito.when(...)`). But from a forensic perspective, the @Mock annotation is performing the following operations:

```java
...
Shipment = Mockito.mock(Shipment.class);
Address address = Mockito.mock(Address.class);
...

```
However, if you decide to use the Mockito annotations, you cannot forget this @Rule:

```java
...
@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
...
```
Otherwise, whenever you run the unit tests, it will always fail.

### @Before

In addition, we have the `setup` method which is assigned as a *Before* JUnit method, that has 2 Mockito rules:

```java
...
Mockito.when(shipment.getReceiverAddress()).thenReturn(address);
Mockito.when(shipment.getSenderAddress()).thenReturn(address);
...
```
Essentially, it is as if these rules are saying something of the sorts:

“Eiii, whenever the  `getReceiverAddress()` method is called, return the address Mocked object please... Ahh... And every time we call `getSenderAddress()`, return the same object again.”

This way, before we add a new Mockito rule for the receiver and the sender objects no `NullpointerException` is thrown.
Then, we just pass the `Shipment` object like it was retrieved from the database.


### @Test methods

Now we need to give more conditions to Mockito in order to be able to test whatever we want with the `AddressParser` methods.

```java
...
Mockito.when(shipment.getReceiverAddress().getStreet1()).thenReturn("Street 1 test");
Assert.assertEquals(addressParser.substringOneRecvAddress(), "treet 1 test");
...
```

To test the `substringOneRecvAddress()` method we use different rule for the chained method call `shipment.getReceiverAddress().getStreet1()`... First, return the "Street 1 test" String. Then we just use JUnit the way we would in the old days. 

```java
...
Mockito.when(shipment.getSenderAddress().getStreet2()).thenReturn("Street 2 test");
Assert.assertEquals(addressParser.upperCaseStreetTwoSender(), "STREET 2 TEST");
...
```
The same applies to the `upperCaseStreetTwoSender()` method. First, provide a rule with the information that you want to implement as return value, and after that “JUnit it”.

## References

If you find that my explanation is not thorough enough (which is probably the case), here are some additional sources of documentation that you can consult, ranging from basic to more complex levels of understanding:

* Vogella tutorial [Vogella](http://www.vogella.com/tutorials/Mockito/article.html)
* Official Mockito website [Mockito](https://site.mockito.org/)
