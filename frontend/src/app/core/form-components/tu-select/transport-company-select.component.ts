import { Component, inject, OnChanges, OnDestroy, OnInit, SimpleChanges, output, input } from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TransportCompany } from '../../../api';
import { map } from 'rxjs/operators';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { TranslatePipe } from '@ngx-translate/core';
import { TransportCompanyService } from '../../../api/service/bodi/transport-company.service';

@Component({
  selector: 'atlas-tu-select',
  templateUrl: './transport-company-select.component.html',
  imports: [SearchSelectComponent, ReactiveFormsModule, AtlasLabelFieldComponent],
  providers: [TranslatePipe],
})
export class TransportCompanySelectComponent implements OnInit, OnDestroy, OnChanges {
  readonly valueExtraction = input('');
  readonly controlName = input.required<string>();
  readonly formModus = input(true);
  readonly formGroup = input.required<FormGroup>();
  readonly disabled = input(false);

  readonly selectedTransportCompanyChanged = output();
  readonly tuSelectionChanged = output<TransportCompany>();

  transportCompanies: Observable<TransportCompany[]> = of([]);
  alreadySelectedTransportCompany: TransportCompany[] = [];
  private formSubscription?: Subscription;
  private readonly transportCompanyService = inject(TransportCompanyService);

  ngOnInit(): void {
    this.init();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.formGroup) {
      if (this.formSubscription) {
        this.formSubscription.unsubscribe();
      }
      this.init();
    }
  }

  init() {
    const tuControl = this.formGroup().get(this.controlName())!;
    this.alreadySelectedTransportCompany = tuControl.value;
    this.formSubscription = tuControl.valueChanges.subscribe((change) => {
      this.alreadySelectedTransportCompany = change;
      this.selectedTransportCompanyChanged.emit(change);
      this.searchTransportCompany(change);
    });

    this.searchTransportCompany(tuControl.value as string);
  }

  searchTransportCompany(searchString: string) {
    if (searchString) {
      this.transportCompanies = this.transportCompanyService
        .getTransportCompanies([searchString], undefined, undefined, undefined, ['number,ASC'])
        .pipe(
          map((value) => {
            const transportCompaniesNotDuplicated: TransportCompany[] = [];
            value.objects?.forEach((val) => {
              if (!this.alreadySelectedTransportCompany.map((tc) => tc.id).includes(val.id)) {
                transportCompaniesNotDuplicated.push(val);
              }
            });
            return transportCompaniesNotDuplicated ?? [];
          })
        );
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }

  getDisplayText(transportCompany: TransportCompany) {
    const abbreviation = transportCompany.abbreviation ? transportCompany.abbreviation + ' - ' : '';
    return abbreviation + transportCompany.businessRegisterName;
  }
}
