double getDemand()
{/*ALCODESTART::1752825159842*/
for (int i = 1; i <= Sectors.size(); i++){
	sectorDemand.set(main.energyDemand.TotalEnergyDemand.get(i, Electricity), i, Electricity);
	double dem = main.energyDemand.TotalEnergyDemand.get(i, Hydrogen);
	sectorDemand.set( (dem >=0 ? dem:0)	, i, Hydrogen);
	}

//cal total electricity demand
electricityDemand = sectorDemand.sum(INDEX_CAN_VARY, Electricity)
+ main.hydrogenProducer_PEM.annualElectricityDemand_MWh/1000000;

//cal total hydrogen demand 
electricityDemand_H2_Agent = main.hydrogenProducer_PEM.annualElectricityDemand_MWh/1000000;
hydrogenDemand = (sectorDemand.sum(INDEX_CAN_VARY, Hydrogen));
/*ALCODEEND*/}

double getSupply()
{/*ALCODESTART::1752831750595*/
availElectricitySupply = main.energySupply.electricitySupply.totalElectricitySupply;
//availElectricitySupply = 230;
availHydrogenSupply = (main.hydrogenProducer_PEM.annualCapacity_TWh);

/*ALCODEEND*/}

double electricity_priority_allocation()
{/*ALCODESTART::1752832413727*/
double availableElectricity = availElectricitySupply;
double allocated;
double ratio;
for (int i = 1; i <= Sectors.size(); i++){
	double demand = sectorDemand.get(i, Electricity);
	
	if (availableElectricity >= demand){
		allocated = demand;
		ratio = zidz(allocated, demand);
		sectorAllocated.set(allocated, i, Electricity);
		electProportionAllocated.set(ratio, i);
		availableElectricity -= allocated;
	} else {
		allocated = availableElectricity;
		ratio = zidz(allocated, demand);
		sectorAllocated.set(allocated, i, Electricity);
		electProportionAllocated.set(ratio, i);
		availableElectricity -= allocated;
	}
	
}

/*ALCODEEND*/}

double hydrogen_priority_allocation()
{/*ALCODESTART::1753099872218*/
double availableHydrogen = availHydrogenSupply;
double allocated;
double ratio;
for (int i = 1; i <= Sectors.size(); i++){
	double demand = sectorDemand.get(i, Hydrogen);
	//traceln("Sector: "+i +" "+demand);
	if (availableHydrogen >= demand){
		allocated = demand;
		ratio = xidz(allocated, demand,0.0001);
		sectorAllocated.set(allocated, i, Hydrogen);
		hydrogenProportionAllocated.set(ratio, i);
		availableHydrogen -= allocated;
	} else {
		allocated = availableHydrogen;
		ratio = zidz(allocated, demand);
		sectorAllocated.set(allocated, i, Hydrogen);
		hydrogenProportionAllocated.set(ratio, i);
		availableHydrogen -= allocated;
	}
	
}

/*ALCODEEND*/}

double hydrogen_proportional_allocation()
{/*ALCODESTART::1758703662672*/
double availableHydrogen = availHydrogenSupply;
double allocated;
double totalDemand = hydrogenDemand;
if (availableHydrogen!= 0 && totalDemand != 0){
	double ratio = availableHydrogen/totalDemand;

if (ratio >=1 ){
	for (int i = 1; i <= Sectors.size(); i++){
		double demand = sectorDemand.get(i, Hydrogen);
		//traceln("Sector: "+i +" "+demand);
		allocated = demand;
		ratio = zidz(allocated, demand);
		sectorAllocated.set(allocated, i, Hydrogen);
		hydrogenProportionAllocated.set(ratio, i);
		availableHydrogen -= allocated;
	}
} else {
	for (int i = 1; i <= Sectors.size(); i++){
		double demand = sectorDemand.get(i, Hydrogen);
		//traceln("Sector: "+i +" "+demand);
		allocated = ratio*demand;
		sectorAllocated.set(allocated, i, Hydrogen);
		if (demand !=0){
			hydrogenProportionAllocated.set(ratio, i);
			}
		availableHydrogen -= allocated;
	}
	
}

} 
/*ALCODEEND*/}

double electricity_proportional_allocation()
{/*ALCODESTART::1758704255858*/
double availableElectricity = availElectricitySupply;
double allocated;
double totalDemand = electricityDemand;
double ratio = availableElectricity/totalDemand;
//more supply than demand
if (ratio >=1 ){
	for (int i = 1; i <= Sectors.size(); i++){
		double demand = sectorDemand.get(i, Electricity);
		//traceln("Sector demand : "+i +" is: "+demand);
		allocated = demand;
		//traceln("Sector allocated: "+i +" is: "+allocated);
		ratio = zidz(allocated, demand);
		sectorAllocated.set(allocated, i, Electricity);
		if (i == 7){
			main.hydrogenProducer_PEM.allocated_electricity_MW = allocated*1_000_000; // converting to MW from TW
			}
		electProportionAllocated.set(ratio, i);
		availableElectricity -= allocated;
	}
} else {
	for (int i = 1; i <= Sectors.size(); i++){
		double demand = sectorDemand.get(i, Electricity);
		//traceln("Sector: "+i +" "+demand);
		allocated = ratio*demand;
		//traceln("Sector: "+i +" "+allocated);
		//traceln("=================================");
		sectorAllocated.set(allocated, i, Electricity);
		if (i == 7){
			main.hydrogenProducer_PEM.allocated_electricity_MW = allocated*1_000_000;
			}
		electProportionAllocated.set(ratio, i);
		availableElectricity -= allocated;
	}
	


} 
/*ALCODEEND*/}

double initActiveBaseLF()
{/*ALCODESTART::1792523273026*/
if (activeBaseLFSet) return;
double d0 = 262.84;  //totalElectricityDemand;      // TWh, first year, before any transition
double s0 = 693.4;          //totalElectricitySupply;      // TWh, same year
if (s0 > 0) {
    activeBaseLF = calibrateBaseLF ? d0 / s0 : baseLoadFactor;
    activeBaseLFSet = true;
    traceln("Active base load factor = " + activeBaseLF + " (D0=" + d0 + ", S0=" + s0 + ")");
}
/*ALCODEEND*/}

