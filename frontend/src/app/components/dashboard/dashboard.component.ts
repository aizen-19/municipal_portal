import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  loggedInUser: string = 'Citizen User'; 
  loggedInEmail: string = 'citizen@portal.gov.in';
  currentDateString: string = '';
  activeTab: string = 'dashboard'; // 'dashboard' | 'apply' | 'my-applications' | 'complaints' | 'property-tax' | 'profile'
  showMenu: boolean = false;
  isChatbotCollapsed: boolean = false;

  // Dashboard Stats Counts
  statTotal = 0;
  statPending = 0;
  statApproved = 0;
  statRejected = 0;

  // Permit Form Fields
  permitType: string = 'building_permit';
  permitDetails: string = '';
  permitDocName: string = '';
  permitSuccess: string = '';
  permitError: string = '';
  isSubmittingPermit = false;
  permits: any[] = [];

  // Complaint Form Fields
  complaintCategory: string = 'Roads';
  complaintSubject: string = '';
  complaintDescription: string = '';
  complaintSuccess: string = '';
  complaintError: string = '';
  isSubmittingComplaint = false;
  complaints: any[] = [];

  // Property Tax Calculator & Payment Fields
  taxZone: string = 'A';
  taxArea: number = 0;
  taxPropType: string = 'residential';
  calculatedTax: number = 0;
  
  cardNumber: string = '';
  cardExpiry: string = '';
  cardCvc: string = '';
  cardName: string = '';
  isCardFlipped = false;
  paymentSuccess = '';
  paymentError = '';
  isProcessingPayment = false;
  taxPayments: any[] = [];
  showReceipt: any = null;

  // AI Chatbot Fields
  chatInput = '';
  chatMessages: any[] = [];
  isChatTyping = false;

  constructor(private router: Router, private apiService: ApiService) { }

  ngOnInit(): void {
    // Protect route: redirect to landing if not logged in
    const savedToken = sessionStorage.getItem('activePortalUserToken');
    if (!savedToken) {
      this.router.navigate(['/']);
      return;
    }

    this.formatCurrentDate();
    this.loadActiveUser();
    this.loadAllData();

    // Welcome message for AI assistant
    this.chatMessages.push({
      sender: 'ai',
      text: 'Hello! I am your AI Civic Assistant. You can ask me anything about building permits, trade licenses, property taxes, or filing complaints. How can I help you today?',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
  }

  loadActiveUser(): void {
    const savedUser = sessionStorage.getItem('activePortalUser');
    const savedEmail = sessionStorage.getItem('activePortalUserEmail');
    if (savedUser) {
      this.loggedInUser = savedUser;
    }
    if (savedEmail) {
      this.loggedInEmail = savedEmail;
    }
  }

  formatCurrentDate(): void {
    const options: Intl.DateTimeFormatOptions = { 
      day: 'numeric', 
      month: 'long', 
      year: 'numeric', 
      weekday: 'long' 
    };
    this.currentDateString = new Date().toLocaleDateString('en-US', options);
  }

  // Load all metrics and items in background
  loadAllData(): void {
    this.apiService.getPermits().subscribe({
      next: (res) => {
        this.permits = res;
        this.updateStats();
      }
    });

    this.apiService.getComplaints().subscribe({
      next: (res) => {
        this.complaints = res;
        this.updateStats();
      }
    });

    this.apiService.getTaxPayments().subscribe({
      next: (res) => {
        this.taxPayments = res;
        this.updateStats();
      }
    });
  }

  updateStats(): void {
    const totalApplications = this.permits.length + this.complaints.length + this.taxPayments.length;
    
    const pendingPermits = this.permits.filter(p => p.status === 'Pending').length;
    const pendingComplaints = this.complaints.filter(c => c.status === 'Pending' || c.status === 'In Progress').length;
    
    const approvedPermits = this.permits.filter(p => p.status === 'Approved').length;
    const resolvedComplaints = this.complaints.filter(c => c.status === 'Resolved').length;
    const paidTaxes = this.taxPayments.length;

    const rejectedPermits = this.permits.filter(p => p.status === 'Rejected').length;

    this.statTotal = totalApplications;
    this.statPending = pendingPermits + pendingComplaints;
    this.statApproved = approvedPermits + resolvedComplaints + paidTaxes;
    this.statRejected = rejectedPermits;
  }

  switchTab(tab: string): void {
    this.activeTab = tab;
    this.showMenu = false;
    this.loadAllData();
  }

  toggleMenu(): void {
    this.showMenu = !this.showMenu;
  }

  toggleChatbot(): void {
    this.isChatbotCollapsed = !this.isChatbotCollapsed;
  }

  // Submit Permit Application
  submitPermit(): void {
    this.permitSuccess = '';
    this.permitError = '';
    this.isSubmittingPermit = true;

    const permitData = {
      type: this.permitType === 'building_permit' ? 'Building Permit' : 'Trade License',
      details: this.permitDetails,
      docName: this.permitDocName || 'blueprint_and_deed.pdf'
    };

    this.apiService.applyPermit(permitData).subscribe({
      next: (res) => {
        this.permitSuccess = 'Permit application submitted successfully! AI has queued it for review.';
        this.permitDetails = '';
        this.permitDocName = '';
        this.isSubmittingPermit = false;
        this.loadAllData();
      },
      error: (err) => {
        this.permitError = err.error?.message || 'Failed to submit application. Please try again.';
        this.isSubmittingPermit = false;
      }
    });
  }

  // Simulate Officer Approval / Rejection
  simulatePermitStatus(id: string, status: string): void {
    this.apiService.simulatePermitReview(id, status).subscribe({
      next: () => {
        this.loadAllData();
      }
    });
  }

  // File Complaint
  submitComplaint(): void {
    this.complaintSuccess = '';
    this.complaintError = '';
    this.isSubmittingComplaint = true;

    const complaintData = {
      category: this.complaintCategory,
      subject: this.complaintSubject,
      description: this.complaintDescription
    };

    this.apiService.registerComplaint(complaintData).subscribe({
      next: (res) => {
        this.complaintSuccess = 'Civic grievance registered successfully. The field team has been notified.';
        this.complaintSubject = '';
        this.complaintDescription = '';
        this.isSubmittingComplaint = false;
        this.loadAllData();
      },
      error: (err) => {
        this.complaintError = err.error?.message || 'Failed to file complaint. Please try again.';
        this.isSubmittingComplaint = false;
      }
    });
  }

  // Simulate Complaint progress
  simulateComplaintStatus(id: string, status: string): void {
    this.apiService.simulateComplaintAction(id, status).subscribe({
      next: () => {
        this.loadAllData();
      }
    });
  }

  // Property Tax Calculator
  calculateTax(): void {
    let rate = 5;
    if (this.taxZone === 'A') rate = 15;
    else if (this.taxZone === 'B') rate = 10;

    let multiplier = 1.0;
    if (this.taxPropType === 'commercial') multiplier = 1.5;

    this.calculatedTax = Math.max(0, this.taxArea) * rate * multiplier;
  }

  // Pay Property Tax
  payTaxSubmit(event: Event): void {
    event.preventDefault();
    this.paymentSuccess = '';
    this.paymentError = '';
    this.isProcessingPayment = true;

    const taxData = {
      zone: this.taxZone,
      area: this.taxArea,
      propertyType: this.taxPropType === 'residential' ? 'Residential' : 'Commercial',
      amount: this.calculatedTax,
      cardName: this.cardName
    };

    // Simulate network delay for premium card processing animation
    setTimeout(() => {
      this.apiService.payPropertyTax(taxData).subscribe({
        next: (res) => {
          this.paymentSuccess = 'Payment completed! Your digital tax receipt is ready below.';
          this.showReceipt = res.payment;
          
          // Clear inputs
          this.cardNumber = '';
          this.cardExpiry = '';
          this.cardCvc = '';
          this.cardName = '';
          this.taxArea = 0;
          this.calculatedTax = 0;
          
          this.isProcessingPayment = false;
          this.loadAllData();
        },
        error: (err) => {
          this.paymentError = err.error?.message || 'Transaction failed. Please check card info.';
          this.isProcessingPayment = false;
        }
      });
    }, 1500);
  }

  // View Receipt
  viewReceiptDetails(payment: any): void {
    this.showReceipt = payment;
  }

  // AI Chat Bot
  sendChatMessage(): void {
    const text = this.chatInput.trim();
    if (!text) return;

    this.chatMessages.push({
      sender: 'user',
      text: text,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });

    this.chatInput = '';
    this.isChatTyping = true;

    this.apiService.sendChatMessage(text).subscribe({
      next: (res) => {
        this.isChatTyping = false;
        this.chatMessages.push({
          sender: 'ai',
          text: res.reply,
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });
      },
      error: () => {
        this.isChatTyping = false;
        this.chatMessages.push({
          sender: 'ai',
          text: 'I am experiencing difficulties connecting to the portal guidelines. Please try asking again shortly.',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });
      }
    });
  }

  // File Upload Mock helper
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.permitDocName = file.name;
    }
  }

  onLogout(): void {
    sessionStorage.clear(); 
    this.router.navigate(['/']);
  }
}