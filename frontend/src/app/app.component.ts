import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { HomeComponent } from './components/home/home.component'; 
import { NavbarComponent } from './components/navbar/navbar.component'; 

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Municipal_civic_portal';

  // Access the child NavbarComponent directly from the template
  @ViewChild(NavbarComponent) navbarComponent!: NavbarComponent;

  constructor(private router: Router) {}

  isDashboardRoute(): boolean {
    return this.router.url.includes('/dashboard');
  }

  onRouteActivate(componentRef: any): void {
    if (componentRef instanceof HomeComponent) {
      componentRef.requestSignUp.subscribe(() => {
        if (this.navbarComponent) {
          this.navbarComponent.openModal('signup');
        }
      });
    }
  }
}