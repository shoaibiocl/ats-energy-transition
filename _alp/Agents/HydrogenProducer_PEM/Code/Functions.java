double getMaxProduction()
{/*ALCODESTART::1758568600284*/
/// Convert current capacity from kW to MW for the conversion formula
double capacity_MW = currentCapacity_kW_H2 / 1000.0;

// Calculate annual production in MWh
double annualProduction_MWh = capacity_MW * 8760 * availabilityFactor;
//traceln("Total production in kgs: "+ annualProduction_MWh * h2_HHV_to_kg_conversion);
// Convert MWh to kg and return the value
return annualProduction_MWh * h2_HHV_to_kg_conversion;
/*ALCODEEND*/}

boolean shouldExpand()
{/*ALCODESTART::1758568600286*/
double maxProduction_kg = getMaxProduction();
//traceln("In should expand. demand_kg "+demand_kg +" max production kg "+getMaxProduction());
//traceln("With in should expand function> Is demand > cap ? "+(demand_kg > maxProduction_kg * (1 + expansionThreshold)));
return demand_kg > maxProduction_kg * (1 + expansionThreshold);
/*ALCODEEND*/}

double getAnnualCostForHydrogenPriceCalculation(double annualProduction_kg)
{/*ALCODESTART::1758568600288*/
// --- FIXED COSTS ---
// 1. Calculate Loan Repayments
// It loops through the list of active loans and sums their annual payments.
double totalLoanPayments = 0;
for (Loan loan : loanList) {
    totalLoanPayments += loan.getAnnualPayment();
}

hydrogen_loanCost_dataset.add(totalLoanPayments);

// 2. Calculate Fixed Operating Expenditure
// This is based on the plant's current rated capacity.
double fixedOpex = currentCapacity_kW_H2 * fixedOpexRate_per_kW_year;
hydrogen_FixedOpexCost_dataset.add(fixedOpex);
// --- VARIABLE COSTS ---
//traceln("Fixed Opex "+fixedOpex);
// 3. Calculate Variable Operating Expenditure
// This is based on the actual amount of hydrogen produced.
double production_MWh = annualProduction_kg / h2_HHV_to_kg_conversion;
double production_kWh = production_MWh * 1000;
double variableOpex = production_kWh * variableOpexRate_per_kWh;
hydrogen_VariableOpexCost_dataset.add(variableOpex);
//traceln("Variable Opex "+variableOpex);

// 4. Calculate Electricity Cost
// This is also based on the actual amount produced.
//double allocated_electricity = production_MWh / efficiency;
double electricityCost = allocated_electricity_MW * electricityPrice_per_MWh;
//traceln("Electricity needed "+electricityNeeded_MWh+ " electricity cost "+electricityCost);
// --- 2. CALCULATE COST OF EQUITY ---
// It now loops through the list of investments and calls their getYearlyCost() method.
hydrogen_ElectricityCost_dataset.add(electricityCost);

double totalAnnualizedEquityCost = 0;
for (CapitalInvestment investment : equityInvestments) {
    totalAnnualizedEquityCost += investment.getYearlyCost();
}
hydrogen_CapexCost_dataset.add(totalAnnualizedEquityCost);
// --- TOTAL COST ---
// Sum all fixed and variable costs for the year.
//traceln("Loan payment: "+ (totalLoanPayments));
//traceln("Fixed OPEX: " + fixedOpex);
//traceln("Variable OPEX: "+ variableOpex);
//traceln("Electricity cost: " + electricityCost);
//traceln("------------------------------------");
return totalLoanPayments +totalAnnualizedEquityCost+ fixedOpex + variableOpex + electricityCost;

/*ALCODEEND*/}

