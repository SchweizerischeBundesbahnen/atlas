import { Component, inject, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../core/auth/user/user.service';
import { User } from '../../core/auth/user/user';
import { PermissionComponent } from '../../core/components/permissions/permission.component';

@Component({
  selector: 'atlas-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  imports: [TranslatePipe, PermissionComponent],
  providers: [TranslatePipe],
})
export class UserProfileComponent implements OnInit {
  private readonly userService = inject(UserService);

  currentUser!: User;

  ngOnInit() {
    this.currentUser = this.userService.currentUser!;
  }
}
