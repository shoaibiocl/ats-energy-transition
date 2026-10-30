# Electricity–Hydrogen–Aviation Agent-Based Model (UK)

Agent-based model of the UK energy transition used to assess whether the national
energy system can supply hydrogen to the Air Transport System (ATS) at the required
scale, cost and reliability when other sectors compete for the same electricity and
hydrogen.

This repository accompanies:

> Shoaib, M., Tantawy, A., Zaidi, Y., Camelia, F., Marr, I. (2026).
> *Evaluating Hydrogen Transition of the Air Transport System under Cross-Sectoral
> Energy Competition.* 

---

## 1. Model description

Four agents interact on an annual time step:

| Agent | Role |
|---|---|
| Energy Supply | Converts installed generation capacity and intermittency factors into available annual electricity |
| Energy Demand | Represents UK final energy consumption by sector and carrier; transitions fossil demand linearly to electricity and hydrogen |
| Hydrogen Production | Electrolysis producer; plans output under electricity allocation, accounts costs and margins, and decides capacity expansion |
| Energy Coordinator | Allocates electricity and hydrogen proportionally under scarcity and sets both prices from the instantaneous demand/supply ratio |

Hydrogen availability and price are **endogenous outcomes** of sectoral demand,
investment behaviour and grid constraints, not exogenous inputs.

## 2. Software requirements

| Tool | Version | Used for |
|---|---|---|
| AnyLogic | University/PLE 8.9 | Simulation experiments |
| Minitab | 22 | Design of experiments, main- and interaction-effect analysis |

> **Licensing note.** The model was built in AnyLogic University. The free
> Personal Learning Edition can open `.alp` files. 
## 3. Repository contents

```
model/
  ATS_hydrogen_ABM.alp        AnyLogic model (all parameters embedded)
outputs/
  Excel spreadsheet/                      Book1.xlsx
  
README.md
```

All model inputs — generation capacities, sectoral demands and techno-economic
parameters — are specified inside the model file. There are no external input
data files. 

## 4. Reproducing the published results

1. Open `model/ATS_hydrogen_ABM.alp` in AnyLogic 8.8.
2. Run the **`ParameterVariation_Average`** experiment. 
   This executes all 16 configurations of the Table 5 design space:

   | Factor | Levels |
   |---|---|
   | ATS transition velocity | 25, 35 years |
   | ATS transition intensity | 0.7, 0.9 |
   | Economy-wide transition velocity | 20, 30 years |
   | Economy-wide transition intensity | 0.3, 0.6 |
   | Capacity expansion delay | 1, 3 years |

3. Simulation horizon: 2023 to 2063, annual time step.
4. The model is deterministic, so a
   single run per configuration reproduces the published values and no random
   seeding is required.

## 5. Key parameter values

Full listing in Tables 1–4 of the paper. Headline values:

| Parameter | Value | Source |
|---|---|---|
| Electrolyser CAPEX | £1,458/kW | DESNZ hydrogen production costs (upper bound) |
| Fixed OPEX | £39.39/kW/yr | DESNZ |
| Electrical conversion efficiency (HHV) | 0.76 | Published electrolysis performance |
| Plant availability | 0.98 | — |
| Hydrogen yield | 25.4 kg/MWh (HHV) | — |
| Starting capacity | 50 MW | Scale of UK Hydrogen Allocation Round 1 |
| Capacity expansion block | 5 MW | Modular deployment abstraction |
| Starting capital | £900,000 | Seed value; no measurable effect on outcomes (S5.1) |
| Cost of capital | 8% | 6–10% range in comparable studies |
| Base electricity price | £100/MWh | UK wholesale reference |
| CAPEX learning rate | 7% per capacity doubling | DESNZ |



## 6. Licence

Code and model: MIT 
Documentation and output data: CC-BY-4.0

## 7. Funding

Out of Cycle NExt generation highly efficient air transport (ONEheart) project,
funded by UK Research and Innovation.