double getAnnualRevenue(double annualProduction)
{/*ALCODESTART::1758568600290*/
// Sells the lesser of what was produced or what the market demands.
double sales_kg = Math.min(actualProduction_kg, demand_kg);
// Calculates revenue based on what was actually sold.
//traceln("Sales kg "+sales_kg);
//traceln("Revenue : "+ (sales_kg * hydrogenPrice_per_kg));
return sales_kg * main.energyCoordinator.hydrogenPrice;
//return sales_kg * (1+profitMargin) * cost_per_kg ;
/*ALCODEEND*/}

double expandCapacity()
{/*ALCODESTART::1760614806628*/
if (!isExpansionInProgress && shouldExpand()) {
    traceln("DEMAND HIGH. Evaluating expansion options...");
    
	double blocksToBuild = 0;
    // --- STRATEGY 1: Attempt an AMBITIOUS expansion (target >20% of current demand) ---
    // Calculate the target capacity: current demand + 20%
    double ambitiousDemandTarget_kg = demand_kg * 1.20;
    
    // Convert this ambitious demand target into a total required capacity in kW
    double ambitiousTotalCapacity_kW = (ambitiousDemandTarget_kg / (8760 * availabilityFactor * h2_HHV_to_kg_conversion / 1000.0));
    
    // Calculate the size of the expansion needed to reach this ambitious target
    double ambitiousExpansion_kW = ambitiousTotalCapacity_kW - currentCapacity_kW_H2;

    if (ambitiousExpansion_kW > 0) {
        // Round up the ambitious expansion to the nearest full block size
        double ambitiousBlocksToBuild = ceil(ambitiousExpansion_kW / expansionBlock_kW);
        double ambitiousActualExpansion_kW = ambitiousBlocksToBuild * expansionBlock_kW;
        
        // Calculate the cost for this ambitious project
        double ambitiousCapex = ambitiousActualExpansion_kW * capexRate_per_kW;
        double ambitiousEquityNeeded = ambitiousCapex * (1 - riskProfile);
        
        traceln("Ambitious goal: Build " + (int)ambitiousActualExpansion_kW + " kW. Equity needed: £" + (int)ambitiousEquityNeeded);

        // If we can afford the ambitious project, build it.
        if (currentCapital >= ambitiousEquityNeeded) {
            traceln("SUCCESS: Capital sufficient for ambitious expansion.");
            currentCapital -= ambitiousEquityNeeded; // Pay the equity
            double newLoanPrincipal = ambitiousCapex * riskProfile;
            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
            CapitalInvestment newInvestment = new CapitalInvestment(ambitiousEquityNeeded, capitalInterestRate, (int)loanRepaymentPeriod_years);
            equityInvestments.add(newInvestment);
            
            isExpansionInProgress = true;
            expansionCompleteYear = time(YEAR) + buildTime_years;
            capacityUnderConstruction_kW = ambitiousActualExpansion_kW;
            
            create_hydrogenCapacityExpansion(1, YEAR);
            traceln("AMBITIOUS PROJECT STARTED: Building " + (int)ambitiousBlocksToBuild + " blocks. Ready in year " + (int)expansionCompleteYear);
            
            // Set blocksToBuildz to 0 to skip the fallback logic
            blocksToBuild = 0; 
        } else {
            traceln("Insufficient capital for ambitious expansion. Falling back to budget-constrained plan.");
          	double maxProduction = getMaxProduction();
	        traceln("Executing budget-constrained expansion...");
	        double deficit = demand_kg - maxProduction;
	        double neededBlocks = ceil((deficit) / 
	        (expansionBlock_kW * 8760 * availabilityFactor * h2_HHV_to_kg_conversion / 1000.0));
	        traceln("Needed hydrogen blocks to meet current demand: "+neededBlocks);
	        double equityPerBlock = (expansionBlock_kW * capexRate_per_kW) * (1 - riskProfile);
	        
	        double affordableBlocks = (equityPerBlock > 0) ? Math.floor(currentCapital / equityPerBlock) : 0;
	        traceln("Hydrogen affordable blocks: "+affordableBlocks);
	        blocksToBuild = Math.min(neededBlocks, affordableBlocks);

	        if (blocksToBuild >= 1) {
	            double actualExpansion_kW = blocksToBuild * expansionBlock_kW;
	            double capex = actualExpansion_kW * capexRate_per_kW;
	            double equityNeeded = capex * (1 - riskProfile);
	            currentCapital -= equityNeeded;
	            double newLoanPrincipal = capex * riskProfile;
	            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
	            CapitalInvestment newInvestment = new CapitalInvestment(equityNeeded, capitalInterestRate, (int)loanRepaymentPeriod_years);
	            equityInvestments.add(newInvestment);
	            
	            isExpansionInProgress = true;
	            expansionCompleteYear = time(YEAR) + buildTime_years;
	            capacityUnderConstruction_kW = actualExpansion_kW;
	            create_hydrogenCapacityExpansion(1, YEAR);
	            traceln("PROJECT STARTED: Building " + (int)blocksToBuild + " blocks (" + (int)actualExpansion_kW + " kW). Ready in year " + (int)expansionCompleteYear);
	        } else {
	            traceln("CANNOT EXPAND: Insufficient capital for even one expansion block.");
	        }
    }}
}
/*ALCODEEND*/}

