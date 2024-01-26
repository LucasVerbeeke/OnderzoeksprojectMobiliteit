# -*- coding: utf-8 -*-
"""
Created on Sat Jan 20 17:28:27 2024

@author: lucas
"""

import matplotlib.pyplot as plt

dlst = []
vlst_auto = []
vlst_voetganger = []
vlst_fiets = []
vlst_bromfiets = []
vlst_ov = []
vlst_overig = []
c = -0.010*41.92 - 2.393*1.51 - 1.035*1.31 + 0.342*6.44

for dichth in range(0, 12000, 1):
    dichth = dichth/1000
    dlst.append(dichth)
    vlst_auto.append(40.697 - 1.785*dichth + c)
    vlst_voetganger.append(8.420 + 0.046*dichth + c)
    vlst_fiets.append(15.011 - 0.136*dichth + c)
    vlst_bromfiets.append(22.197 - 0.702*dichth + c)
    vlst_ov.append(51.170 - 2.010*dichth + c)
    vlst_overig.append(25.375 - 1.785*dichth + c)

plt.figure()
plt.plot(dlst, vlst_auto)
plt.plot(dlst, vlst_voetganger)
plt.plot(dlst, vlst_fiets)
plt.plot(dlst, vlst_bromfiets)
plt.plot(dlst, vlst_ov)
plt.plot(dlst, vlst_overig)
plt.ylim(0,60)
plt.xlabel("Adressendichtheid (1000 adressen/km2)")
plt.ylabel("Reissnelheid (km/u)")
plt.legend(["Auto","Voetganger","Fiets","Bromfiets","Openbaar vervoer","Overig"],ncol=2)
plt.show()