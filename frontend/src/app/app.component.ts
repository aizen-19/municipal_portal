import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // Adjust extension if using scss
})
export class AppComponent {
  title = 'Municipal_civic_portal';

  constructor(private router: Router) {}

  // Returns true if the active route is the dashboard
  isDashboardRoute(): boolean {
    return this.router.url.includes('/dashboard');
  }
}