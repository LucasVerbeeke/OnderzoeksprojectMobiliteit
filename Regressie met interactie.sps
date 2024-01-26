* Encoding: UTF-8.

DATASET ACTIVATE DataSet1.

REGRESSION
  /MISSING LISTWISE
  /STATISTICS COEFF OUTS R ANOVA
  /CRITERIA=PIN(.05) POUT(.10)
  /NOORIGIN 
  /DEPENDENT snelheid_ned
  /METHOD=ENTER leeftijd geslacht ethnicit inkh_s10 gem_dichth_nieuw dicht_loop dicht_fiets dicht_bromf dicht_ov dicht_anders Voetganger Fiets Bromfiets OV Anders.