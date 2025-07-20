import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: false,
  templateUrl: './footer.html',
  styleUrl: './footer.scss'
})

export class Footer implements OnInit {
  public currentYear: number;

  constructor() {
    this.currentYear = new Date().getFullYear();
  }

  ngOnInit() {
    this.currentYear = new Date().getFullYear();
    console.log(this.currentYear);
  }
}


