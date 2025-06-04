import { Component, inject, OnInit } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';
import { PageService } from '../../core/pages/page.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NgFor, AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../app.testing.module';
import { UserService } from '../../core/auth/user/user.service';
import { User } from '../../core/auth/user/user';
import { PermissionComponent } from '../../core/components/permissions/permission.component';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  imports: [TranslatePipe, PermissionComponent],
})
export class UserProfileComponent implements OnInit {
  userService = inject(UserService);

  currentUser!: User;

  ngOnInit() {
    this.currentUser = this.userService.currentUser!;
  }
}
