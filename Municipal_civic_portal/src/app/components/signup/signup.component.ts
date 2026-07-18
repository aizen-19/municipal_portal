import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Output() switchToLogin = new EventEmitter<void>();

  fullName = '';
  email = '';
  password = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';

  constructor(private apiService: ApiService) { }

  ngOnInit(): void { }

  onSignUpSubmit(event: Event): void {
    event.preventDefault();
    this.errorMessage = '';
    this.successMessage = '';

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.apiService.signup({
      fullName: this.fullName,
      email: this.email,
      password: this.password
    }).subscribe({
      next: (res) => {
        this.successMessage = res.message || 'Account registered successfully! Redirecting...';
        // Clear fields
        this.fullName = '';
        this.email = '';
        this.password = '';
        this.confirmPassword = '';
        
        // Auto switch to login modal after 1.5 seconds
        setTimeout(() => {
          this.switchToLogin.emit();
        }, 1500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to register account. Please try again.';
      }
    });
  }
}