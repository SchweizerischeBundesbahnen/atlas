# ServicePoint Status different scenarios


## Use Case 1: Create new Stop Point
```agsl
NEU:       |________________Haltestelle_________________|
IST:
Status:

RESULTAT:  |________________Haltestelle_________________|
Status:                       DRAFT
```

## Use Case 2: Update from Service Point to Stop Point
```agsl
NEU:                                |________________Haltestelle______________________|
IST:       |__________________________Dienststelle____________________________________|
Status:                                VALIDATED

RESULTAT:  |______Dienststelle______|______________Haltestelle________________________|
Status:          VALIDATED                             DRAFT
```

## Use Case 3: Update from Service Point to Stop Point
```agsl
NEU:                                                               |________________Haltestelle____________________________|
IST:       |__________Dienststelle___________|____________Dienststelle__________________|___________Dienststelle___________|
Status:                 VALIDATED                         VALIDATED                                   VALIDATED

RESULTAT:  |__________Dienststelle___________|__Dienststelle_______|____Haltestelle_____|___Haltestelle____________________|
Status:                VALIDATED                  VALIDATED               DRAFT              DRAFT
```

## Use Case 4: Update Stop Point Change Name
```agsl
NEU:                               |_________Haltestelle B Hausen_______________|
IST:       |___________________Haltestelle A Hausen_____________________________|
Status:                             VALIDATED

RESULTAT:  |__Haltestelle A Hausen__|__________Haltestelle B Hausen_____________|
Status:          VALIDATED                           DRAFT
```

## Use Case 5: Update Stop Point Change Name
```agsl
NEU:       |________________Haltestelle B Hausen________|
IST:       |____________________Haltestelle A Hausen____________________________|
Status:                             VALIDATED

RESULTAT:  |__________Haltestelle B Hausen_____________|__Haltestelle A Hausen__|
Status:                  DRAFT                               VALIDATED
```

## Use Case 6: Update Stop Point Change Name
```agsl
NEU:                                                                             |__Haltestelle C Hausen__|
IST:       |______________Haltestelle A Hausen__________|_________________Haltestelle B Hausen____________|
Status:                    VALIDATED                                        VALIDATED

RESULTAT:  |___________Haltestelle A Hausen_____________|__Haltestelle B Hausen__|__Haltestelle C Hausen__|
Status:                  VALIDATED                              VALIDATED                DRAFT
```

## Use Case 7: Update Stop Point Change Name
```agsl
NEU:                                         |__Haltestelle C Hausen__|
IST:       |____________Haltestelle A Hausen____________|______________Haltestelle B Hausen_______________|
Status:                    VALIDATED                                     VALIDATED

RESULTAT:  |_____Haltestelle A Hausen________|__Haltestelle C Hausen__|_______Haltestelle B Hausen________|
Status:                VALIDATED                        DRAFT                    VALIDATED
```

## Use Case 8: Update Stop Point Change Name
```agsl
NEU:                                |_______Haltestelle C Hausen_______|
IST:       |_______________________________Haltestelle A Hausen___________________________________________|
Status:                                       VALIDATED

RESULTAT:  |__Haltestelle A Hausen__|_______Haltestelle C Hausen_______|________Haltestelle A Hausen______|
Status:          VALIDATED                       DRAFT                              VALIDATED
```

## Use Case 9: Update Stop Point Change Name
```agsl
NEU:       |__________________________Haltestelle C Hausen________________________________________________|
IST:       |__________________________Haltestelle A Hausen________________________________________________|
Status:                                 VALIDATED

RESULTAT:  |__________________________Haltestelle C Hausen________________________________________________|
Status:                                   DRAFT
```

## Use Case 10: Update Stop Point Change Name and some other Property
```agsl
NEU:                                                                                             |________Haltestelle B Hausen + Category 3_______|
IST:       |________________Haltestelle A Hausen + Category 1________|________________Haltestelle A Hausen + Category 2___________________________|
Status:                         VALIDATED                                                      VALIDATED

RESULTAT:  |_____________Haltestelle A Hausen + Category 1___________|_HS A Hausen + Category 2__|________Haltestelle B Hausen + Category 3 ______|
Status:                           VALIDATED                                    VALIDATED                           DRAFT
```

