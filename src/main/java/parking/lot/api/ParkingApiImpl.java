package parking.lot.api;


import java.util.HashMap;
import java.util.Vector;

/**
 * Simple API for a parking lot bussiness, 3 parking types are available:
 *
 * - fossil fuel (no outlet for recharging)
 * - 20kw (outlet that can deliver up to 20kW)
 * - 50kw (outlet that can deliver up to 50kw)
 *
 * Instantiate a parking without any parking or slots available, further calls to newParking are
 * needed to create parking of a given type with the slots.
 *
 * @author Pedro
 * @since 04/02/18
 */
public class ParkingApiImpl {

    /**
     * parking type standard, used for fossil fuel cars
     */
    public static final String PARKING_STANDARD_TYPE = "standard";

    /**
     * parking for charging cars with 20kW outlet
     */
    public static final String PARKING_20kW_TYPE = "20kW";

    /**
     * parking for charging cars with 50kW outlet
     */
    public static final String PARKING_50kW_TYPE = "50kW";

    private HashMap<String, ParkingSlotSet> parkingStandard;
    private HashMap<String, ParkingSlotSet> parking20kw;
    private HashMap<String, ParkingSlotSet> parking50kw;
    private static int uniqueIdCounter = 1;

    /**
     * Create object to hold the parking
     */
    ParkingApiImpl(){
        parkingStandard = new HashMap<>();
        parking20kw = new HashMap<>();
        parking50kw = new HashMap<>();
    }

    /**
     * Create a new parking lot with available slots.
     *
     * @param numberOfSlots the desired number of slots to create.
     * @return an unique id for the newly created parking lot.
     */
    public String newParking(int numberOfSlots, String type) throws UnknownParkingTypeException {
        String parkingId = ""+uniqueIdCounter;
        if (PARKING_STANDARD_TYPE.equals(type)) {
            parkingStandard.put("" + parkingId, new ParkingSlotSet(numberOfSlots));
        } else if (PARKING_20kW_TYPE.equals(type)) {
            parking20kw.put("" + parkingId, new ParkingSlotSet(numberOfSlots));

        } else if (PARKING_50kW_TYPE.equals(type)) {
            parking50kw.put("" + parkingId, new ParkingSlotSet(numberOfSlots));

        } else {
            throw new UnknownParkingTypeException("Parking type required does not exists!");
        }
        uniqueIdCounter++;
        return parkingId;
    }

    /**
     * Use this method to checking a car for a specific parking.
     *
     * @param parkingId id of the parking lot.
     * @param type type of the required slot.
     * @return id of the parking slot aquired.
     * @throws UnknownParkingTypeException if the type is not one of the three possible types.
     * @throws UnknowParkingIdException if the id is not a registered parking lot.
     * @throws SlotsFullException if the parking has no available spots of the
     */
    public String checkIn(String parkingId, String type) throws UnknownParkingTypeException, UnknowParkingIdException, SlotsFullException {
        ParkingSlotSet parking;
        if(type.equals(PARKING_STANDARD_TYPE)) {
            parking = parkingStandard.get(""+parkingId);
        }else if(type.equals(PARKING_20kW_TYPE)) {
            parking = parking20kw.get(""+parkingId);
        }else if(type.equals(PARKING_50kW_TYPE)) {
            parking = parking50kw.get(""+parkingId);
        }else{
            throw new UnknownParkingTypeException("Parking type required does not exists!");
        }
        if(parking == null){
            throw new UnknowParkingIdException("The requested parking ID does not exists!"+
                    "You can retrieve a complete list of available parking ids with getParkingsList!");
        }
        return parking.checkIn();
    }

    /**
     * Checkout a car occuping slotId from the parking identified by parkingId.
     * Apply the lambda expression desired as billing policy.
     *
     * @param parkingId unique id of the parking lot.
     * @param slotId unique id of the car slot.
     * @param function the price policy to apply based on the amount of minutes the car stayed.
     * @return the price to pay.
     * @throws UnknowParkingIdException might happen if the parkingId does not exists.
     * @throws SlotNotFoundException might happen if the slot is not available for the current parkingSlot.
     */
    public double checkOut(String parkingId, String slotId, BillingPolicy function) throws UnknowParkingIdException, SlotNotFoundException {
        ParkingSlotSet parking = findParkingById(parkingId);
        if(parking == null){
            throw new UnknowParkingIdException("The parking your are trying to check out does not exists!");
        }
        return parking.checkOut(slotId, function);
    }

