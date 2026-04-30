# Anleitung: Migration von Angular Constructor Dependency Injection zu inject()-Funktion

Diese Anleitung richtet sich an einen Programmieragenten, der Angular-Komponenten, -Services oder -Direktiven von der klassischen Constructor-Injection auf die neue inject()-Funktion migrieren soll (ab Angular 14+).

## Ziel

Statt Abhängigkeiten im Konstruktor zu deklarieren, sollen sie mit der Funktion `inject()` direkt im Klassenrumpf oder in Methoden/Funktionen bezogen werden.

---

## Schritt-für-Schritt-Anleitung

1. **Finde alle Klassen mit Constructor-Injection**
    - Suche nach Klassen, die Angular-typische Abhängigkeiten im Konstruktor deklarieren, z.B.:
      ```typescript
      constructor(private service: MyService, private readonly router: Router) {}
      ```

2. **Ersetze die Constructor-Injektion durch inject()**
    - Entferne die Abhängigkeitsparameter aus dem Konstruktor.
    - Füge stattdessen für jede Abhängigkeit eine Property-Zuweisung mit `inject()` hinzu:
      ```typescript
      private service = inject(MyService);
      private readonly router = inject(Router);
      ```
    - Die Properties können als `private` oder `readonly` deklariert werden, je nach ursprünglicher Nutzung.

3. **Passe die Nutzung im Code an**
    - Stelle sicher, dass alle Verwendungen der Abhängigkeiten weiterhin korrekt funktionieren.
    - Falls die Abhängigkeit nur im Konstruktor verwendet wurde, verschiebe die Logik in eine passende Methode und nutze dort die Property.

4. **Entferne ungenutzte Konstruktoren**
    - Wenn der Konstruktor nach der Migration leer ist, entferne ihn vollständig.

5. **Importiere inject()**
    - Stelle sicher, dass `inject` aus `@angular/core` importiert wird:
      ```typescript
      import { inject } from '@angular/core';
      ```

6. **Sonderfälle**
    - Bei Injektion von `ChangeDetectorRef`, `ElementRef` oder ähnlichen Angular-Klassen ist das Vorgehen identisch.
    - Bei Vererbung: Stelle sicher, dass die Basisklasse keinen Konstruktor mit Parametern mehr erwartet.

7. **Testen**
    - Führe die existierenen Tests mit vitest aus, um sicherzustellen, dass die Migration keine Funktionalität bricht.

---

## Beispiel

Vorher:

```typescript
import {Component} from '@angular/core';
import {MyService} from './my.service';

@Component({...})
export class MyComponent {
    constructor(private myService: MyService) {
    }

    doSomething() {
        this.myService.action();
    }
}
```

Nachher:

```typescript
import {Component, inject} from '@angular/core';
import {MyService} from './my.service';

@Component({...})
export class MyComponent {
    private myService = inject(MyService);

    doSomething() {
        this.myService.action();
    }
}
```

---

## Hinweise

- Die inject()-Funktion kann nur im Klassenrumpf (Property-Initialisierung) oder in Funktionen aufgerufen werden, nicht im Konstruktor.
- Die Migration ist ab Angular 14+ möglich und ab Angular 16 empfohlen.
- Bei Unsicherheiten: Siehe [Angular Doku zur inject()-Funktion](https://angular.io/api/core/inject).
