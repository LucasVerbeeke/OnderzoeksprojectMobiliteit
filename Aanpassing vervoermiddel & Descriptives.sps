* Encoding: UTF-8.
DATASET ACTIVATE DataSet1.
COMPUTE snelheid_ned=(afst_ned / 10) / (duur_ned / 60).
EXECUTE.

RECODE verv (1=1) (14=1) (16=1) (2=2) (3=2) (4=3) (5=3) (6=3) (15=3) (17=3) (7=4) (8=4) (18=4) 
    (19=4) (20=4) (21=4) (9=5) (10=5) (11=5) (12=5) (13=6) (22=6) (23=6) (24=6).
EXECUTE.

SPSSINC CREATE DUMMIES VARIABLE=verv 
ROOTNAME1=verv 
/OPTIONS ORDER=A USEVALUELABELS=YES USEML=NO OMITFIRST=NO.

COMPUTE gem_dichth=(vert_dichth + aank_dichth) / 2.
EXECUTE.

COMPUTE gem_dichth_nieuw = gem_dichth / 1000.
COMPUTE dicht_loop = gem_dichth_nieuw * Voetganger.
COMPUTE dicht_fiets = gem_dichth_nieuw * Fiets.
COMPUTE dicht_bromf = gem_dichth_nieuw * Bromfiets.
COMPUTE dicht_ov = gem_dichth_nieuw * OV.
COMPUTE dicht_anders = gem_dichth_nieuw * Anders.
COMPUTE dicht_auto = gem_dichth_nieuw * Auto.
EXECUTE.

MISSING VALUES ethnicit (9).
MISSING VALUES inkh_s10 (99).

DESCRIPTIVES VARIABLES=leeftijd geslacht ethnicit inkh_s10 snelheid_ned gem_dichth_nieuw Voetganger 
    Fiets Bromfiets Auto OV Anders
  /STATISTICS=MEAN STDDEV MIN MAX.

MISSING VALUES dicht_loop (0).
MISSING VALUES dicht_fiets (0).
MISSING VALUES dicht_bromf (0).
MISSING VALUES dicht_auto (0).
MISSING VALUES dicht_ov (0).
MISSING VALUES dicht_anders (0).
EXECUTE.

DESCRIPTIVES VARIABLES=dicht_loop dicht_fiets dicht_bromf dicht_ov dicht_anders dicht_auto
  /STATISTICS=MEAN STDDEV MIN MAX.