double forecast()
{/*ALCODESTART::1760619193110*/
//goes to the demand agent to determine current demand.
//double demand = main.energyDemand.TotalEnergyDemand.sum(INDEX_CAN_VARY, Hydrogen);
//determine annual expected change in demand
//convert from TWh to KWh
//traceln("---------------------------------------------------");
double annual_change_KWh = main.energyDemand.annualReduction.sum()*1000_000;
double annual_reduction_ATS_KWh = main.energyDemand.annualReduction.get(ATS)*1000_000;
double annual_reduction_others_KWh = annual_change_KWh - annual_reduction_ATS_KWh;
//traceln("Annual reduction ATS: "+annual_reduction_ATS_KWh);
//traceln("Annual reduction others: "+annual_reduction_others_KWh);
//traceln("---------------------------------------------------");
//estimate hydrogen demand
double hydrogen_annual_change_others_KWh = 
annual_reduction_others_KWh * main.energyDemand.hydrogenSplit;
double hydrogen_annual_change_ATS_KWh = 
annual_reduction_ATS_KWh * main.energyDemand.hydrogenSplit_ATS;
//assuming 5 future period capacity
//double currentGap = demand*1000_000 - currentCapacity_kW_H2;
// estimating gap in demand
double currentGap = demand_kg - annualCapacity_kg;
//changing kgs to MWh
//1 MWh H2 (HHV) = 25.4 kg H2
double currentGap_MWh_H2 = currentGap/25.4;
double currentGap_KWh_H2 =  currentGap_MWh_H2 * 1000;

traceln("===================================================");
traceln("Current demand in '000 tonnes: "+demand_kg/1000000);
traceln("Current capacity in '000 tonnes: "+annualCapacity_kg/1000000);
traceln("Current Gap in '000 tonnes: "+ currentGap/1000000);
//traceln("Current Gap in KWh H2: "+currentGap_KWh_H2);

//traceln("Annual Change in demand of other sectors in KWh: " +hydrogen_annual_change_others_KWh);
//traceln("Annual Change in demand of ATS in KWh: "+ hydrogen_annual_change_ATS_KWh);

double x = (hydrogen_annual_change_others_KWh +
		    hydrogen_annual_change_ATS_KWh) ;
traceln("Annual demand increase : "+x);
traceln("Remainig transition period: "+main.energyDemand.remainingTransitionTime);
double y = x *(min(main.energyDemand.remainingTransitionTime, 5));
traceln("Total forecast in 000 tonnes : "+((currentGap_KWh_H2 + y)*(25.4/1000))/1000000);
//traceln("Annual demand increase after forecast : "+y);		
//traceln("Total forecast: "+(currentGap_KWh_H2 + y));
traceln("===================================================");
return currentGap_KWh_H2 + y;
/*ALCODEEND*/}

