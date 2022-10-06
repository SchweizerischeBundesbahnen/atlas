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
import { BusinessOrganisation, BusinessOrganisationsService } from '../../../api';
import { map } from 'rxjs/operators';

@Component({
  selector: 'bo-select',
  templateUrl: './business-organisation-select.component.html',
  styleUrls: ['./business-organisation-select.component.scss'],
})
export class BusinessOrganisationSelectComponent implements OnInit, OnDestroy, OnChanges {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() sboidsRestrictions: string[] = [];

  @Output() selectedBusinessOrganisationChanged = new EventEmitter();
  @Output() boSelectionChanged = new EventEmitter<BusinessOrganisation>();

  businessOrganisations: Observable<BusinessOrganisation[]> = of([]);
  private formSubscription!: Subscription;

  constructor(private businessOrganisationsService: BusinessOrganisationsService) {}

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
    const boControl = this.formGroup.get(this.controlName)!;
    this.formSubscription = boControl.valueChanges.subscribe((change) => {
      this.selectedBusinessOrganisationChanged.emit(change);
      this.searchBusinessOrganisation(change);
    });

    this.searchBusinessOrganisation(boControl.value as string);
  }

  searchBusinessOrganisation(searchString: string) {
    if (searchString) {
      this.businessOrganisations = this.businessOrganisationsService
        .getAllBusinessOrganisations(
          [searchString],
          this.sboidsRestrictions,
          undefined,
          undefined,
          undefined,
          undefined,
          ['organisationNumber,ASC']
        )
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }
}
