import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  input,
  OnChanges,
  OnDestroy,
  OnInit,
  output,
  signal,
  SimpleChanges,
} from '@angular/core';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { Field, FormField } from '@angular/forms/signals';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { BusinessOrganisation } from '../../../api';
import { map } from 'rxjs/operators';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent, AtlasLabelFieldComponent } from '@atlas/form';
import { BoSelectionDisplayPipe } from '../../pipe/bo-selection-display.pipe';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';

type BusinessOrganisationSelectValue = BusinessOrganisation | string | null;

@Component({
  selector: 'atlas-bo-select',
  templateUrl: './business-organisation-select.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SearchSelectComponent,
    FormField,
    NgSelectComponent,
    NgLabelTemplateDirective,
    NgOptionTemplateDirective,
    AsyncPipe,
    NgTemplateOutlet,
    TranslatePipe,
    AtlasFieldErrorComponent,
    AtlasLabelFieldComponent,
    BoSelectionDisplayPipe,
  ],
})
export class BusinessOrganisationSelectComponent implements OnInit, OnDestroy, OnChanges {
  readonly valueExtraction = input('sboid');
  readonly controlName = input('businessOrganisation');
  readonly formModus = input(true);
  readonly formGroup = input<FormGroup>();
  readonly field = input<Field<BusinessOrganisationSelectValue>>();
  readonly sboidsRestrictions = input<string[]>([]);
  readonly disabled = input(false);

  readonly selectedBusinessOrganisationChanged = output<BusinessOrganisationSelectValue>();
  readonly boSelectionChanged = output<BusinessOrganisation>();

  readonly businessOrganisations = signal<Observable<BusinessOrganisation[]>>(of([]));
  protected readonly searchTriggerSubject = new Subject<string>();
  private formSubscription?: Subscription;
  private previousField?: Field<BusinessOrganisationSelectValue>;
  private previousValue: BusinessOrganisationSelectValue = null;

  private readonly businessOrganisationService = inject(BusinessOrganisationService);

  constructor() {
    this.searchTriggerSubject.pipe(takeUntilDestroyed()).subscribe((query) => this.searchBusinessOrganisation(query));
    effect(() => {
      const selectedField = this.field();
      if (!selectedField) {
        return;
      }
      const value = selectedField().value();
      if (selectedField !== this.previousField) {
        this.previousField = selectedField;
        this.previousValue = value;
      } else if (value === this.previousValue) {
        return;
      } else {
        this.previousValue = value;
        this.selectedBusinessOrganisationChanged.emit(value);
      }
      if (typeof value === 'string') {
        this.searchBusinessOrganisation(value);
      }
    });
  }

  ngOnInit(): void {
    if (this.formGroup() && !this.field()) {
      this.init();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.formGroup && !changes.formGroup.firstChange && !this.field()) {
      this.formSubscription?.unsubscribe();
      this.init();
    }
  }

  init() {
    const boControl = this.legacyForm.get(this.controlName());
    if (!boControl) {
      throw new Error(`Business organisation select requires a form control with name ${this.controlName()}`);
    }
    this.formSubscription = boControl.valueChanges.subscribe((change) => {
      this.selectedBusinessOrganisationChanged.emit(change);
      this.searchBusinessOrganisation(change);
    });
    this.searchBusinessOrganisation(boControl.value as string);
  }

  get legacyForm(): FormGroup {
    const formGroup = this.formGroup();
    if (!formGroup) {
      throw new Error('Business organisation select requires a form or signal field');
    }
    return formGroup;
  }

  searchBusinessOrganisation(searchString: string) {
    if (searchString) {
      this.businessOrganisations.set(
        this.businessOrganisationService
          .getAllBusinessOrganisations(
            [searchString],
            this.sboidsRestrictions(),
            undefined,
            undefined,
            undefined,
            undefined,
            ['sboid,ASC']
          )
          .pipe(map((value) => value.objects ?? []))
      );
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }
}
