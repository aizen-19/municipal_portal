import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  isLoggedIn: boolean = false; 

  @Output() requestSignUp = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void { }

  onApplyPermit(): void {
    if (this.isLoggedIn) {
      alert('Access Granted! Opening digital permit application...');
    } else {
      this.requestSignUp.emit();
    }
  }
}