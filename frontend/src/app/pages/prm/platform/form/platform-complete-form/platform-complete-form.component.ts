import { Component, Input, OnInit } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import {
  BasicAttributeType,
  BoardingDeviceAttributeType,
  BooleanOptionalAttributeType,
} from '../../../../../api';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { CompletePlatformFormGroup } from '../platform-form-group';

@Component({
  selector: 'app-platform-complete-form',
  templateUrl: './platform-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class PlatformCompleteFormComponent implements OnInit {
  @Input() form!: FormGroup<CompletePlatformFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  basicAttributeType = Object.values(BasicAttributeType);
  boardingDeviceAttributeTypes = Object.values(BoardingDeviceAttributeType);

  constructor(private readonly translationSortingService: TranslationSortingService) {}

  ngOnInit(): void {}
}
