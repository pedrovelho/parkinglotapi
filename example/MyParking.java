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