    /**
     * Find the ParkingSlotSet given an id.
     *
     * @param parkingId unique id of the searched parking lot.
     * @return a ParkingSlotSet object or null if not found.
     */
    private ParkingSlotSet findParkingById(String parkingId){
        ParkingSlotSet parking = parkingStandard.get(parkingId);
        //tries other type if null
        if(parking == null){
            parking = parking20kw.get(parkingId);
        }
        if(parking == null){
            parking = parking50kw.get(parkingId);
        }
        return parking;
    }

    /**
     * Checkout a car occuping slotId from the parking identified by parkingId.
     * Use the billingPolicy associated with the parkingId. If billing policy
     * was not previously set throws exception.
     *
     * @param parkingId unique id of the parking lot.
     * @param slotId unique id of the car slot.
     * @return the price to pay.
     * @throws UnknowParkingIdException might happen if the parkingId does not exists.
     * @throws SlotNotFoundException might happen if the slot is not available for the current parkingSlot.
     * @throws BillingPolicyNotSetException rised when setBillingPolicy was not properly called.
     */
    public double checkOut(String parkingId, String slotId) throws UnknowParkingIdException, SlotNotFoundException, BillingPolicyNotSetException {
        ParkingSlotSet parking = findParkingById(parkingId);
        if(parking == null){
            throw new UnknowParkingIdException("The parking your are trying to check out does not exists!");
        }
        return parking.checkOut(slotId);
    }

    /**
     * Specify a BillingPolicy to compute the price upon subsequent calls to checkOut omissing the
     * parameter each time.
     *
     * @param parkingId the id of the parking lot.
     * @param billingPolicy an instance of the interface or lambda expression.
     *                   examples:
     *                      - Bill 1.5 per hours
     *                      <code>
     *                      setBillingPolicy(parkingId, (minutes) -> {
     *                          return (minutes/60) * 1.5; //charge 1.5 the hour
     *                      });
     *                      </code>
     *                      - First hour free, next hour 1.8, after 0.85 cents each 15 minutes
     *                      <code>
     *                      for instances:
     *                      setBillingPolicy(parkingId, (minutes) -> {
     *                          if(minutes < 60){
     *                              return 0;
     *                          }else if(minutes >= 60 && minutes < 120){
     *                              return 1.8;
     *                          }else {
     *                              return ((minutes-120)/15)*0.85+1.8;
     *                          }
     *                      });
     *                      </code>
     *
     * @throws UnknownParkingTypeException
     */
    public void setBillingPolicy(String parkingId, BillingPolicy billingPolicy) throws UnknownParkingTypeException {
        ParkingSlotSet parking = findParkingById(parkingId);
        if(parking == null){
            throw new UnknownParkingTypeException("Unable to find the parking lot for setting BillingPolicy!");
        }
        parking.setBillingPolicy(billingPolicy);
    }

    /**
     * Retrieve all the Ids for slots of a given parking lot.
     * @param parkingId unique id of parking lot of interest.
     * @return a vector with all ids.
     */
    public Vector<String> getAllSlotsIds(String parkingId) throws UnknowParkingIdException {
        ParkingSlotSet parking = parkingStandard.get(parkingId);
        //tries other type if null
        if(parking == null){
            parking = parking20kw.get(parkingId);
        }
        if(parking == null){
            parking = parking50kw.get(parkingId);
        }
        if(parking == null){
            throw new UnknowParkingIdException("The parking your are trying to check out does not exists!");
        }
        return parking.getAllSlotsIds();
    }

    /**
     * Retrieve String vector contianing all parking ids.
     * @return vector containing String ids.
     */
    public Vector<String> getAllParkingIds(){
        Vector<String> parkingIdList = new Vector<>();
        try {
            parkingIdList.addAll(getAllIdsForType(PARKING_STANDARD_TYPE));
            parkingIdList.addAll(getAllIdsForType(PARKING_20kW_TYPE));
            parkingIdList.addAll(getAllIdsForType(PARKING_50kW_TYPE));
        } catch (UnknownParkingTypeException e) {
            e.printStackTrace();
        }
        return parkingIdList;
    }

    /**
     * Retrieve String vector contianing all parking ids of given type.
     * @param type a string that is one of the accepted types.
     * @return vector containing String ids.
     */
    private Vector<String> getAllIdsForType(String type) throws UnknownParkingTypeException {
        Vector<String> parkingIdList = new Vector<>();
        if (type.equals(PARKING_STANDARD_TYPE)) {
            parkingStandard.forEach((key, value) -> parkingIdList.add(key));
        } else if (type.equals(PARKING_20kW_TYPE)) {
            parking20kw.forEach((key, value) -> parkingIdList.add(key));
        } else if (type.equals(PARKING_50kW_TYPE)) {
            parking50kw.forEach((key, value) -> parkingIdList.add(key));
        } else {
            throw new UnknownParkingTypeException("Parking type required does not exists!");
        }
        return parkingIdList;
    }
}
