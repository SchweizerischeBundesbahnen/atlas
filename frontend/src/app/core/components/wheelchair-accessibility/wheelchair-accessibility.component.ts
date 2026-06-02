import { Component, inject, input, OnInit } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { InfoIconComponent, InfoLinkDirective } from '@atlas/form';
import { TranslatePipe } from '@ngx-translate/core';
import { ReadWheelchairAccessibility } from '../../../api';
import { ConnectedPosition, OverlayModule } from '@angular/cdk/overlay';
import { WheelchairAccessibilityInternalService } from '../../../api/service/prm/wheelchair-accessibility/wheelchair-accessibility-internal.service';
import { DateComponent } from '../../form-components/date/date.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { DialogCloseComponent } from '../dialog/close/dialog-close.component';
import { AtlasSpacerComponent } from '../spacer/atlas-spacer.component';
import { TableComponent } from '../table/table.component';
import { AccessibilityRow } from '../../../api/model/accessibilityRow';
import { TableColumn } from '../table/table-column';

export type AccessibilityType = 'STOP_POINT' | 'PLATFORM';

@Component({
  selector: 'atlas-wheelchair-accessibility',
  imports: [
    NgOptimizedImage,
    InfoIconComponent,
    TranslatePipe,
    OverlayModule,
    DateComponent,
    DialogCloseComponent,
    AtlasSpacerComponent,
    TableComponent,
    InfoLinkDirective,
  ],
  templateUrl: './wheelchair-accessibility.component.html',
  styleUrls: ['./wheelchair-accessibility.component.scss'],
})
export class WheelchairAccessibilityComponent implements OnInit {
  sloid = input.required<string>();
  objectType = input.required<AccessibilityType>();

  private readonly wheelchairAccessibilityService = inject(WheelchairAccessibilityInternalService);

  readonly dateInputForm = new FormGroup({
    startingFrom: new FormControl(moment(), [Validators.required]),
  });

  protected readonly accessibilityColumns: TableColumn<AccessibilityRow>[] = [
    {
      headerTitle: 'PRM.ACCESSIBILITY',
      value: 'accessibilityState',
      translate: {
        withPrefix: 'PRM.ENUMS.WHEELCHAIR_ACCESSIBILITY.',
      },
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'from',
      formatAsDate: true,
    },
    {
      headerTitle: 'COMMON.VALID_TO',
      value: 'to',
      formatAsDate: true,
    },
  ];

  protected readonly overlayPositionRight: ConnectedPosition[] = [
    {
      originX: 'end',
      originY: 'center',
      overlayX: 'start',
      overlayY: 'center',
      offsetX: 8,
    },
  ];
  isOverlayOpen = false;
  wheelchairAccessibilityToday?: ReadWheelchairAccessibility.StateEnum;
  wheelchairAccessibility: AccessibilityRow[] = [];

  ngOnInit(): void {
    this.loadWheelchairAccessibilityToday();
    // beim edit modus schliessen
    this.dateInputForm.controls.startingFrom.valueChanges.subscribe(() =>
      this.loadWheelchairAccessibilityOnSpecifiedDate()
    );
  }

  toggleOverlay() {
    this.isOverlayOpen = !this.isOverlayOpen;
    if (this.wheelchairAccessibility.length < 1) {
      this.loadWheelchairAccessibilityOnSpecifiedDate();
    }
  }

  close() {
    this.isOverlayOpen = false;
  }

  private loadWheelchairAccessibilityToday() {
    let accessibilityTodayRequest;
    switch (this.objectType()) {
      case 'STOP_POINT':
        accessibilityTodayRequest = this.wheelchairAccessibilityService.getStopPointAccessibilityToday(this.sloid());
        break;
      case 'PLATFORM':
        accessibilityTodayRequest = this.wheelchairAccessibilityService.getPlatformAccessibilityToday(this.sloid());
        break;
    }
    accessibilityTodayRequest.subscribe({
      next: (response) => (this.wheelchairAccessibilityToday = response.state),
    });
  }

  private loadWheelchairAccessibilityOnSpecifiedDate() {
    const startingFrom = this.dateInputForm.controls.startingFrom;
    if (startingFrom.valid) {
      const selectedDate = this.dateInputForm.controls.startingFrom.value?.toDate();
      let accessibilityRequest;
      switch (this.objectType()) {
        case 'STOP_POINT':
          accessibilityRequest = this.wheelchairAccessibilityService.getStopPointAccessibility(
            this.sloid(),
            selectedDate
          );
          break;
        case 'PLATFORM':
          accessibilityRequest = this.wheelchairAccessibilityService.getPlatformAccessibility(
            this.sloid(),
            selectedDate
          );
          break;
      }
      accessibilityRequest.subscribe({
        next: (response) => (this.wheelchairAccessibility = response.rows),
      });
    }
  }
}
