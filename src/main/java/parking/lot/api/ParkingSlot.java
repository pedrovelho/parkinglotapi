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

import org.joda.time.DateTime;

/**
 * @author Pedro
 * @since 04/02/18
 */
public class ParkingSlot  {
    /**
     * Keep tracks when this parking lot is available.
     */
    private boolean available;

    /**
     * Time it started last checkin.
     */
    private DateTime startTime;

    /**
     * Create a slot and set it as available.
     */
    ParkingSlot(){
        this.available = false;
    }

    /**
     * Simple put just make the slot unavailable and start the timer.
     */
    public void checkIn(){
        this.available = false;
        startTime = new DateTime();
    }

    /**
     * Checkout a car, consider the time as now and return the amount of minutes
     * the car stayed.
     *
     * @return amount of minutes the car stayed on the slot.
     */
    public long checkOut(){
        this.available = true;
        long elapsedMillis = (new DateTime()).getMillis() - startTime.getMillis();
        //convert in minutes
        return (elapsedMillis/1000)/60;
    }
}
