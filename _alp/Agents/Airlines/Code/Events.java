void annualUpdate()
{/*ALCODESTART::1759845396881*/
ArrayList<Aircraft> removeList = new ArrayList<Aircraft>();

for(Aircraft a : AircraftList) {
	a.age += 1; 
	if (a.age >= a.lifetime){
		removeList.add(a);
		}
}

for (Aircraft a: removeList){
	updateFleetStock(a.distanceType, a.fuelType, "Remove");
	}

AircraftList.removeIf(a -> a.age >= a.lifetime);
/*ALCODEEND*/}

void monthlyUpdate()
{/*ALCODESTART::1759913428367*/
passengerDemand += main.society.demand;
unitFuelCost.set(main.hydrogenProducer_PEM.hydrogenPrice_per_kg, H2);
/*ALCODEEND*/}

