import { Component, Input, OnInit } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { BooleanOptionalAttributeType, VehicleAccessAttributeType } from '../../../../../api';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { ReducedPlatformFormGroup } from '../platform-form-group';

@Component({
  selector: 'app-platform-reduced-form',
  templateUrl: './platform-reduced-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class PlatformReducedFormComponent implements OnInit {
  @Input() form!: FormGroup<ReducedPlatformFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  vehicleAccess = Object.values(VehicleAccessAttributeType);

  constructor(private readonly translationSortingService: TranslationSortingService) {}

  ngOnInit(): void {}
}
