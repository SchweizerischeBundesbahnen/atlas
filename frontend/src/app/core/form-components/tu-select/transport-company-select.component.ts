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
    this.formSubscription = tuControl.valueChanges.subscribe((change) => {
      this.selectedTransportCompanyChanged.emit(change);
      this.searchTransportCompany(change);
    });

    this.searchTransportCompany(tuControl.value as string);
  }

  searchTransportCompany(searchString: string) {
    if (searchString) {
      this.transportCompanies = this.transportCompaniesService
        .getTransportCompanies([searchString], undefined, undefined, undefined, ['number,ASC'])
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }
}