## Use Case 11: Update Stop Point Change Name and some other Property
```agsl
NEU:                                              |_______________________________Haltestelle B Hausen + Koordinaten 3 _______________________________________________|
IST:       |_______________________Haltestelle A Hausen + Koordinaten 1__________________|_____________________Haltestelle A Hausen + Koordinaten 2___________________|
Status:                                 VALIDATED                                                                          VALIDATED

RESULTAT:  |_Haltestelle A Hausen + Koordinaten 1_|_Haltestelle B Hausen + Koordinaten 3_|___________________Haltestelle A Hausen + Koordinaten 2_____________________|
Status:                  VALIDATED                               DRAFT                                                 DRAFT
```

## Use Case 12: Update Stop Point Change Name and extend Validity
```agsl
NEU:                                                                                                      |__Verlängerung & Wechseln C Hausen__|

IST:       |______________Haltestelle A Hausen__________|__________________Haltestelle B Hausen___________|
Status:                      VALIDATED                                         VALIDATED

RESULTAT:  |______________Haltestelle A Hausen__________|________________Haltestelle B Hausen______________|__Verlängerung & Wechseln C Hausen__|
Status:                       VALIDATED                                         VALIDATED                                DRAFT
```

## Use Case 13: Update Stop Point Change Name and extend Validity
```agsl
NEU:       |__Verlängerung & Wechseln C Hausen__|

IST:                                            |_____________Haltestelle A Hausen___________|________________Haltestelle B Hausen_____________|
Status:                                                        VALIDATED                                         VALIDATED

RESULTAT:  |__Verlängerung & Wechseln C Hausen__|_____________Haltestelle A Hausen___________|_________________Haltestelle B Hausen_____________|
Status:                 DRAFT                                  VALIDATED                                          VALIDATED
```

## Use Case 14: Update Stop Point extend Validity without name change
```agsl
NEU:                                                                                                      |__Verlängerung B Hausen__|

IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
Status:                       VALIDATED                                          VALIDATED

RESULTAT:  |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen__________________________________|
Status:                       VALIDATED                                          VALIDATED
```

## Use Case 15: Update Stop Point extend Validity without name change
```agsl
NEU:       |__Verlängerung A Hausen__|

IST:                                 |_____________Haltestelle A Hausen___________|________________Haltestelle B Hausen_____________|
Status:                                               VALIDATED                                         VALIDATED

RESULTAT:  |______________________Haltestelle A Hausen____________________________|_________________Haltestelle B Hausen____________|
Status:                              VALIDATED                                                           VALIDATED
```

## Use Case 16: Update Stop Point extend Validity over another version without name change
```agsl
NEU:                                               |_______________________Wechsel zu B Hausen____________|

IST:       |________________Haltestelle A Hausen________|________________Haltestelle B Hausen_____________|
Status:                        VALIDATED                                    VALIDATED

RESULTAT:  |________________Haltestelle A Hausen___|_____________________Haltestelle B Hausen_____________|
Status:                         VALIDATED                                   VALIDATED
```

## Use Case 17: Update Stop Point extend Validity over another version without name change
```agsl
NEU:       |_______________________Wechsel zu A Hausen____________|

IST:       |________________Haltestelle A Hausen________|_______________Haltestelle B Hausen______________|
Status:                        VALIDATED                                    VALIDATED

RESULTAT:  |________________Haltestelle A Hausen__________________|___________Haltestelle B Hausen________|
Status:                         VALIDATED                                        VALIDATED
```

## Use Case 18: Update Stop Point, introduce version again with name change and a gap with previous version
```agsl
NEU:                                                                    |________________Wiedereinführung & Wechsel zu B Hausen____________|

IST:       |___________Haltestelle A Hausen_____________|
Status:                    VALIDATED

RESULTAT:  |___________Haltestelle A Hausen_____________|               |________________________Haltestelle B Hausen______________________|
Status:                    VALIDATED                                                                  DRAFT
```

## Use Case 19: Update Stop Point, introduce version again without name change and a gap with previous version
```agsl
NEU:                                                                    |________________Wiedereinführung ohne Wechsel____________|

IST:       |_____________Haltestelle A Hausen___________|
Status:                      VALIDATED

RESULTAT:  |_____________Haltestelle A Hausen____________|              |__________________Haltestelle A Hausen___________________|
Status:                      VALIDATED                                                            DRAFT
```
