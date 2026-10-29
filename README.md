# ATS energy transition model

AnyLogic agent-based model of the UK electricity and hydrogen system, used to assess hydrogen supply to the Air Transport System (ATS) under cross-sectoral competition. It accompanies the paper "Evaluating Hydrogen Transition of the Air Transport System under Cross-Sectoral Energy Competition". Version 1.0 matches the revised paper. Archived at [Zenodo DOI].

## Requirements
- AnyLogic [version, edition: CONFIRM]

## Files
- `ATS_Energy_Transition_ABM.alpx`: the model
- [results file names: CONFIRM; rename `Book1.xlsx` or describe it here]

## Reproducing the results
1. Open the `.alpx` file in AnyLogic.
2. Run experiment [name: CONFIRM] with the settings in Table S5 of the Supplementary Material (five two-level factors, 32 scenarios).
3. Outputs are written to [location: CONFIRM].

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
