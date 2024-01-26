* Encoding: UTF-8.

DATASET ACTIVATE DataSet1.
REGRESSION
  /MISSING LISTWISE
  /STATISTICS COEFF OUTS R ANOVA COLLIN TOL
  /CRITERIA=PIN(.05) POUT(.10)
  /NOORIGIN 
  /DEPENDENT snelheid_ned
  /METHOD=ENTER Voetganger Fiets Bromfiets Auto OV Anders gem_dichth gem_dichth_nieuw dicht_loop 
    dicht_fiets dicht_bromf dicht_ov dicht_anders dicht_auto
  /RESIDUALS HISTOGRAM(ZRESID).
