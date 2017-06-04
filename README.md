# agents-homework
Agents relay race - homework 2

# How to run 3 teams, 3 containers
parameters are: isRepresentative(the first who starts running), targetAgent

runner agents container 1:
-gui A0:agents.RunnerAgent("false","A1rep");A1rep:agents.RunnerAgent("true","A2");B3:agents.RunnerAgent("false","B0");C2:agents.RunnerAgent("false","C3");

runner agents container 2:
-container A2:agents.RunnerAgent("false","A3");B0:agents.RunnerAgent("false","B1rep");B1rep:agents.RunnerAgent("true","B2");C3:agents.RunnerAgent("false","C0");

runner agents container 3:
-container A3:agents.RunnerAgent("false","A0");B2:agents.RunnerAgent("false","B3");C0:agents.RunnerAgent("false","C1rep");C1rep:agents.RunnerAgent("true","C2");

run the ExperimentMasterAgent:
-container EMA:agents.ExperimentMasterAgent("1"); 
parameters are: number of laps

# How to run 3 teams, 4 machines, 5 laps
-gui A0:agents.RunnerAgent("false","A1rep");A1rep:agents.RunnerAgent("true","A2");B3:agents.RunnerAgent("false","B0");C2:agents.RunnerAgent("false","C4");

-container A2:agents.RunnerAgent("false","A4");B0:agents.RunnerAgent("false","B1rep");B1rep:agents.RunnerAgent("true","B2");C3:agents.RunnerAgent("false","C0");

-container A3:agents.RunnerAgent("false","A0");B2:agents.RunnerAgent("false","B4");C0:agents.RunnerAgent("false","C1rep");C1rep:agents.RunnerAgent("true","C2");

-container A4:agents.RunnerAgent("false","A3");B4:agents.RunnerAgent("false","B3");C4:agents.RunnerAgent("false","C3");

-container EMA:agents.ExperimentMasterAgent("5"); 

# How to run different number of teams, 3 containers(machines), 5 laps
parameters are: isRepresentative(the first who starts running), targetAgent

runner agents cont 1:
-gui A0:agents.RunnerAgent("false","A1rep");A1rep:agents.RunnerAgent("true","A2");B3:agents.RunnerAgent("false","B0");C2:agents.RunnerAgent("false","C3");
(add here:)D0:agents.RunnerAgent("false","D1rep");D1rep:agents.RunnerAgent("true","D2"); where D is next team - A, B, C, D - 4 teams
E0:agents.RunnerAgent("false","E1rep");D1rep:agents.RunnerAgent("true","E2"); - A, B, C, D, E - 5 teams and so on

runner agents cont 2:
-container A2:agents.RunnerAgent("false","A3");B0:agents.RunnerAgent("false","B1rep");B1rep:agents.RunnerAgent("true","B2");C3:agents.RunnerAgent("false","C0");(add here:)D2:agents.RunnerAgent("false","D3"); where D is next team - A, B, C, D - 4 teams, see above

runner agents cont 3:
-container A3:agents.RunnerAgent("false","A0");B2:agents.RunnerAgent("false","B3");C0:agents.RunnerAgent("false","C1rep");C1rep:agents.RunnerAgent("true","C2");(add here:)D3:agents.RunnerAgent("false","D0");where D is next team - A, B, C, D - 4 teams, see above

run the ExperimentMasterAgent:
-container EMA:agents.ExperimentMasterAgent("5"); 
parameters are: number of laps

