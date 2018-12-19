## How to use JUnit Mockito(Noobie explanation)
Back on the old Java days, I didn't even knew what JUnit was. My testing process was:
 - step 1: Run
 - step 2: If some red color appears that meant that It didn't worked
 
But now with JUnit we have a great tool for testing, however, this tool has some limitations... For example: If we have a object that is populated by a database, it is not convenient to query that database everytime just to test a particular method or if we use a method to do some math operation from cloud service... So or we re-implement the method that receives that object for testing(which in my opinion it shouldn't be a consequence) or we just use *Mockito*.


## Maven dependencies

* Using [JUnit](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.3.1)
* Using [Mockito](https://mvnrepository.com/artifact/org.mockito/mockito-all/1.10.19)

## Code snipets

Imagine that you have a `Shipment` object that contains several information and that information is populate with a DB query and for a good pratice your operations are made over a `Shipment interface`:

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

The curious part is that the `Shipment` interface has another interface nested, `Address` interface.

The operations that are going to be made n the `Shipment` interface is going to be called `AddressParser`, which basically will have 2 methods implemented to mess around with the information that the `Address` interface has:

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

The methods doesn't need any introductions, since that they have some comments.

## Why Mockito in JUnit?!

Now that we have the project implemented, we need to test this... But how do we do it without creating a Db connection to populate the `Shipment`? We use Mockito:

Mockito is used for other scenarios, but for this one we are going to use it to create method stubs for the `Shipment` interface.
The explanation is going to follow the *Watch first and understand later* method:

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

As you can see we are using the `@Mock` annotation for the `Shipment` and `Address` interface. Basically by reflection, what the Mockito is doing is creating stubs of methods for those classes, dummy data but without data actually...
For example: if you try to use the method `setShipmentReference("1234F")` it will not do anything if you didn't mock a rule for this method(`Mockito.when(...)`). But in a forensic perspective the @Mock annotation is doing these operations:
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
otherwise if you run the unit tests, it will always fail.

### @Before

Then... we have the `setup` method which is assigned as a *Before* JUnit method, that has 2 Mockito rules:
```java
...
Mockito.when(shipment.getReceiverAddress()).thenReturn(address);
Mockito.when(shipment.getSenderAddress()).thenReturn(address);
...
```
Which basically what those rules are saying in a common language is: Yeiii, when the `getReceiverAddress()` method is called return the address Mocked object please... Ahh... And when `getSenderAddress()` return again the same object. This way before we add a new Mockito rule for the receiver and the sender object no `NullpointerException` is thrown.

Then we just pass the `Shipment` object like it was retrieved from the database.

### @Test methods

Now we need to give more conditions to Mockito in order to be able to test what ever we want with the `AddressParser` methods.

```java
...
Mockito.when(shipment.getReceiverAddress().getStreet1()).thenReturn("Street 1 test");
Assert.assertEquals(addressParser.substringOneRecvAddress(), "treet 1 test");
...
```

To test the `substringOneRecvAddress()` method we use other rule for the chained method call `shipment.getReceiverAddress().getStreet1()`... Return the "Street 1 test" String. Then we just use JUnit like in the old days. 

```java
...
Mockito.when(shipment.getSenderAddress().getStreet2()).thenReturn("Street 2 test");
Assert.assertEquals(addressParser.upperCaseStreetTwoSender(), "STREET 2 TEST");
...
```
The same thing happens with the `upperCaseStreetTwoSender()` method, first provide a rule with the information that you want to as a return value and then "JUnit it".


## References

If you think that my explanation is not enough(Which is the most probable thing) you can consult more basic or complex documentations:

* Vogella tutorial [Vogella](http://www.vogella.com/tutorials/Mockito/article.html)
* Official Mockito website [Mockito](https://site.mockito.org/)
