import {Component, ContentChild, Input, OnDestroy, OnInit, TemplateRef} from '@angular/core';
import {BaseDetailController} from './base-detail-controller';
import {KeepaliveService} from '../../keepalive/keepalive.service';
import {Record} from './record';
import {Subscription} from 'rxjs';
import {PermissionService} from "../../auth/permission/permission.service";
import { ScrollToTopDirective } from '../../scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../detail-page-content/detail-page-content.component';
import { NgIf, NgTemplateOutlet } from '@angular/common';
import { DateRangeTextComponent } from '../../versioning/date-range-text/date-range-text.component';
import { SwitchVersionComponent } from '../switch-version/switch-version.component';
import { UserDetailInfoComponent } from './user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-detail-wrapper [controller][headingNew]',
    templateUrl: './base-detail.component.html',
    styleUrls: ['./base-detail.component.scss'],
    imports: [ScrollToTopDirective, DetailPageContainerComponent, DetailPageContentComponent, NgIf, DateRangeTextComponent, SwitchVersionComponent, NgTemplateOutlet, UserDetailInfoComponent, DetailFooterComponent, AtlasButtonComponent, TranslatePipe]
})
export class BaseDetailComponent implements OnInit, OnDestroy {
  @Input() controller!: BaseDetailController<Record>;
  @Input() headingNew!: string;
  @Input() formDetailHeading!: string;
  selectedRecord!: Record;
  private recordSubscription!: Subscription;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('additionalInfo') additionalInfo!: TemplateRef<any>;

  constructor(
    private readonly keepaliveService: KeepaliveService,
    public permissionService: PermissionService
  ) {
    keepaliveService.startWatching(() => {
      this.controller.closeConfirmDialog();
      this.controller.backToOverview();
    });
  }

  receiveWorkflowEvent() {
    this.controller.reloadRecord();
  }

  ngOnInit(): void {
    this.selectedRecord = this.controller.record;
    this.recordSubscription = this.controller.selectedRecordChange.subscribe(
      (value) => (this.selectedRecord = value)
    );
  }

  ngOnDestroy(): void {
    this.keepaliveService.stopWatching();
    this.recordSubscription.unsubscribe();
  }

  isEditButtonVisible() {
    return (
      this.selectedRecord.status !== 'IN_REVIEW' ||
      this.permissionService.isAtLeastSupervisor(this.controller.getApplicationType())
    );
  }
}