double expandCapacity1()
{/*ALCODESTART::1760724672374*/
if (!isExpansionInProgress && shouldExpand()) {
   // traceln("DEMAND HIGH. Evaluating expansion options...");
    
	double blocksToBuild = 0;
    // --- STRATEGY 1: Attempt an AMBITIOUS expansion (target >20% of current demand) ---
 
    double ambitiousTotalCapacity_kW = (forecast() * 1_000_000_000 / (8760));
    //ading some randomness
    if (randomness){
	    ambitiousTotalCapacity_kW = ambitiousTotalCapacity_kW*uniform(1, 2);
  		}
    // Calculate the size of the expansion needed to reach this ambitious target
    double ambitiousExpansion_kW = ambitiousTotalCapacity_kW;

    if (ambitiousExpansion_kW > 0) {
        // Round up the ambitious expansion to the nearest full block size
        double ambitiousBlocksToBuild = ceil(ambitiousExpansion_kW / expansionBlock_kW);
        double ambitiousActualExpansion_kW = ambitiousBlocksToBuild * expansionBlock_kW;
        
        // Calculate the cost for this ambitious project
        double ambitiousCapex = ambitiousActualExpansion_kW * capexRate_per_kW;
        double ambitiousEquityNeeded = ambitiousCapex * (1 - riskProfile);
        
        traceln("Ambitious goal: Build " + (int)ambitiousActualExpansion_kW + " kW. Equity needed: £" + (int)ambitiousEquityNeeded);

        // If we can afford the ambitious project, build it.
        if (currentCapital >= ambitiousEquityNeeded) {
            traceln("SUCCESS: Capital sufficient for ambitious expansion.");
            currentCapital -= ambitiousEquityNeeded; // Pay the equity
            double newLoanPrincipal = ambitiousCapex * riskProfile;
            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
            CapitalInvestment newInvestment = new CapitalInvestment(ambitiousEquityNeeded, capitalInterestRate, (int)loanRepaymentPeriod_years);
            equityInvestments.add(newInvestment);
            isExpansionInProgress = true;
            expansionCompleteYear = time(YEAR) + buildTime_years;
            capacityUnderConstruction_kW = ambitiousActualExpansion_kW;
            hydrogen_capacityExpansions_dataset.add(capacityUnderConstruction_kW);
            create_hydrogenCapacityExpansion(buildTime_years, YEAR);
            traceln("AMBITIOUS PROJECT STARTED: Building " + (int)ambitiousBlocksToBuild + " blocks. Ready in year " + (int)expansionCompleteYear);
            
            // Set blocksToBuildz to 0 to skip the fallback logic
            blocksToBuild = 0; 
        } else {
            traceln("Insufficient capital for ambitious expansion. Falling back to budget-constrained plan.");
          	double maxProduction = getMaxProduction();
	        traceln("Executing budget-constrained expansion...");
	        
	        double deficit = demand_kg - maxProduction;
	        double neededBlocks = ceil((deficit) / 
	        (expansionBlock_kW * 8760 * availabilityFactor * h2_HHV_to_kg_conversion / 1000.0));
	      //  traceln("Needed hydrogen blocks to meet current demand: "+neededBlocks);
	        //double possibleMaxExpansion = current
	        double equityPerBlock = (expansionBlock_kW * capexRate_per_kW) * (1 - riskProfile);
	        
	        double affordableBlocks = (equityPerBlock > 0) ? Math.floor(currentCapital / equityPerBlock) : 0;
	      //  traceln("Hydrogen affordable blocks: "+affordableBlocks);
	        // checking if the more blocks than what is needed true then build afforable, otherwise build needed to match demand. 
	        if (affordableBlocks > neededBlocks){
	        	blocksToBuild = affordableBlocks;
	        } else if (neededBlocks > affordableBlocks){
	        	blocksToBuild = affordableBlocks;
	        }

	        if (blocksToBuild >= 1) {
	            double actualExpansion_kW = blocksToBuild * expansionBlock_kW;
	            double capex = actualExpansion_kW * capexRate_per_kW;
	            double equityNeeded = capex * (1 - riskProfile);
	            currentCapital -= equityNeeded;
	            double newLoanPrincipal = capex * riskProfile;
	            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
	            CapitalInvestment newInvestment = new CapitalInvestment(equityNeeded, capitalInterestRate, (int)loanRepaymentPeriod_years);
	            equityInvestments.add(newInvestment);
	            
	            isExpansionInProgress = true;
	            expansionCompleteYear = time(YEAR) + buildTime_years;
	            capacityUnderConstruction_kW = actualExpansion_kW;
	            create_hydrogenCapacityExpansion(buildTime_years, YEAR);
	            hydrogen_capacityExpansions_dataset.add(capacityUnderConstruction_kW);
	            traceln("PROJECT STARTED: Building " + (int)blocksToBuild + " blocks (" + (int)actualExpansion_kW + " kW). Ready in year " + (int)expansionCompleteYear);
	        } else {
	            traceln("CANNOT EXPAND: Insufficient capital for even one expansion block.");
	        }
	        
    }}
}
/*ALCODEEND*/}

