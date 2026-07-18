import { Component, OnInit, HostListener } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  activeAuthModal: 'login' | 'signup' | null = null;
  
  // Tracks the active section string ('home', 'services', 'procedure', etc.)
  currentFragment: string = 'home'; 

  constructor() { }

  ngOnInit(): void { }

  // Manual override when a user clicks a navbar item directly
  setActiveSection(section: string): void {
    this.currentFragment = section;
  }

  // AUTOMATIC SCROLL SPY TRACKING
  @HostListener('window:scroll', [])
  onWindowScroll() {
    const sections = ['home', 'services', 'procedure', 'aboutus', 'contactus'];
    
    /* CRITICAL TOP GUARD: If the window scroll position is near the absolute top edge 
       (less than 100px from zero), immediately force the active indicator line 
       to 'home' and exit the execution function loop early. */
    if (window.scrollY < 100) {
      this.currentFragment = 'home';
      return;
    }

    // Dynamic track computation parameters for subsequent scroll sections
    const scrollPosition = window.scrollY + 200; 

    for (const sectionId of sections) {
      const element = document.getElementById(sectionId);
      if (element) {
        const top = element.offsetTop;
        const height = element.offsetHeight;

        if (scrollPosition >= top && scrollPosition < top + height) {
          this.currentFragment = sectionId;
          break;
        }
      }
    }
  }

  openModal(type: 'login' | 'signup'): void {
    this.activeAuthModal = type;
    document.body.style.overflow = 'hidden';
  }

  closeModal(): void {
    this.activeAuthModal = null;
    document.body.style.overflow = 'auto';
  }

  isUserLoggedIn(): boolean {
    return !!sessionStorage.getItem('activePortalUserToken');
  }

  onLoginSubmit(event: Event): void {
    event.preventDefault();
    this.closeModal();
  }
}