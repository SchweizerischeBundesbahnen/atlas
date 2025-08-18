import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { BusinessOrganisation } from '../../../api';
import { map } from 'rxjs/operators';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { BoSelectionDisplayPipe } from './bo-selection-display.pipe';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';

@Component({
  selector: 'bo-select',
  templateUrl: './business-organisation-select.component.html',
  imports: [
    SearchSelectComponent,
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    BoSelectionDisplayPipe,
  ],
})
export class BusinessOrganisationSelectComponent
  implements OnInit, OnDestroy, OnChanges
{
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() sboidsRestrictions: string[] = [];
  @Input() disabled = false;

  @Output() selectedBusinessOrganisationChanged = new EventEmitter();
  @Output() boSelectionChanged = new EventEmitter<BusinessOrganisation>();

  businessOrganisations: Observable<BusinessOrganisation[]> = of([]);
  private formSubscription!: Subscription;

  private readonly businessOrganisationService = inject(
    BusinessOrganisationService
  );

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
      this.businessOrganisations = this.businessOrganisationService
        .getAllBusinessOrganisations(
          [searchString],
          this.sboidsRestrictions,
          undefined,
          undefined,
          undefined,
          undefined,
          ['sboid,ASC']
        )
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }
}