double annualCostReduction_LR()
{/*ALCODESTART::1761826455178*/
if (currentCapacity_kW_H2 > baseCapacity_KW_LR){
	double capacityRatio = currentCapacity_kW_H2/baseCapacity_KW_LR;
	//traceln("Current capacity: "+currentCapacity_kW_H2+" base capacity: "+baseCapacity_KW_LR);
	//traceln("Ratio : "+capacityRatio);
	//traceln("Floor of capacity ratio: "+floor(capacityRatio));
	double ratio = currentCapacity_kW_H2/baseCapacity_KW_LR;
        // Math.floor ensures we only apply the reduction for a *full* doubling (2x, 4x, 8x, etc.)
    double numberOfDoublings = Math.floor(Math.log(ratio) / Math.log(2));

        // Calculate the total cost reduction factor.
        // For each doubling, the cost is reduced by 7%, meaning the new cost multiplier is (1 - 0.07).
        // We raise this to the power of the number of doublings.
    double costMultiplier = Math.pow((1.0 - 0.07), numberOfDoublings);
        // 3. Calculate the reduced cost per unit capacity.
    double reducedCostPerUnit = base_capexRate_per_kW * costMultiplier;
        // 4. Calculate the total cost.        
    set_capexRate_per_kW(reducedCostPerUnit);
    }
		


/*ALCODEEND*/}

