# ATS energy transition model

AnyLogic agent-based model of the UK electricity and hydrogen system, used to assess hydrogen supply to the Air Transport System (ATS) under cross-sectoral competition. 
## Requirements
- AnyLogic University/PLE

## Files
- `ATS_Energy_Transition_ABM.alpx`: the model
- `Book1.xlsx`

## Reproducing the results
1. Open the `.alpx` file in AnyLogic.
2. Run ParametersVariation_Average:Main experiment with the settings in Table S5 of the Supplementary Material.
3. Outputs are written to the root folder.

## Where things are
| Agent | What it does | Supplement section |
|---|---|---|
| EnergyDemand | Sector demand and the linear transition | S2.3 |
| ElectricitySupply / EnergySupply | Capacity and available energy | S2.1 |
| HydrogenProducer | Production, costs, expansion decisions | S2.2, Algorithms 1--3 |
| EnergyCoordinator | Allocation and prices | S2.4 |

Parameter values: Table 3 of the paper. Equations: Table S4.

## Licence
MIT.
