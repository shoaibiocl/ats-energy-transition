void allocation()
{/*ALCODESTART::1753091837661*/
getDemand();
getSupply();
electricity_proportional_allocation();
hydrogen_proportional_allocation();
demandSupplyRatio = xidz(main.energyDemand.totalElectricityDemand, availElectricitySupply,1);
demandSupplyRatio_H2 = xidz(hydrogenDemand,availHydrogenSupply,1);
send("Allocated", main.hydrogenProducer_PEM);

//db_table.hydrogen_price.add(1);
/*ALCODEEND*/}

void dataUpdate()
{/*ALCODESTART::1762362711156*/
//updating arraylists
if (time(YEAR) > 2){
	hydrogenPrice_list.add(hydrogenPrice);
	electricityPrice_list.add(electricityPrice);
	cordinator_hydrogenPrice_dataset.add(hydrogenPrice);
	cordinator_electricityPrice_dataset.add(electricityPrice);
	cordinator_hydrogenAllocated_ATS_dataset.add(hydrogenProportionAllocated.get(ATS));
	cordinator_electricityDemand_dataset.add(sectorDemand.sum(INDEX_CAN_VARY, Electricity));
	cordinator_electricityLoadFactor_dataset.add(demandSupplyRatio);
	}
/*ALCODEEND*/}