double expandCapacity2()
{/*ALCODESTART::1762955073921*/
if (!isExpansionInProgress && shouldExpand()) {
   // traceln("DEMAND HIGH. Evaluating expansion options...");
    
	double blocksToBuild = 0;
    // --- STRATEGY 1: Attempt an AMBITIOUS expansion ---
 	// demand from the forecaset comes in KWh. So divinding by 8760. 
    double ambitiousTotalCapacity_kW = (forecast1() / (8760));
    //ading some randomness
    if (randomness){
	    ambitiousTotalCapacity_kW = ambitiousTotalCapacity_kW*uniform(1, 2);
  		}
    // Calculate the size of the expansion needed to reach this ambitious target
    double ambitiousExpansion_kW = ambitiousTotalCapacity_kW;

    if (ambitiousExpansion_kW > 0) {
        // Round up the ambitious expansion to the nearest full block size
        double ambitiousBlocksToBuild = ceil(ambitiousExpansion_kW / expansionBlock_kW);
        double ambitiousActualExpansion_kW = ambitiousBlocksToBuild * expansionBlock_kW;
        
        // Calculate the cost for this ambitious project
        double ambitiousCapex = ambitiousActualExpansion_kW * capexRate_per_kW;
        double ambitiousEquityNeeded = ambitiousCapex * (1 - riskProfile);
        
       // traceln("Ambitious goal: Build " + (int)ambitiousActualExpansion_kW + " kW. Equity needed: £" + (int)ambitiousEquityNeeded);

        // If we can afford the ambitious project, build it.
        if (currentCapital >= ambitiousEquityNeeded) {
         //   traceln("SUCCESS: Capital sufficient for ambitious expansion.");
            
            currentCapital -= ambitiousEquityNeeded; // Do not pay the equity
            
            double newLoanPrincipal = ambitiousCapex * riskProfile;
            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
            
            //Adding in the equity invested. It is amortised over the plant life. 
            CapitalInvestment newInvestment = new CapitalInvestment(ambitiousEquityNeeded, capitalInterestRate, plantLife);
            equityInvestments.add(newInvestment);
            
            isExpansionInProgress = true;
            expansionCompleteYear = time(YEAR) + buildTime_years;
            capacityUnderConstruction_kW = ambitiousActualExpansion_kW;
            hydrogen_capacityExpansions_dataset.add(capacityUnderConstruction_kW);
            create_hydrogenCapacityExpansion(buildTime_years, YEAR);
           // traceln("AMBITIOUS PROJECT STARTED: Building " + (int)ambitiousBlocksToBuild + " blocks. Ready in year " + (int)expansionCompleteYear);
            
            // Set blocksToBuildz to 0 to skip the fallback logic
            blocksToBuild = 0; 
        } else {
            //traceln("Insufficient capital for ambitious expansion. Falling back to budget-constrained plan.");
          	double maxProduction = getMaxProduction();
	        //traceln("Executing budget-constrained expansion...");
	        double possibleMaxExpansion_kW = (currentCapital*(1 - riskProfile))/capexRate_per_kW;
	        //traceln("Current capital : "+currentCapital);
	        //traceln("Investable capital: "+currentCapital*(1 - riskProfile));
	        //traceln("Possible expansion in KW: "+possibleMaxExpansion_kW);
	        //traceln("Expansion block size: "+expansionBlock_kW);
	        blocksToBuild = possibleMaxExpansion_kW/expansionBlock_kW; 
	        //traceln("Blocks to build : "+blocksToBuild);
	        if (blocksToBuild >= 1) {
	            double actualExpansion_kW = blocksToBuild * expansionBlock_kW;
	            double capex = actualExpansion_kW * capexRate_per_kW;
	            double equityNeeded = capex * (1 - riskProfile);
	            currentCapital -= equityNeeded;
	            double newLoanPrincipal = capex * riskProfile;
	            loanList.add(new Loan(newLoanPrincipal, loanInterestRate, loanRepaymentPeriod_years));
	            CapitalInvestment newInvestment = new CapitalInvestment(equityNeeded, capitalInterestRate, plantLife);
	            equityInvestments.add(newInvestment);
	            
	            isExpansionInProgress = true;
	            expansionCompleteYear = time(YEAR) + buildTime_years;
	            capacityUnderConstruction_kW = actualExpansion_kW;
	            create_hydrogenCapacityExpansion(buildTime_years, YEAR);
	            hydrogen_capacityExpansions_dataset.add(capacityUnderConstruction_kW);
	          //  traceln("PROJECT STARTED: Building " + (int)blocksToBuild + " blocks (" + (int)actualExpansion_kW + " kW). Ready in year " + (int)expansionCompleteYear);
	        } else {
	            //traceln("CANNOT EXPAND: Insufficient capital for even one expansion block.");
	        }
	        
    }}
}
/*ALCODEEND*/}

double getAnnualCostForProfitCalculation(double annualProduction_kg)
{/*ALCODESTART::1763482460333*/
// --- FIXED COSTS ---
// 1. Calculate Loan Repayments
// It loops through the list of active loans and sums their annual payments.
double totalLoanPayments = 0;
for (Loan loan : loanList) {
    totalLoanPayments += loan.getAnnualPayment();
}

double fixedOpex = currentCapacity_kW_H2 * fixedOpexRate_per_kW_year;
double production_MWh = annualProduction_kg / h2_HHV_to_kg_conversion;
double production_kWh = production_MWh * 1000;
double variableOpex = production_kWh * variableOpexRate_per_kWh;
double electricityCost = allocated_electricity_MW * electricityPrice_per_MWh;
return totalLoanPayments + fixedOpex + variableOpex + electricityCost;

/*ALCODEEND*/}

