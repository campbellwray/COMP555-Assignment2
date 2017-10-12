setwd("Results")
library(readr)
candidatesFilteredByKnownVariants <- read_delim("candidatesFilteredByKnownVariants.vcf", "\t", escape_double = FALSE, trim_ws = TRUE)
View(candidatesFilteredByKnownVariants)

library(binom)
penetrance = function(af_case, af_control, baseline_risk) {
  calculated_penetrance = af_case * baseline_risk / af_control
  estimated_penetrance = pmin(1,pmax(0,calculated_penetrance)) # trim to [0,1] support
  return (estimated_penetrance)
}
penetrance_confint = function (ac_case, n_case, ac_control, n_control, baseline_risk) {
  case_confint = binom.confint(x=ac_case,n=2*n_case,method='wilson')
  control_confint = binom.confint(x=ac_control,n=2*n_control,method='wilson')
  lower_bound = penetrance(case_confint$lower,control_confint$upper,baseline_risk)
  best_estimate = penetrance(case_confint$mean,control_confint$mean,baseline_risk)
  upper_bound = penetrance(case_confint$upper,control_confint$lower,baseline_risk)
  
  return(best_estimate)
}
penetrance_confint_lower = function (ac_case, n_case, ac_control, n_control, baseline_risk) {
  case_confint = binom.confint(x=ac_case,n=2*n_case,method='wilson')
  control_confint = binom.confint(x=ac_control,n=2*n_control,method='wilson')
  lower_bound = penetrance(case_confint$lower,control_confint$upper,baseline_risk)
  best_estimate = penetrance(case_confint$mean,control_confint$mean,baseline_risk)
  upper_bound = penetrance(case_confint$upper,control_confint$lower,baseline_risk)
  
  return(lower_bound)
}
penetrance_confint_upper = function (ac_case, n_case, ac_control, n_control, baseline_risk) {
  case_confint = binom.confint(x=ac_case,n=2*n_case,method='wilson')
  control_confint = binom.confint(x=ac_control,n=2*n_control,method='wilson')
  lower_bound = penetrance(case_confint$lower,control_confint$upper,baseline_risk)
  best_estimate = penetrance(case_confint$mean,control_confint$mean,baseline_risk)
  upper_bound = penetrance(case_confint$upper,control_confint$lower,baseline_risk)
  
  return(upper_bound)
}

disease_occurrence = function(disease) {
  switch(disease,
         SickleCellAnemiaAR = 6,
         RetinitisPigmentosaPatternAR = 3,
         RetinitisPigmentosaPatternAD = 3,
         SpasticParaplegiaPatternAR = 4,
         SpasticParaplegiaPatternAD = 4,
         SkeletalDysplasia = 2)
}

candidatesFilteredByKnownVariants$ac_case = mapply(disease_occurrence, candidatesFilteredByKnownVariants$`#DISEASE`)
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "SickleCellAnemiaAR"] = 0.155
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "RetinitisPigmentosaPatternAR"] = (1.0/5000.0)
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "RetinitisPigmentosaPatternAD"] = (1.0/5000.0)
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "SpasticParaplegiaPatternAR"] = (1.8/10000.0)
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "SpasticParaplegiaPatternAD"] = (1.8/10000.0)
candidatesFilteredByKnownVariants$prior[candidatesFilteredByKnownVariants$`#DISEASE` == "SkeletalDysplasia"] = (1.0/5000.0)

candidatesFilteredByKnownVariants$penetrance = mapply(penetrance_confint, candidatesFilteredByKnownVariants$ac_case, 7, candidatesFilteredByKnownVariants$PROPORTION*2148, 2148, candidatesFilteredByKnownVariants$prior)
candidatesFilteredByKnownVariants$penetrance_lower = mapply(penetrance_confint_lower, candidatesFilteredByKnownVariants$ac_case, 7, candidatesFilteredByKnownVariants$PROPORTION*2148, 2148, candidatesFilteredByKnownVariants$prior)
candidatesFilteredByKnownVariants$penetrance_upper = mapply(penetrance_confint_upper, candidatesFilteredByKnownVariants$ac_case, 7, candidatesFilteredByKnownVariants$PROPORTION*2148, 2148, candidatesFilteredByKnownVariants$prior)

write(candidatesFilteredByKnownVariants$penetrance_lower, file = "penet_lower.vcf", ncolumns = 1)
write(candidatesFilteredByKnownVariants$penetrance, file = "penet_best.vcf", ncolumns = 1)
write(candidatesFilteredByKnownVariants$penetrance_upper, file = "penet_upper.vcf", ncolumns = 1)
