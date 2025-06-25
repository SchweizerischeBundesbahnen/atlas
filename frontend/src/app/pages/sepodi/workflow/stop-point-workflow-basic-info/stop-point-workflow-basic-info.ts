import { Component, input } from '@angular/core';
import { ReadServicePointVersion } from '../../../../api';
import { AsyncPipe } from '@angular/common';
import { BoDisplayPipe } from '../../../../core/form-components/bo-select/bo-display.pipe';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { SplitServicePointNumberPipe } from '../../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';

@Component({
  selector: 'app-stop-point-workflow-basic-info',
  imports: [
    AsyncPipe,
    BoDisplayPipe,
    DisplayDatePipe,
    SplitServicePointNumberPipe,
    TranslatePipe,
    AtlasSpacerComponent,
  ],
  templateUrl: './stop-point-workflow-basic-info.html',
})
export class StopPointWorkflowBasicInfo {
  stopPoint = input.required<ReadServicePointVersion>();
}
