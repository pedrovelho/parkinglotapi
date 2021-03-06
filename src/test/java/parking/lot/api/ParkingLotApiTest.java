package parking.lot.api;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Pedro
 * @since 04/02/18
 */
public class ParkingLotApiTest {

    @Test
    public void newParkingSuccess() {

        try {
            ParkingApiImpl myApi = new ParkingApiImpl();

            String parkingIdStandard = myApi.newParking(10, ParkingApi.PARKING_STANDARD_TYPE);
            String parkingId20kw = myApi.newParking(10, ParkingApi.PARKING_20kW_TYPE);
            String parkingId50kw = myApi.newParking(10, ParkingApi.PARKING_50kW_TYPE);

            Assert.assertEquals(myApi.getAllParkingIds().size(), 3);
            Assert.assertEquals(myApi.getAllSlotsIds(parkingIdStandard).size(), 10);
            Assert.assertEquals(myApi.getAllSlotsIds(parkingId20kw).size(), 10);
            Assert.assertEquals(myApi.getAllSlotsIds(parkingId50kw).size(), 10);

        } catch (UnknownParkingTypeException | UnknowParkingIdException e) {
            e.printStackTrace();
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void checkInSuccess() {
        try {
            ParkingApiImpl myApi = new ParkingApiImpl();
            String parkingIdStandard = myApi.newParking(10, ParkingApi.PARKING_STANDARD_TYPE);

            myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);
            myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);
            String slotId = myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);

            Assert.assertEquals("3", slotId);
        }catch (UnknownParkingTypeException | SlotsFullException | UnknowParkingIdException e) {
            e.printStackTrace();
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void checkOutSuccess()  {
        try {
            ParkingApiImpl myApi = new ParkingApiImpl();
            String parkingIdStandard = myApi.newParking(10, ParkingApi.PARKING_STANDARD_TYPE);

            String slotId = myApi.checkIn(parkingIdStandard, ParkingApi.PARKING_STANDARD_TYPE);

            double price = myApi.checkOut(parkingIdStandard, slotId, (long min) -> {
                System.out.println("Elapsed minutes "+min);
                if(min == 0){
                    return 15.0;
                }else{
                    return 30.0;
                }
            });

            Assert.assertEquals(15, price, 0.001);
        }catch (UnknownParkingTypeException | SlotsFullException | UnknowParkingIdException | SlotNotFoundException e) {
            e.printStackTrace();
            fail("Should not have thrown any exception");
        }
    }

}