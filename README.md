# agents-homework
Agents relay race - homework 2

How to run 3 teams, 3 containers
parameters are: isRepresentative(the first who starts running), targetAgent

runner agents team 1:
-gui A0:agents.RunnerAgent("false","A1rep");A1rep:agents.RunnerAgent("true","A2");B3:agents.RunnerAgent("false","B0");C2:agents.RunnerAgent("false","C3");

runner agents team 2:
-container A2:agents.RunnerAgent("false","A3");B0:agents.RunnerAgent("false","B1rep");B1rep:agents.RunnerAgent("true","B2");C3:agents.RunnerAgent("false","C0");

runner agents team 3:
-container A3:agents.RunnerAgent("false","A0");B2:agents.RunnerAgent("false","B3");C0:agents.RunnerAgent("false","C1rep");C1rep:agents.RunnerAgent("true","C2");

run the ExperimentMasterAgent:
-container EMA:agents.ExperimentMasterAgent("1"); 
parameters are: number of laps

