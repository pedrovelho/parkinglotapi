/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package parking.lot.api;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.IntStream;

/**
 * @author Pedro
 * @since 04/02/18
 */
public class ParkingSlotSet {
    private HashMap<String, ParkingSlot>  slotsAvailableSet;
    private HashMap<String, ParkingSlot>  slotsOccupiedSet;

    /**
     * Instanciate a new set of parking slots.
     *
     * @param maxSlots the number of parking slots to create in this set.
     * @throws NumberFormatException if the parameters is less than 1.
     */
    public ParkingSlotSet(int maxSlots) throws NumberFormatException{
        this.slotsAvailableSet = new HashMap<>();
        this.slotsOccupiedSet = new HashMap<>();

        IntStream.range(0, maxSlots).forEach(
            id -> slotsAvailableSet.put(""+id, new ParkingSlot())
        );

        if(maxSlots < 1){
            throw new NumberFormatException("Need to specify at least 1 slot!");
        }
    }

    /**
     * Just checkin a vehicle if possible, guarantee this is thread safe by
     * using synchronized.
     *
     * @return the id of the obtained parking slot, null if exception occurs.
     *
     * @throws SlotsFullException might throw this exception if the parking is full.
     */
    public synchronized  String checkIn() throws SlotsFullException {
        String slotId;
        //checkin the slot, insert in occupied set, remove from available set
        if(slotsAvailableSet.size() > 0){
            HashMap.Entry<String,ParkingSlot> entry = slotsAvailableSet.entrySet().iterator().next();
            slotId = entry.getKey();
            ParkingSlot parkingSlot = entry.getValue();
            parkingSlot.checkIn();
            slotsOccupiedSet.put(slotId, parkingSlot);
            slotsAvailableSet.remove(slotId);
        }else{
            throw new SlotsFullException("All slots of the current Parking Set are occupied!");
        }
        return slotId;
    }

    /**
     * Checkout a vehicle and return the amount left to pay based on the BillingPolicy.
     *
     * @param id the unique id of the parking slot occupied by the client.
     * @return the price to pay based on the BillingPolicy.
     */
    public synchronized double checkOut(String id, BillingPolicy function) throws SlotNotFoundException {
        ParkingSlot parkingSlot = slotsOccupiedSet.get(id);
        if(parkingSlot == null){
            throw new SlotNotFoundException("Tried to checkOut parkingSlot "+id+" but it is available!");
        }
        long elapsedMinutes = parkingSlot.checkOut();
        slotsOccupiedSet.remove(id);
        slotsAvailableSet.put(id, parkingSlot);
        return function.bill(elapsedMinutes);
    }

    /**
     * Retreive vector containig all the slots id.
     * @return vector with all the slots IDs.
     */
    public Vector<String> getAllSlotsIds() {
        Vector<String> allSlotsIds = new Vector<>();
        slotsOccupiedSet.entrySet().forEach(entry -> allSlotsIds.add(entry.getKey()));
        slotsAvailableSet.entrySet().forEach(entry -> allSlotsIds.add(entry.getKey()));
        return allSlotsIds;
    }
}
