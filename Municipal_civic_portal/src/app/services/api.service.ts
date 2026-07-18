import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Helper to construct headers with the saved JWT token
  private getAuthHeaders(): { headers: HttpHeaders } {
    const token = sessionStorage.getItem('activePortalUserToken');
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token || ''}`
      })
    };
  }

  // Authentication
  signup(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/signup`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  getProfile(): Observable<any> {
    return this.http.get(`${this.baseUrl}/auth/me`, this.getAuthHeaders());
  }

  // Permits
  applyPermit(permitData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/permits`, permitData, this.getAuthHeaders());
  }

  getPermits(): Observable<any> {
    return this.http.get(`${this.baseUrl}/permits`, this.getAuthHeaders());
  }

  simulatePermitReview(permitId: string, status: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/permits/${permitId}/simulate-review`,
      { status },
      this.getAuthHeaders()
    );
  }

  // Complaints
  registerComplaint(complaintData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/complaints`, complaintData, this.getAuthHeaders());
  }

  getComplaints(): Observable<any> {
    return this.http.get(`${this.baseUrl}/complaints`, this.getAuthHeaders());
  }

  simulateComplaintAction(complaintId: string, status: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/complaints/${complaintId}/simulate-action`,
      { status },
      this.getAuthHeaders()
    );
  }

  // Property Tax
  payPropertyTax(taxData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/tax/pay`, taxData, this.getAuthHeaders());
  }

  getTaxPayments(): Observable<any> {
    return this.http.get(`${this.baseUrl}/tax/payments`, this.getAuthHeaders());
  }

  // AI Chat
  sendChatMessage(message: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/ai/chat`, { message });
  }
}
