package parking.lot.api;


import java.util.HashMap;
import java.util.List;
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
    public ParkingLotApi(){
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
        if(type.equals(PARKING_STANDARD_TYPE)) {
            parkingStandard.put(""+parkingId, new ParkingSlotSet(numberOfSlots));
        }else if(type.equals(PARKING_20kW_TYPE)) {
            parking20kw.put(""+parkingId, new ParkingSlotSet(numberOfSlots));
        }else if(type.equals(PARKING_50kW_TYPE)) {
            parking50kw.put(""+parkingId, new ParkingSlotSet(numberOfSlots));
        }else{
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
        }else if(type.equals(PARKING_STANDARD_TYPE)) {
            parking = parking20kw.get(""+parkingId);
        }else if(type.equals(PARKING_STANDARD_TYPE)) {
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

     * @param parkingId
     * @return
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
        parkingIdList.addAll(getAllStandardIds());
        parkingIdList.addAll(getAll20kwIds());
        parkingIdList.addAll(getAll50kwIds());
        return parkingIdList;
    }

    /**
     * Retrieve String vector contianing all parking ids of type standard.
     * @return vector containing String ids.
     */
    public Vector<String> getAllStandardIds(){
        Vector<String> parkingIdList = new Vector<>();
        parkingStandard.entrySet().forEach((entry)-> parkingIdList.add(entry.getKey()));
        return parkingIdList;
    }

    /**
     * Retrieve String vector contianing all parking ids of type 20kW.
     * @return vector containing String ids.
     */
    public Vector<String> getAll20kwIds(){
        Vector<String> parkingIdList = new Vector<>();
        parking20kw.entrySet().forEach((entry)-> parkingIdList.add(entry.getKey()));
        return parkingIdList;
    }

    /**
     * Retrieve String vector contianing all parking ids of type 50kW.
     * @return vector containing String ids.
     */
    public Vector<String> getAll50kwIds(){
        Vector<String> parkingIdList = new Vector<>();
        parking50kw.entrySet().forEach((entry)-> parkingIdList.add(entry.getKey()));
        return parkingIdList;
    }

}
