import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, untracked } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { AtlasInfoIconComponent, AtlasInfoLinkDirective } from '@atlas/form';
import { TranslatePipe } from '@ngx-translate/core';
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
import { WheelchairAccessibilityState } from '../../../api/model/wheelchairAccessibilityState';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export type AccessibilityType = 'STOP_POINT' | 'PLATFORM';

@Component({
  selector: 'atlas-wheelchair-accessibility',
  imports: [
    NgOptimizedImage,
    AtlasInfoIconComponent,
    TranslatePipe,
    OverlayModule,
    DateComponent,
    DialogCloseComponent,
    AtlasSpacerComponent,
    TableComponent,
    AtlasInfoLinkDirective,
  ],
  templateUrl: './wheelchair-accessibility.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./wheelchair-accessibility.component.scss'],
})
export class WheelchairAccessibilityComponent {
  private readonly wheelchairAccessibilityService = inject(WheelchairAccessibilityInternalService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly dateInputForm = new FormGroup({
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

  readonly sloid = input.required<string>();
  readonly objectType = input.required<AccessibilityType>();
  readonly editMode = input.required<boolean>();

  constructor() {
    this.dateInputForm.controls.startingFrom.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.loadWheelchairAccessibilityOnSpecifiedDate());

    effect(() => {
      if (this.editMode()) {
        this.closeOverlay();
      }
    });

    effect(() => {
      const sloid = this.sloid();
      const objectType = this.objectType();
      untracked(() => this.reloadForSloid(sloid, objectType));
    });
  }

  isOverlayOpen = false;
  wheelchairAccessibilityToday?: WheelchairAccessibilityState;
  wheelchairAccessibility: AccessibilityRow[] = [];

  private reloadForSloid(sloid: string, objectType: AccessibilityType) {
    this.closeOverlay();
    this.wheelchairAccessibility = [];
    this.loadWheelchairAccessibilityToday(sloid, objectType);
  }

  toggleOverlay() {
    this.isOverlayOpen = !this.isOverlayOpen;
    if (this.wheelchairAccessibility.length < 1) {
      this.loadWheelchairAccessibilityOnSpecifiedDate();
    }
  }

  closeOverlay() {
    this.isOverlayOpen = false;
  }

  private loadWheelchairAccessibilityToday(sloid: string, objectType: AccessibilityType) {
    let accessibilityTodayRequest;
    switch (objectType) {
      case 'STOP_POINT':
        accessibilityTodayRequest = this.wheelchairAccessibilityService.getStopPointAccessibilityToday(sloid);
        break;
      case 'PLATFORM':
        accessibilityTodayRequest = this.wheelchairAccessibilityService.getPlatformAccessibilityToday(sloid);
        break;
    }
    const isCurrent = () => this.sloid() === sloid && this.objectType() === objectType;
    accessibilityTodayRequest.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response) => {
        if (isCurrent()) {
          this.wheelchairAccessibilityToday = response.state;
        }
      },
      error: () => {
        if (isCurrent()) {
          this.wheelchairAccessibilityToday = undefined;
        }
      },
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
      const sloid = this.sloid();
      accessibilityRequest.subscribe({
        next: (response) => {
          if (this.sloid() === sloid) {
            this.wheelchairAccessibility = response.rows;
          }
        },
      });
    }
  }
}
