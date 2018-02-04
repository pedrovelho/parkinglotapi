package parking.lot.api;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ActiveEon Team
 * @since 04/02/18
 */
public class ParkingSlotTest {

    ParkingSlot slot;
    String id;

    @Before
    public void setUp() throws Exception {
        slot = new ParkingSlot();
    }

    @Test
    public void checkIn() {
        try {
            slot.checkIn();
        }catch (Exception e){
            e.printStackTrace();
            fail("Does not expect exceptions!");
        }
    }

    @Test
    public void checkOut() {
        slot.checkIn();
        try {
            Thread.sleep(60020);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, slot.checkOut());
    }
}