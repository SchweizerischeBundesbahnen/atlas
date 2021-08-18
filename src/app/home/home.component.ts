import { Component, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService, Version } from '../api';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  versions: Observable<Array<Version>> | undefined;

  constructor(
    private authService: AuthService,
    private timetableFieldNumbersService: TimetableFieldNumbersService
  ) {}

  ngOnInit(): void {
    this.getVersions();
  }

  get loggedIn() {
    return this.authService.loggedIn;
  }

  getVersions(): void {
    this.versions = this.timetableFieldNumbersService.getVersions(0, 20);
  }
}
