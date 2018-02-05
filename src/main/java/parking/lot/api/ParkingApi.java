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

import java.util.Vector;

/**
* @author Pedro
* @since 04/02/18
*/
public class ParkingApi {

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

    ParkingApiImpl parkingImpl;

    public ParkingApi(){
        parkingImpl = new ParkingApiImpl();
    }

    /**
     * Create a new parking lot with available slots.
     *
     * @param numberOfSlots the desired number of slots to create.
     * @return an unique id for the newly created parking lot.
     */
    public String newParking(int numberOfSlots, String type) throws UnknownParkingTypeException {
        return parkingImpl.newParking(numberOfSlots, type);
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
        return parkingImpl.checkIn(parkingId, type);
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
        return parkingImpl.checkOut(parkingId, slotId, function);
    }

    /**
     * Retrieve all the Ids for slots of a given parking lot.
     * @param parkingId unique id of parking lot of interest.
     * @return a vector with all ids.
     */
    public Vector<String> getAllSlotsIds(String parkingId) throws UnknowParkingIdException {
        return parkingImpl.getAllSlotsIds(parkingId);
    }

    /**
     * Retrieve String vector contianing all parking ids.
     * @return vector containing String ids.
     */
    public Vector<String> getAllParkingIds(){
        return parkingImpl.getAllParkingIds();
    }

    /**
     * Set the billing policy so you can use checkout omitting the billingPolicy parameter.
     * @param parkingId the parking id to set the billing policy.
     * @param billingPolicy the billingPolicy to set.
     */
    public void setBillingPolicy(String parkingId, BillingPolicy billingPolicy) throws UnknownParkingTypeException {
        parkingImpl.setBillingPolicy(parkingId, billingPolicy);
    }
}

