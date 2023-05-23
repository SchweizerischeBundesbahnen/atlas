import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { TransportCompaniesService, TransportCompany } from '../../../api';
import { map } from 'rxjs/operators';

@Component({
  selector: 'tu-select',
  templateUrl: './transport-company-select.component.html',
  styleUrls: ['./transport-company-select.component.scss'],
})
export class TransportCompanySelectComponent implements OnInit, OnDestroy, OnChanges {
  @Input() valueExtraction = '';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled!: boolean;

  @Output() selectedTransportCompanyChanged = new EventEmitter();
  @Output() ttfnSelectionChanged = new EventEmitter<TransportCompany>();

  transportCompanies: Observable<TransportCompany[]> = of([]);
  alreadySelectdTransportCompany: TransportCompany[] = [];
  private formSubscription?: Subscription;

  constructor(private transportCompaniesService: TransportCompaniesService) {}

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
    const tuControl = this.formGroup.get(this.controlName)!;
    this.alreadySelectdTransportCompany = Array.isArray(tuControl.value)
      ? tuControl.value
      : [tuControl.value];
    this.formSubscription = tuControl.valueChanges.subscribe((change) => {
      this.alreadySelectdTransportCompany = change;
      this.selectedTransportCompanyChanged.emit(change);
      this.searchTransportCompany(change);
    });

    this.searchTransportCompany(tuControl.value as string);
  }

  searchTransportCompany(searchString: string) {
    if (searchString) {
      this.transportCompanies = this.transportCompaniesService
        .getTransportCompanies([searchString], undefined, undefined, undefined, ['number,ASC'])
        .pipe(
          map((value) => {
            const transportCompaniesNotDuplicated: TransportCompany[] = [];
            value.objects?.forEach((val) => {
              if (!this.alreadySelectdTransportCompany.map((tc) => tc.id).includes(val.id)) {
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
}
