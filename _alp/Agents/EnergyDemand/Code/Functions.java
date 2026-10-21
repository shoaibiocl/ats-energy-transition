double all_sector_transition(int years)
{/*ALCODESTART::1753110981003*/
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Industry, Fossil), years), Industry);
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Society, Fossil), years), Society);
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Transport, Fossil), years), Transport);
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(ATS, Fossil), years), ATS);
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(NonEnergy, Fossil), years), NonEnergy);
annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(OtherSectors, Fossil), years), OtherSectors);


/*ALCODEEND*/}

double getDemand()
{/*ALCODESTART::1759838603887*/
double totalHydrogenDemand = TotalEnergyDemand.sum(INDEX_CAN_VARY, Hydrogen); // getting demand

main.hydrogenProducer_PEM.demand_kg = totalHydrogenDemand*25400000; // setting hydrogen demand in the hydrogen agent

//traceln("Demand agent, now setting hydrogen demand: "+totalHydrogenDemand*25400000);

electricityDemand_Hydrogen_MWh = main.hydrogenProducer_PEM.annualElectricityDemand_MWh; //getting electricity demand from the 
//hydrogen production agent
//traceln("Hydrogen agent's electricity demand is: "+electricityDemand_Hydrogen_MWh);
TotalEnergyDemand.set(electricityDemand_Hydrogen_MWh/1_000_000, HydrogenProduction, Electricity);
/*ALCODEEND*/}

double transport_transition(int years)
{/*ALCODESTART::1760085886588*/
//get demand
double demand = TotalEnergyDemand.get(Transport, Fossil);
// Total demand after transition
double transition_end_demand = demand * fractionalTransition;
// calculate annual reduction
double annual_reduction = transition_end_demand/years; 
// update in the table
annualReduction.set(annual_reduction, Transport);

//annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Transport, Fossil), years), Transport);
/*ALCODEEND*/}

double society_transition(int years)
{/*ALCODESTART::1760085926752*/
//get demand
double demand = TotalEnergyDemand.get(Society, Fossil);
// Total demand after transition
double transition_end_demand = demand * fractionalTransition;
// calculate annual reduction
double annual_reduction = transition_end_demand/years; 
// update in the table
annualReduction.set(annual_reduction, Society);

//annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Society, Fossil), years), Society);
/*ALCODEEND*/}

double industry_transition(int years)
{/*ALCODESTART::1760085982853*/
//annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(Industry, Fossil), years), Industry);
//get demand
double demand = TotalEnergyDemand.get(Industry, Fossil);
// Total demand after transition
double transition_end_demand = demand * fractionalTransition;
// calculate annual reduction
double annual_reduction = transition_end_demand/years; 
// update in the table
annualReduction.set(annual_reduction, Industry);

/*ALCODEEND*/}

double other_sector_transition(int years)
{/*ALCODESTART::1760086016119*/
//get demand
double demand = TotalEnergyDemand.get(OtherSectors, Fossil);
// Total demand after transition
double transition_end_demand = demand * fractionalTransition;
// calculate annual reduction
double annual_reduction = transition_end_demand/years; 
// update in the table
annualReduction.set(annual_reduction, OtherSectors);
//annualReduction.set(fractionalTransition*zidz(TotalEnergyDemand.get(OtherSectors, Fossil), years), OtherSectors);

/*ALCODEEND*/}

double ATS_transition(int years)
{/*ALCODESTART::1761989876776*/
//get demand
double demand = TotalEnergyDemand.get(ATS, Fossil);
// Total demand after transition
double transition_end_demand = demand * fractionalTransition_ATS;
// calculate annual reduction
double annual_reduction = transition_end_demand/years; 
// update in the table
annualReduction.set(annual_reduction, ATS);
/*ALCODEEND*/}

