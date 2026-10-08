import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { User } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationUserCreateComponent } from './create/user-administration-user-create.component';
import { UserAdministrationUserEditComponent } from './edit/user-administration-user-edit.component';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { UserPermissionGivenUserService } from './edit/user-permission-given-user.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-user-administration',
  templateUrl: './user-administration-user-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [UserAdministrationUserEditComponent, UserAdministrationUserCreateComponent],
  providers: [TranslatePipe],
})
export class UserAdministrationUserDetailComponent implements OnInit, DetailFormComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly userPermissionGivenUserService = inject(UserPermissionGivenUserService);

  readonly user = signal<User | undefined>(undefined);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.user.set(data.user);
    });
  }

  readonly dirty = computed(() => this.userPermissionGivenUserService.currentForm()?.().dirty() ?? false);
}
