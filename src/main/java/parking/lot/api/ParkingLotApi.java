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
public class ParkingLotApi {

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
    ParkingLotApi(){
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

        return parking.checkOut(slotId, function);
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
