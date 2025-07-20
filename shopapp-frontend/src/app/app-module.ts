import { NgModule, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { App } from './app';
import { Home } from './home/home';
import { Header } from './header/header';
import { Footer } from './footer/footer';
import { Order } from './order/order';

@NgModule({
  declarations: [
    App,
    Home,
    Header,
    Footer,
    Order,
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideClientHydration(withEventReplay())
  ],
  bootstrap: [App]
})
export class AppModule { }
