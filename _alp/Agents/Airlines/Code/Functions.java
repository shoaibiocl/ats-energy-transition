double updateFleetStock(String FlightDistance,String Fuel,String operation)
{/*ALCODESTART::1759852923880*/
int iCol;
int iRow;
if      ("Long".equalsIgnoreCase(FlightDistance))  iCol = 1;
else if ("Medium".equalsIgnoreCase(FlightDistance)) iCol = 2;
else                                                iCol = 3; // assume "Short"

if      ("Kerosene".equalsIgnoreCase(Fuel))   iRow = 1;
else if ("SAF".equalsIgnoreCase(Fuel))        iRow = 2;
else if ("H2".equalsIgnoreCase(Fuel))   	  iRow = 3;
else if ("CH3OH".equalsIgnoreCase(Fuel))      iRow = 4;
else                                          iRow = 5; // assume "ammonia"

if ("Add".equalsIgnoreCase(operation)){
	operationalFleet.set(operationalFleet.sum(iRow, iCol)+1, iRow, iCol);
	} else {
	operationalFleet.set(operationalFleet.sum(iRow, iCol)-1, iRow, iCol);
	}


/*ALCODEEND*/}

