# parkinglotapi

A simple parking lot API example. 

Java API that meets the following requirements : 

A toll parking contains multiple parking slots of different types : 
the standard parking slots for sedan cars (gasoline-powered)
parking slots with 20kw power supply for electric cars 
parking slots with 50kw power supply for electric cars

20kw electric cars cannot use 50kw power supplies and vice-versa.

Every Parking is free to implement is own pricing policy :
- Some only bills their customer for each hour spent in the parking (nb hours * hour price)
- Some other bill a fixed amount + each hour spent in the parking (fixed amount + nb hours * hour price)
In the future, there will be others pricing policies.

Cars of all types come in and out randomly, the API must : 
- Send them to the right parking slot of refuse them if there is no slot (of the right type) left.
- Mark the parking slot as Free when the car leaves it
- Bill the customer when the car leaves.


# How to compile

Checkout last version from github.

```ssh
git clone https://github.com/pedrovelho/parkinglotapi.git
```

A directory parkinglotapi will appear with the project content
To compile just use the gradlew script.

```ssh
cd parkinglotapi
./gradlew clean build test
```

If runs fine you should see the message below.

```ssh
BUILD SUCCESSFUL in 1m 3s
5 actionable tasks: 5 executed
```

You also can verify that the release file .jar is present in your path.

```ssh
ls build/libs/parkinglotapi-1.0-SNAPSHOT.jar
```

# How to use

To use the API you can import the package and compile your
code setting the jar on the classpath. Example create a file 'MyParking.java' and copy the content below.

```java
import parking.lot.api.*;

public class MyParking {
    public static void main(String args[]){

	String parkingIdStandard = "";
	String parkingId50kw = "";
	ParkingApi myApi = null;
	
	try {
            myApi = new ParkingApi();
	    //create 10 slots for parking type standard
            parkingIdStandard = myApi.newParking(10, ParkingApi.PARKING_STANDARD_TYPE);
	    //create 10 slots for parking type 50kw
            parkingId50kw = myApi.newParking(10, ParkingApi.PARKING_50kW_TYPE);
	}catch (UnknownParkingTypeException e) {
            e.printStackTrace();
        }

	String slotId = "";
	try{
	    //check in a standard car
            slotId = myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);
	}catch (UnknownParkingTypeException | UnknowParkingIdException e) {
            e.printStackTrace();
        }catch (SlotsFullException e){
	    System.out.println("No slots available for the required type!");
	}

	long clockFake = 130; // 2 hours 10 minutes
	
	//checkout using a complex billingPolicy:
	//First hour free, next hour 1.8, after 0.85 cents each 15 minutes
	double price = 0.0;
	try{
	    price = myApi.checkOut(parkingIdStandard, slotId, (long min) -> {
		    min += clockFake;
		    System.out.println("Elapsed minutes "+min);
		    if(min < 60){
			return 0;
		    }else if(min >= 60 && min < 120){
			return 1.8;
		    }else {
			return ((min-120)/15)*0.85+1.8;
		    }
		});
	}catch (SlotNotFoundException | UnknowParkingIdException e) {
            e.printStackTrace();
        }

	//double error accepted
	double delta = 0.0001;
	
	if(1.8-delta < price && price < 1.8+delta){
	    System.out.println("It is working AWSOME!!!");
	}else{
	    System.out.println("PROBLEMS FOUND!!!");
	}
    }
}
``` 

To compile this program just use the jar download or build locally.

```bash
javac -cp build/libs/parkinglotapi-1.0-SNAPSHOT.jar MyParking.java
```

For executing the program we also need the joda-time package. This
will be available in your .gradle/cache. You can find a local
copy searching on your .gradle/cache folder or you can [download it
from sourceForge here](https://sourceforge.net/projects/joda-time/files/joda-time/2.4/).
Once you have a joda-time properly set you can run the example program as below.

```bash
java -cp build/libs/parkinglotapi-1.0-SNAPSHOT.jar:joda-time-2.9.1.jar:. MyParking
```

If you see 

```
Elapsed minutes 130
It is working AWSOME!!!
```

It is working ;)

# API doc

## Starting a Parking business

```java
import parking.lot.api.*;
[...]
myApi = new ParkingApi();
```

## Adding a new parking lot of desired type

Method ``newParking`` will add slots of given parking type. 
The three types availabe are defined by the constantes:

```
ParkingApi.PARKING_STANDARD_TYPE
ParkingApi.PARKING_20kW_TYPE
ParkingApi.PARKING_50kW_TYPE
```

For instance the code below will create 10 slots of parking for the standard fossil fuel type.
The result of newParking is the ID String, this string must be kept to reference the parking.

```java
parkingIdStandard = myApi.newParking(10, ParkingApi.PARKING_STANDARD_TYPE);
```

## Setting a billing policy for your new parking lot

After creating a parking lot you can add a
specific billing policy to the parking lot. 
Upon checkout the billingPolicy will access the amount of elapsed time since check in.
All polices must use the BillingPolicy Interface,
for your convinience you can use lambda, if using java 8 or above.

For instance if you want to associate the parking (referenced by
parknigId) for a policy where the first hour is free, 
the next hour cost 1.8, and after the users pay 0.85 cents each 15 minutes
the settingBillingPolicy can be called like this:

```java
myApi.setBillingPolicy(parkingId, (long min) -> {
		    System.out.println("Elapsed minutes "+min);
		    if(min < 60){
		        return 0;
		    }else if(min >= 60 && min < 120){
		        return 1.8;
		    }else {
		        return ((min-120)/15)*0.85+1.8;
		    }
		});
```

## Check in (THREAD SAFE)

Within he same parking all checkIn and checkOut operations are
synchronized to guarantee atomicity.
For checkin you need to specify the type of the desired
slot and the parkingId. The API will throw *UnknownParkingTypeException* if 
the desired type does not match the parking type. It can
also rise *UnknowParkingIdException* if the parkingId
does not exists. If the desired parking lot is full a *SlotsFullException*. Handle this exception at your convinience to alert users that the parking is full.
See this example below.

```java
String slotId = myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);
```

Upon success ``checkIn`` returns an unique slotId within the parking lot.
This id needs to be stored to properly check out and bill the car. For your
convinice you can print the slotId in parking card.

## Check out (THREAD SAFE)

Two ``checkOut`` methods signature can be used, with or without the BillingPolicy parameters.
Besides *UnknownParkingTypeException* and  *UnknowParkingIdException*, the
this method can also rise a *BillingPolicyNotSetException*. This
last exception mean that there is not BillingPolicy set for the
given parkingId. Upon success checkout will return
the price to pay and free the parking lot.

```java
//might rise BillingPolicyNotSetException
double price = myApi.checkOut(parkingIdStandard, slotId);

//might specify billing policy upon checkOut
double price = myApi.checkOut(parkingIdStandard, (long min) -> (min/60)*1.5);
```

## Retrieve ids

For maintanance purposes you can retrieve a complete list of all parking lots ids.

```java
myApi.getAllParkingIds();
```

You can also retrieve a complete list of all slots for a given parking.

```java
myApi.getAllSlotsIds(parkingId);
```
