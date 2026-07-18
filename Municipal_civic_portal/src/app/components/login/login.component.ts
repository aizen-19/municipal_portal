import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  @Output() switchToSignUp = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();

  email = '';
  password = '';
  errorMessage = '';

  constructor(private router: Router, private apiService: ApiService) {}

  onLoginSubmit(event: Event) {
    event.preventDefault();
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter both email and password.';
      return;
    }

    this.apiService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        sessionStorage.setItem('activePortalUserToken', res.token);
        sessionStorage.setItem('activePortalUser', res.user.fullName);
        sessionStorage.setItem('activePortalUserEmail', res.user.email);
        
        this.close.emit();
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Invalid email or password. Please try again.';
      }
    });
  }
}