double forecast1()
{/*ALCODESTART::1763577807696*/
//get annual hydrogen demand change from the Energy Demand agent

//Only ATS
double hydrogen_demand_ATS_annual_change_TWh = main.energyDemand.annualReduction.get(ATS)*
main.energyDemand.hydrogenSplit_ATS; 
double hydrogen_demand_ATS_annual_change_MWh = hydrogen_demand_ATS_annual_change_TWh * 1000_000;
// 1 MWh H2 = 25.4 kg H2
double hydrogen_demand_ATS_annual_change_kg = hydrogen_demand_ATS_annual_change_MWh * 25.4; 
//traceln("Annual hydrogen demand in 000 tonnes: "+ hydrogen_demand_ATS_annual_change_kg/1000000);
double forecasted_hydrogen_demand_ATS_kgs = 
hydrogen_demand_ATS_annual_change_kg *(min(main.energyDemand.remainingTransitionTimeATS, forecast_period));
//traceln("Forecasted hydrogen demand ATS in 000 tonnes: "+ forecasted_hydrogen_demand_ATS_kgs/1000000);

//All other sectors
double hydrogen_demand_Other_annual_change_TWh = main.energyDemand.hydrogenSplit*(main.energyDemand.annualReduction.get(Society)+
main.energyDemand.annualReduction.get(Industry) +
main.energyDemand.annualReduction.get(Transport) +
main.energyDemand.annualReduction.get(OtherSectors) +
main.energyDemand.annualReduction.get(NonEnergy)
);


double hydrogen_demand_Other_annual_change_MWh = hydrogen_demand_Other_annual_change_TWh * 1000_000;
// 1 MWh H2 = 25.4 kg H2
double hydrogen_demand_Other_annual_change_kg = hydrogen_demand_Other_annual_change_MWh * 25.4; 
//traceln("Annual hydrogen demand for other sectors in 000 tonnes: "+ hydrogen_demand_Other_annual_change_kg/1000000);
double forecasted_hydrogen_demand_Other_kgs = 
hydrogen_demand_Other_annual_change_kg *(min(main.energyDemand.remainingTransitionTime, forecast_period));
//traceln("Forecasted hydrogen demand Others in 000 tonnes: "+ forecasted_hydrogen_demand_Other_kgs/1000000);



double currentGap = demand_kg - annualCapacity_kg;
//traceln("Current demand in '000 tonnes: "+ demand_kg/1000000);
//traceln("Current capacity in '000 tonnes: "+ annualCapacity_kg/1000000);
//traceln("Current Gap in '000 tonnes: "+ currentGap/1000000);
traceln("===================================================");

//traceln("Total hydrogen demand in 000 tons: "+(currentGap + forecasted_hydrogen_demand_Other_kgs+
//forecasted_hydrogen_demand_ATS_kgs)/1000000);

//changing kgs to MWh

traceln("");

//1 MWh H2 (HHV) = 25.4 kg H2
// converting current gap in gs to MWh
double currentGap_MWh_H2 = currentGap/25.4;
double currentGap_KWh_H2 =  currentGap_MWh_H2 * 1000;
double y_MWh = (forecasted_hydrogen_demand_Other_kgs+forecasted_hydrogen_demand_ATS_kgs)/(25.4);
//traceln("Total demand returned from the forecase function in MWh: "+ (currentGap_KWh_H2 + y_MWh*1000)/1000);
//traceln("Total demand in kw of H2: "+ ((currentGap_KWh_H2 + y_MWh*1000))/8760);
//traceln("===================================================");
return (currentGap_KWh_H2 + y_MWh*1000);
/*ALCODEEND*/}

