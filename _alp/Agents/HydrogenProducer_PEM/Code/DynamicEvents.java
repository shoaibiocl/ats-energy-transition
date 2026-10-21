void hydrogenCapacityExpansion()
{/*ALCODESTART::1758657828349*/
currentCapacity_kW_H2 += capacityUnderConstruction_kW;
//traceln("CONSTRUCTION COMPLETE: " + (int)capacityUnderConstruction_kW + 
//" kW is now online. New total capacity: " + (int)currentCapacity_kW_H2 + " kW.");
isExpansionInProgress = false;
capacityUnderConstruction_kW = 0;
main.energyCoordinator.availHydrogenSupply = getMaxProduction()/(h2_HHV_to_kg_conversion*1000000);
/*ALCODEEND*/}

