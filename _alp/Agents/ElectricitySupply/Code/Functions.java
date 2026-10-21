double capacity_increase()
{/*ALCODESTART::1760090380665*/
//nuclear
Capacity.set(nuclearTarget(time(YEAR)+2023), Nuclear);
//hydro
Capacity.set(hydroTarget(time(YEAR)+2023), Hydro);
//onshore
Capacity.set(onShoreWindTarget(time(YEAR)+2023), OnshoreWind);
//Natural Gas
Capacity.set(naturalGasTarget(time(YEAR)+2023), NaturalGas);
//Offshore wind
Capacity.set(offShoreWindTarget(time(YEAR)+2023), OffshoreWind);
//solar
Capacity.set(solarTarget(time(YEAR)+2023), Solar);
//coal
Capacity.set(coalTarget(time(YEAR)+2023), Coal);
//oil
Capacity.set(oilTarget(time(YEAR)+2023), Oil);
/*ALCODEEND*/}

